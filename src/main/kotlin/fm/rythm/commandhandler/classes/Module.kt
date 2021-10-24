package fm.rythm.commandhandler.classes

import fm.rythm.commandhandler.textcommands.PreParseContext
import fm.rythm.commandhandler.textcommands.TextCommand
import fm.rythm.commandhandler.textcommands.Registry
import net.dv8tion.jda.api.JDA
import fm.rythm.commandhandler.utils.Environment

open class Module(name: String, private val registry: Registry) {
    private val disabled: Boolean = !Environment.ENABLED_MODULES.contains(name)

    /**
     * For unit testing purposes.
     */
    open fun getRegistry(): Registry {
        return registry
    }

    /**
     * For unit testing purposes.
     */
    open fun getDisabled(): Boolean {
        return disabled
    }

    fun registerEventsIfEnabled(
        jda: JDA,
        vararg handlers: EventHandler
    ) {
        if (getDisabled())
            return

        registerGenericEvents(
            jda,
            *handlers
        )
    }

    open fun check(context: PreParseContext): Boolean {
        return true
    }

    fun registerCommands(vararg textCommand: TextCommand<*>) {
        if (getDisabled())
            return

        textCommand.forEach {
                command -> command.moduleCheck = ::check
        }

        getRegistry().register(*textCommand)
    }
}