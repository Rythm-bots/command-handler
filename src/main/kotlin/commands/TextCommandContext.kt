package commands

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

class TextCommandContext<Parameters>(
    channel: TextChannel,
    jda: JDA,
    author: Member,
    contentRaw: String,
    val parameters: Parameters,
    triggerUsed: String,
) : PreParseContext(
    channel,
    jda,
    author,
    contentRaw,
    triggerUsed
)