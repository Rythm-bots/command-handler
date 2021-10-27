package fm.rythm.commandhandler.utils

import fm.rythm.commandhandler.textcommands.TextCommand
import fm.rythm.commandhandler.textcommands.getCommand
import fm.rythm.commandhandler.textcommands.getCommandName
import fm.rythm.commandhandler.textcommands.getRawParameters

// TODO: move to CommandUtils.kt
/**
 * Returns the command used. Accounts for subcommands.
 *
 * @return The command if found, else null.
 */
fun recursivelyFindCommandUsed(
    prefixLength: Int,
    commands: ArrayList<TextCommand<*>>,
    content: String
): TextCommand<*>? {
    val commandName = getCommandName(prefixLength, content)
    val foundCommand = getCommand(commands, commandName) ?: return null

    if (foundCommand.getRegistry().size == 0)
        return foundCommand

    val rawParameters = getRawParameters(prefixLength, commandName, content)

    return recursivelyFindCommandUsed(0, foundCommand.getRegistry(), rawParameters) ?: foundCommand
}