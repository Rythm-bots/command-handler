package commands

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

class TextCommandContext(
    val channel: TextChannel,
    val author: Member,
    val contentRaw: String,
    val parameters: HashMap<String, Any>
)