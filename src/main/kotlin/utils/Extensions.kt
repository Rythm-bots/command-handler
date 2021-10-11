package utils

import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction

enum class DiscordTimestampStyle(val token: String) {
    ShortTime("t"),
    LongTime("T"),
    ShortDate("d"),
    LongDate("D"),
    ShortDateTime("f"),
    LongDateTime("F"),
    Relative("R")
}

fun MessageChannel.sendSafe(messageBuilder: MessageBuilder): MessageAction {
    val message = messageBuilder.setAllowedMentions(emptyList()).build()

    return this.sendMessage(message)
}

fun Long.asDiscordTimestamp(style: DiscordTimestampStyle = DiscordTimestampStyle.ShortDateTime): String {
    return "<t:${this}:${style.token}>"
}

fun String.truncateIfNecessary(maxLength: Int = 800): String {
    if (this.length > maxLength)
        return "${this.slice(IntRange(0, maxLength))}... (+${this.length - maxLength})"

    return this
}