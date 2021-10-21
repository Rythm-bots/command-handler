package examples.dummyModeration.modules.basic.commands

import commands.text.Context
import commands.text.PreParseContext
import commands.text.TextCommand
import examples.dummyModeration.EmojiPing
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import utils.sendSafe

class PingCommand : TextCommand<Unit>(
    arrayListOf("ping")
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>) {
        return
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<Unit>) {
        val channel = context.channel
        val jda = context.jda
        val gatewayPing = jda.gatewayPing

        val embed = EmbedBuilder()
            .setColor(0x7afa9c)
            .setTitle("$EmojiPing Ping")
            .addField("Gateway", "${gatewayPing}ms", false)
            .build()

        val messageBuilder = MessageBuilder()
            .setEmbed(embed)

        channel.sendSafe(messageBuilder).queue()
    }
}