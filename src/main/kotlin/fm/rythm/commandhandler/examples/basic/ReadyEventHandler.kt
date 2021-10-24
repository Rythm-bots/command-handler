package fm.rythm.commandhandler.examples.basic

import fm.rythm.commandhandler.textcommands.Registry
import fm.rythm.commandhandler.classes.EventHandler
import fm.rythm.commandhandler.examples.basic.modules.test.TestModule
import net.dv8tion.jda.api.events.ReadyEvent

class ReadyEventHandler(private val registry: Registry) : EventHandler("ready") {
    // TODO: can happen multiple times, limit to 1 execution!
    override fun onReady(event: ReadyEvent) {
        val selfUser = event.jda.selfUser;
        val name = selfUser.name;
        val discriminator = selfUser.discriminator;

        println("Logged on to account '$name#$discriminator'")
    }
}