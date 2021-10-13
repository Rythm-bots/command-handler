package commands

import net.dv8tion.jda.api.entities.Message
import utils.Environment

/**
 * This method is to be called on every incoming message the bot sees in a guild.
 * It will throw no error if no command is found, however, should a command be found,
 * and it's error is not handled, it will throw the error that occurred.
 * An error is considered unhandled if the onError callback is null, if it returns false,
 * or it itself throws an error.
 *
 * @throws Exception any unhandled exception as described above.
 */
fun tryDecodeAndExecute(
    commandRegistry: TextCommandRegistry,
    onError: ((e: Exception, m: Message, command: TextCommand<*>) -> Boolean)?,
    message: Message
) {
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
    val command = commandRegistry.findByTrigger(commandName) ?: return
    command.execute(commandName, commandRightHandSide, message, onError)
}