package fm.rythm.commandhandler.textcommands

import net.dv8tion.jda.api.entities.Message

fun getCommand(commands: ArrayList<TextCommand<*>>, commandName: String): TextCommand<*>? {
    return commands.find { it.getNames().contains(commandName) }
}

fun <Parameters> commandContextFactory(message: Message, parameters: Parameters): CommandContext<Parameters> {
    return CommandContext(message, parameters)
}

fun moduleContextFactory(command: TextCommand<*>, message: Message): ModuleContext {
    return ModuleContext(message, command)
}

/**
 * Returns the command used. Accounts for subcommands.
 *
 * @return The command if found, else null.
 */
fun String.recursivelyFindCommandUsed(
    prefixLength: Int,
    commands: ArrayList<TextCommand<*>>
): TextCommand<*>? {
    val commandName = getCommandName(prefixLength, this)
    val foundCommand = getCommand(commands, commandName) ?: return null

    if (foundCommand.getSubcommandRegistry().size == 0)
        return foundCommand

    val rawParameters = getRawParameters(prefixLength, commandName, this)

    return rawParameters.recursivelyFindCommandUsed(0, foundCommand.getSubcommandRegistry()) ?: foundCommand
}