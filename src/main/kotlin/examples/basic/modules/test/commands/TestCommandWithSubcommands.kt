package examples.basic.modules.test.commands

import commands.text.*
import net.dv8tion.jda.api.entities.Message
import utils.sendSafe

data class IsEvenOrOddParameters(val number: Long)

class SubcommandIsEvenOrOdd : TextCommand<IsEvenOrOddParameters>(
    arrayListOf("is-even", "even", "is-odd", "odd"),
    linkedMapOf(
        "number" to TextCommandParameter(TextCommandParameterType.INT)
    )
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): IsEvenOrOddParameters {
        val number = paramsParsed["number"]!! as Long

        return IsEvenOrOddParameters(number)
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<IsEvenOrOddParameters>) {
        val number = context.parameters.number
        val isEven = number % 2 == 0L

        if (context.triggerUsed == "is-odd" || context.triggerUsed == "odd")
        {
            context.channel.sendSafe("$number odd? ${!isEven}").queue()
            return
        }

        context.channel.sendSafe("$number even? $isEven").queue()
    }
}

data class DivideParameters(val number: Long, val otherNumber: Long)

class SubcommandDivide : TextCommand<DivideParameters>(
    arrayListOf("divide", "div", "/"),
    linkedMapOf(
        "number" to TextCommandParameter(TextCommandParameterType.INT),
        "otherNumber" to TextCommandParameter(TextCommandParameterType.INT)
    )
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): DivideParameters {
        val number = paramsParsed["number"]!! as Long
        val otherNumber = paramsParsed["otherNumber"]!! as Long

        return DivideParameters(number, otherNumber)
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<DivideParameters>) {
        val number = context.parameters.number
        val otherNumber = context.parameters.otherNumber

        if (otherNumber == 0L)
        {
            context.channel.sendSafe("Cannot divide by zero").queue()
            return
        }

        val result = number / otherNumber
        context.channel.sendSafe("$number / $otherNumber = $result").queue()
    }
}

class TestCommandWithSubcommands : TextCommand<Unit>(
    arrayListOf("test-subcommands", "tsc"),
    linkedMapOf(),
    arrayListOf(
        SubcommandIsEvenOrOdd(),
        SubcommandDivide()
    )
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>) {
        return
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<Unit>) {
        context.channel.sendSafe("This is the base command speaking.").queue()
    }
}