package fm.rythm.commandhandler.textcommands

abstract class TextCommand(private val names: ArrayList<String>) {
    private val registry = arrayListOf<TextCommand>()

    open fun getNames(): ArrayList<String> {
        return names
    }

    open fun getRegistry(): ArrayList<TextCommand> {
        return registry
    }

    abstract fun execute()
}