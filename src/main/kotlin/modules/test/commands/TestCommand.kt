package modules.test.commands

import commands.TextCommand
import commands.TextCommandContext
import commands.TextCommandParameter
import commands.TextCommandParameterType
import net.dv8tion.jda.api.entities.Message
import utils.sendSafe

data class TestCommandParameters(val number: Long, val text: String, val anotherNumber: Long)

class TestCommand : TextCommand<TestCommandParameters>(
    "test",
    linkedMapOf(
        "number" to TextCommandParameter(TextCommandParameterType.INT),
        "text" to TextCommandParameter(TextCommandParameterType.STRING),
        "anotherNumber" to TextCommandParameter(TextCommandParameterType.INT)
    ),
    hashMapOf()
) {

    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): TestCommandParameters {
        val number = paramsParsed["number"] as Long
        val text = paramsParsed["text"] as String
        val anotherNumber = paramsParsed["anotherNumber"] as Long

        return TestCommandParameters(number, text, anotherNumber)
    }

    override fun check(context: TextCommandContext<TestCommandParameters>): Boolean {
        return true
    }

    override fun handler(context: TextCommandContext<TestCommandParameters>) {
        val number = context.parameters.number
        val text = context.parameters.text
        val anotherNumber = context.parameters.anotherNumber

        context.channel.sendSafe("number: $number; text: $text; anotherNumber: $anotherNumber")
            .queue()
    }
}