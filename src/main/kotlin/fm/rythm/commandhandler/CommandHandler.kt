package fm.rythm.commandhandler

import fm.rythm.commandhandler.textcommands.*
import fm.rythm.commandhandler.utils.recursivelyFindCommandUsed
import net.dv8tion.jda.api.entities.Message

enum class CommandHandlerResult {
    SUCCESS,
    NO_PREFIX,

    /**
     * For when the message has a prefix, but it cannot be a command.
     */
    NOT_COMMAND,

    /**
     * For when the message is a valid command, but the command name isn't found.
     */
    COMMAND_NOT_FOUND,
    INVALID_PARAMETERS,
    EXECUTION_ERROR,

    /**
     * For when the command was valid, but the user wasn't permitted to use it.
     */
    FORBIDDEN
}

class CommandHandler(
    private val prefixes: ArrayList<String>,
    private val commands: ArrayList<TextCommand<*>>,
    private val onError: (() -> Boolean)?
) {

    fun handleJdaMessage(message: Message): Pair<CommandHandlerResult, TextCommand<*>?> {
        val content = message.contentRaw
        val prefixUsed = findPrefixUsed(prefixes, content) ?: return Pair(CommandHandlerResult.NO_PREFIX, null)
        val possibleCommand = isPossibleCommand(prefixUsed, content)

        if (!possibleCommand)
            return Pair(CommandHandlerResult.NOT_COMMAND, null)

        val commandUsed = content.recursivelyFindCommandUsed(
            prefixUsed.length,
            commands
        ) ?: return Pair(CommandHandlerResult.COMMAND_NOT_FOUND, null)

        val commandNameUsed = getCommandName(prefixUsed.length, content)
        val rawParameterString = getRawParameters(prefixUsed.length, commandNameUsed, content)
        val parametersRegex = compileParameterRegex(commandUsed.getParameters())
        val rawParameterValues = extractParameters(
            rawParameterString,
            commandUsed.getParameters(),
            parametersRegex
        ) ?: return Pair(CommandHandlerResult.INVALID_PARAMETERS, null)

        return try {
            val wasPermitted = commandUsed.execute(rawParameterValues, message)

            if (!wasPermitted)
                Pair(CommandHandlerResult.FORBIDDEN, commandUsed)
            else
                Pair(CommandHandlerResult.SUCCESS, commandUsed)
        } catch (e: Exception) {
            val exceptionHandled = this.onError?.let { it() } ?: throw e

            if (!exceptionHandled)
                throw e

            Pair(CommandHandlerResult.EXECUTION_ERROR, null)
        }
    }
}