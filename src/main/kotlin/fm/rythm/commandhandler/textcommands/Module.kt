package fm.rythm.commandhandler.textcommands

abstract class Module(val enabled: Boolean) {
    abstract fun check(context: ModuleContext): Boolean
}