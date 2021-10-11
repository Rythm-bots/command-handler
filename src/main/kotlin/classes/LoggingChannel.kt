package classes

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction
import utils.sendSafe

open class LoggingChannel(
    jda: JDA,
    channelId: String
) {
    private val channel: TextChannel = jda.getTextChannelById(channelId)!!

    protected fun send(messageBuilder: MessageBuilder): MessageAction {
        return channel.sendSafe(messageBuilder)
    }
}