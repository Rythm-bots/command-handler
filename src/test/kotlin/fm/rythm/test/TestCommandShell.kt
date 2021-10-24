package fm.rythm.test

import fm.rythm.commandhandler.textcommands.Context
import fm.rythm.commandhandler.textcommands.PreParseContext
import fm.rythm.commandhandler.textcommands.TextCommand
import net.dv8tion.jda.api.entities.Message

class TestCommandShell(name: String) : TextCommand<Unit>(
    arrayListOf(name)
) {

    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): Unit {
        return
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<Unit>) {

    }
}