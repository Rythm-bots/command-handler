package modules.test.commands

import commands.TextCommand
import commands.TextCommandContext
import commands.TextCommandParameter
import commands.TextCommandParameterType
import net.dv8tion.jda.api.entities.Message
import utils.promiseToBeOfType
import utils.sendSafe

data class TestCommandMultipleParameters(
    val numbers: ArrayList<Long>,
    val text: String
)

class TestCommandMultiple : TextCommand<TestCommandMultipleParameters>(
    "test-multiple",
    linkedMapOf(
        "numbers" to TextCommandParameter(TextCommandParameterType.INT, null, true),
        "text" to TextCommandParameter(TextCommandParameterType.STRING)
    ),
    hashMapOf()
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): TestCommandMultipleParameters {
        val numbers = paramsParsed["numbers"]!!.promiseToBeOfType<ArrayList<Long>>()
        val text = paramsParsed["text"] as String

        return TestCommandMultipleParameters(numbers, text)
    }

    override fun check(context: TextCommandContext<TestCommandMultipleParameters>): Boolean {
        return true
    }

    override fun handler(context: TextCommandContext<TestCommandMultipleParameters>) {
        val numbers = context.parameters.numbers
        val text = context.parameters.text

        context.channel.sendSafe("numbers: $numbers; text: $text")
            .queue()
    }
}