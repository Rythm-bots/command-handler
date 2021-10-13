package modules.test.commands

import commands.text.PreParseContext
import commands.text.TextCommand
import commands.text.Context
import net.dv8tion.jda.api.entities.Message
import utils.sendSafe

class TestCommandMultiTrigger : TextCommand<Unit>(
    arrayListOf("trigger1", "trigger2", "trigger3")
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>) {
        return
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<Unit>) {
        context.channel.sendSafe("Trigger used: ${context.triggerUsed}")
            .queue()
    }

}