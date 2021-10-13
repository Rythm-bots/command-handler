package commands

import classes.CommandTriggerConflictException
import net.dv8tion.jda.api.entities.Message
import utils.Environment
import utils.promiseToBeOfType

class TextCommandRegistry(private val onError: ((e: Exception, m: Message, command: TextCommand<*>) -> Boolean)?) {
    private val commands = arrayListOf<TextCommand<*>>()

    /**
     * Find command by specified trigger.
     *
     * @return The text command if one exists by the specified trigger, otherwise null.
     */
    private fun findByTrigger(trigger: String): TextCommand<*>? {
        return commands.find { command ->
            return@find command.triggers.contains(trigger)
        }
    }

    /**
     * Register specified commands.
     *
     * @throws CommandTriggerConflictException
     *         if one of the registered commands' triggers conflicts with already registered ones.
     */
    fun register(vararg textCommands: TextCommand<*>) {
        // Look for conflicting triggers before registering the command,
        // throw CommandTriggerConflictException if one exists.
        textCommands.forEach { command ->
            val triggers = command.triggers
            val conflictingTrigger = triggers.find {
                    trigger -> findByTrigger(trigger) != null
            } ?: return@forEach

            throw CommandTriggerConflictException(conflictingTrigger)
        }

        // Register commands all specified commands.
        commands.addAll(textCommands)
    }


    /**
     * This method is to be called on every incoming message the bot sees in a guild.
     * It will throw no error if no command is found, however, should a command be found,
     * and it's error is not handled, it will throw the error that occurred.
     * An error is considered unhandled if the onError callback is null, if it returns false,
     * or it itself throws an error.
     *
     * @throws Exception any unhandled exception as described above.
     */
    fun tryDecodeAndExecute(message: Message) {
        val content = message.contentRaw

        /*
        See if a prefix was used, assign it to usedPrefix if so.
        If not, return from this function.
         */
        val usedPrefix = Environment.PREFIXES.find find@{ prefix ->
            return@find message.contentRaw.startsWith(prefix)
        } ?: return

        val contentWithoutPrefix = content.slice(IntRange(usedPrefix.length, content.length - 1))
        val commandSplit = contentWithoutPrefix.split(' ', limit = 2)
        val commandName = commandSplit[0]
        val commandRightHandSide = if (commandSplit.size > 1) commandSplit[1] else ""

        // Try and find the command by the trigger, if no such command is found, return.
        val command = findByTrigger(commandName) ?: return
        try {
            command.execute(commandName, commandRightHandSide, message)
        } catch (e: Exception) {
            val result = onError?.let {
                it(e, message, command)
            }

            // if handler is null or false, throw error again
            // because the error wasn't marked as handled
            if (result != true)
                throw e
        }
    }
}