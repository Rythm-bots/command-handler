package commands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

class TextCommandContext<Parameters>(
    val channel: TextChannel,
    val jda: JDA,
    val author: Member,
    val contentRaw: String,
    val parameters: Parameters
)