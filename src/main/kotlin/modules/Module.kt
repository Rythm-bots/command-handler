package modules

import commands.text.TextCommand
import commands.text.Registry
import events.classes.EventHandler
import events.classes.Registration
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

        Registration.registerGenericEvents(
            jda,
            *handlers
        )
    }

    protected fun registerCommands(vararg textCommand: TextCommand<*>) {
        if (disabled)
            return

        registry.register(*textCommand)
    }
}