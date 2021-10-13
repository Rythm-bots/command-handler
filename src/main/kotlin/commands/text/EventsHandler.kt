package commands.text

import classes.CheckFailedException
import classes.InvalidParametersException
import events.classes.EventHandler
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import utils.sendSafe

fun errorHandler(error: Exception, message: Message, command: TextCommand<*>): Boolean {
    val channel = message.channel

    if (error !is CheckFailedException && error !is InvalidParametersException)
    {
        channel.sendSafe("An error occurred whilst executing your command.").queue()
        return false
    }

    if (error is InvalidParametersException)
    {
        val messageBuilder = MessageBuilder()
            .setContent(error.message!!)
            .setEmbed(command.generateEmbed(0xe6435e).build())

        channel.sendSafe(messageBuilder).queue()
        return true
    }

    channel.sendSafe(error.message!!).queue()
    return true
}

class CommandsEventsHandler(private val registry: Registry) : EventHandler("commands") {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        tryDecodeAndExecute(registry, ::errorHandler, event.message)
    }
}