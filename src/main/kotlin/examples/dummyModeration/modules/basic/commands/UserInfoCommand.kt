package examples.dummyModeration.modules.basic.commands

import commands.text.*
import examples.dummyModeration.EmojiFailure
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import utils.sendSafe

data class UserInfoCommandParameters(
    val member: Member?
)

class UserInfoCommand : TextCommand<UserInfoCommandParameters>(
    arrayListOf("ui", "i", "info", "userinfo", "whois"),
    linkedMapOf(
        "member" to TextCommandParameter(TextCommandParameterType.USER)
    )
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): UserInfoCommandParameters {
        val member = message.guild.getMemberById(paramsParsed["member"]!! as Long)

        return UserInfoCommandParameters(member)
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<UserInfoCommandParameters>) {
        val channel = context.channel
        val member = context.parameters.member
        val author = context.author

        if (member == null)
        {
            channel.sendSafe("$EmojiFailure Member not found").queue()
            return
        }

        val embed = EmbedBuilder()
            .setTitle("User Info")
            .setColor(member.color)
            .setAuthor(author.user.asTag, null, author.user.avatarUrl)
            .addField("Account Created", author.user.timeCreated.toString(), true)
            .build()

        val messageBuilder = MessageBuilder()
            .setEmbed(embed)

        channel.sendSafe(messageBuilder).queue()
    }

}