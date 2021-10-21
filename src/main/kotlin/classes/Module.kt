package classes

import commands.text.PreParseContext
import commands.text.TextCommand
import commands.text.Registry
import events.classes.EventHandler
import events.classes.registerGenericEvents
import net.dv8tion.jda.api.JDA
import utils.Environment

open class Module(name: String, private val registry: Registry) {
    private val disabled: Boolean = !Environment.ENABLED_MODULES.contains(name)

    protected fun registerEventsIfEnabled(
        jda: JDA,
        vararg handlers: EventHandler
    ) {
        if (disabled)
            return

        registerGenericEvents(
            jda,
            *handlers
        )
    }

    open fun check(context: PreParseContext): Boolean {
        return true
    }

    protected fun registerCommands(vararg textCommand: TextCommand<*>) {
        if (disabled)
            return

        textCommand.forEach { command -> command.moduleCheck = ::check }

        registry.register(*textCommand)
    }
}