package modules.test.commands

import commands.*
import net.dv8tion.jda.api.entities.Message

data class TestCommandErrorParameters(val type: Long)

class TestCommandError : TextCommand<TestCommandErrorParameters>(
    arrayListOf("error"),
    linkedMapOf(
        "type" to TextCommandParameter(TextCommandParameterType.INT)
    ),
    hashMapOf()
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): TestCommandErrorParameters {
        val type = paramsParsed["type"]!! as Long

        return TestCommandErrorParameters(type)
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: TextCommandContext<TestCommandErrorParameters>) {
        throw Exception("Test Error")
    }
}