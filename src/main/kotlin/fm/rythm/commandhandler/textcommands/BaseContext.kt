package fm.rythm.commandhandler.textcommands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

open class BaseContext(private val message: Message) {
    val author: Member
        get() { return message.member!! }

    val channel: TextChannel
        get() { return message.textChannel }

    val guild: Guild
        get() { return message.guild }

    val jda: JDA
        get() { return message.jda }
}