package fm.rythm.commandhandler.textcommands

fun getCommand(commands: ArrayList<TextCommand>, commandName: String): TextCommand? {
    return commands.find { it.getNames().contains(commandName) }
}