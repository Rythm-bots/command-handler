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