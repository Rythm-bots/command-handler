package events.classes

import net.dv8tion.jda.api.hooks.ListenerAdapter
import utils.Environment

open class EventHandler(name: String) : ListenerAdapter() {
    val disabled: Boolean

    init {
        disabled = Environment.DISABLED_EVENTS.contains(name)
    }
}