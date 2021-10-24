package fm.rythm.test

import fm.rythm.commandhandler.textcommands.*
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

data class TestCommandParameters(val p1: Long, val p2: String, val p3: User)

open class TestCommand(names: ArrayList<String>) : TextCommand<TestCommandParameters>(
    names,
    linkedMapOf(
        "p1" to TextCommandParameter(TextCommandParameterType.INT),
        "p2" to TextCommandParameter(TextCommandParameterType.STRING),
        "p3" to TextCommandParameter(TextCommandParameterType.USER)
    ),
    arrayListOf(
        TestCommand(arrayListOf("subcommand"))
    )
) {

    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): TestCommandParameters {
        val p1 = paramsParsed["p1"] as Long
        val p2 = paramsParsed["p2"] as String
        val p3 = paramsParsed["p3"] as Long

        val user = message.jda.getUserById(p3)!!

        return TestCommandParameters(p1, p2, user)
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<TestCommandParameters>) {

    }
}