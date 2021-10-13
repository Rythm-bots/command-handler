package modules.test.commands

import commands.*
import net.dv8tion.jda.api.entities.Message

class TestCommandWithSubcommands : TextCommand<Unit>(
    arrayListOf("test-subcommands", "tsc"),
    linkedMapOf(
        "someText" to TextCommandParameter(TextCommandParameterType.STRING)
    ),
    hashMapOf()
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun check(context: PreParseContext): Boolean {
        TODO("Not yet implemented")
    }

    override fun handler(context: TextCommandContext<Unit>) {
        TODO("Not yet implemented")
    }
}