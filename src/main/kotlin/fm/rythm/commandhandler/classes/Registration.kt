package fm.rythm.commandhandler.classes

import net.dv8tion.jda.api.JDA

fun registerGenericEvents(jda: JDA, vararg handlers: EventHandler) {
    val enabledHandlers = handlers.filter { handler -> !handler.getIfDisabled() }

    jda.addEventListener(*enabledHandlers.toTypedArray())
}