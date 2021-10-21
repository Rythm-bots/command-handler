package fm.rythm.commandhandler.textcommands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

open class PreParseContext(
    val channel: TextChannel,
    val jda: JDA,
    val author: Member,
    val contentRaw: String,
    val triggerUsed: String
) {
    fun <Parameters> constructTextCommandContext(parameters: Parameters): Context<Parameters> {
        return Context(
            channel,
            jda,
            author,
            contentRaw,
            parameters,
            triggerUsed
        )
    }
}