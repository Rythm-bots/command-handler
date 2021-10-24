package fm.rythm.commandhandler.classes

import net.dv8tion.jda.api.hooks.ListenerAdapter
import fm.rythm.commandhandler.utils.Environment

open class EventHandler(name: String) : ListenerAdapter() {
    private val disabled = Environment.DISABLED_EVENTS.contains(name)

    /**
     * Open for mocking purposes
     */
    open fun getIfDisabled(): Boolean {
        return disabled
    }
}