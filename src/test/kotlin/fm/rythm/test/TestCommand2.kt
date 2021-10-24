package fm.rythm.test

import fm.rythm.commandhandler.textcommands.*
import net.dv8tion.jda.api.entities.Message

class TestCommand2(names: ArrayList<String>) : TextCommand<Unit>(
    names,
    linkedMapOf(
        "p1" to TextCommandParameter(TextCommandParameterType.INT),
        "p2" to TextCommandParameter(TextCommandParameterType.STRING),
        "p3" to TextCommandParameter(TextCommandParameterType.USER)
    )
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