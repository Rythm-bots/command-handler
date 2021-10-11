package events.classes

import modules.Module
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter

class Registration {
    companion object {
        /**
         *  Helper function that registers event listeners
         *  if and only if they are enabled.
         *  Event listeners are enabled by default, and disabled using the DISABLED_EVENTS environment variable.
         *  To disable an event, simply add it to the comma separated list of DISABLED_EVENTS.
         */
        fun registerGenericEvents(jda: JDA, vararg handlers: EventHandler) {
            val enabledHandlers = handlers.filter { handler -> !handler.disabled }

            jda.addEventListener(*enabledHandlers.toTypedArray())
        }
    }
}