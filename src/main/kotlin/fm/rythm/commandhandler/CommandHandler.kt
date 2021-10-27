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
    EXECUTION_ERROR
}

class CommandHandler(
    private val prefixes: ArrayList<String>,
    private val commands: ArrayList<TextCommand>,
    private val onError: (() -> Boolean)?
) {

    fun handleJdaMessage(message: Message): Pair<CommandHandlerResult, TextCommand?> {
        val content = message.contentRaw
        val prefixUsed = findPrefixUsed(prefixes, content) ?: return Pair(CommandHandlerResult.NO_PREFIX, null)
        val possibleCommand = isPossibleCommand(prefixUsed, content)

        if (!possibleCommand)
            return Pair(CommandHandlerResult.NOT_COMMAND, null)

        val commandUsed = recursivelyFindCommandUsed(
            prefixUsed.length,
            commands,
            content
        ) ?: return Pair(CommandHandlerResult.COMMAND_NOT_FOUND, null)

        return try {
            commandUsed.execute()
            Pair(CommandHandlerResult.SUCCESS, commandUsed)
        } catch (e: Exception) {
            val exceptionHandled = this.onError?.let { it() } ?: throw e

            if (!exceptionHandled)
                throw e

            Pair(CommandHandlerResult.EXECUTION_ERROR, null)
        }
    }
}