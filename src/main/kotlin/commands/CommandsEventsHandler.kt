package commands

import events.classes.EventHandler
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class CommandsEventsHandler(private val registry: TextCommandRegistry) : EventHandler("commands") {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        registry.tryDecodeAndExecute(event.message)
    }
}