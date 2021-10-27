package fm.rythm.commandhandler.textcommands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel

data class CommandContext<Parameters>(
    private val message: Message,
    private val parameters: Parameters
) {
    fun getAuthor(): Member {
        return message.member!!
    }

    fun getChannel(): TextChannel {
        return message.textChannel
    }

    fun getMessage(): Message {
        return message
    }

    fun getGuild(): Guild {
        return message.guild
    }

    fun getJda(): JDA {
        return message.jda
    }

    fun getParameters(): Parameters {
        return parameters
    }
}