package fm.rythm.commandhandler.examples.basic.modules.test.commands

import fm.rythm.commandhandler.textcommands.*
import net.dv8tion.jda.api.entities.Message
import fm.rythm.commandhandler.utils.sendSafe

data class TestCommandParameters(val number: Long, val text: String, val anotherNumber: Long)

class TestCommand : TextCommand<TestCommandParameters>(
    arrayListOf("test"),
    linkedMapOf(
        "number" to TextCommandParameter(TextCommandParameterType.INT),
        "text" to TextCommandParameter(TextCommandParameterType.STRING),
        "anotherNumber" to TextCommandParameter(TextCommandParameterType.INT)
    )
) {

    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): TestCommandParameters {
        val number = paramsParsed["number"] as Long
        val text = paramsParsed["text"] as String
        val anotherNumber = paramsParsed["anotherNumber"] as Long

        return TestCommandParameters(number, text, anotherNumber)
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<TestCommandParameters>) {
        val number = context.parameters.number
        val text = context.parameters.text
        val anotherNumber = context.parameters.anotherNumber

        context.channel.sendSafe("number: $number; text: $text; anotherNumber: $anotherNumber")
            .queue()
    }
}