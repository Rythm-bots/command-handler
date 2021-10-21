package fm.rythm.commandhandler.examples.basic.modules.test.commands

import fm.rythm.commandhandler.textcommands.PreParseContext
import fm.rythm.commandhandler.textcommands.TextCommand
import fm.rythm.commandhandler.textcommands.Context
import net.dv8tion.jda.api.entities.Message
import fm.rythm.commandhandler.utils.sendSafe

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