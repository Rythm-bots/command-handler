package modules.test.commands

import commands.TextCommand
import commands.TextCommandContext
import commands.TextCommandParameter
import commands.TextCommandParameterType
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import utils.promiseToBeOfType
import utils.sendSafe

data class TestCommandMemberParameters(val members: List<Member?>, val note: String)

class TestCommandMember : TextCommand<TestCommandMemberParameters>(
    "tags",
    linkedMapOf(
        "members" to TextCommandParameter(TextCommandParameterType.USER, true),
        "note" to TextCommandParameter(TextCommandParameterType.STRING)
    ),
    hashMapOf()
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): TestCommandMemberParameters {
        val members = paramsParsed["members"]!!
            .promiseToBeOfType<ArrayList<Long>>()
            .map { id -> message.guild.getMemberById(id) }
        val note = paramsParsed["note"] as String

        return TestCommandMemberParameters(members, note)
    }

    override fun check(context: TextCommandContext<TestCommandMemberParameters>): Boolean {
        return true
    }

    override fun handler(context: TextCommandContext<TestCommandMemberParameters>) {
        val members = context.parameters.members.map { m -> m?.user?.asTag }
        val note = context.parameters.note

        context.channel.sendSafe("members: $members; note: $note").queue()
    }


}