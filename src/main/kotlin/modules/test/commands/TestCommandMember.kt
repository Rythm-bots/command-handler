package modules.test.commands

import commands.text.*
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import utils.promiseToBeOfType
import utils.sendSafe

data class TestCommandMemberParameters(val members: List<Member?>, val note: String)

class TestCommandMember : TextCommand<TestCommandMemberParameters>(
    arrayListOf("tags"),
    linkedMapOf(
        "members" to TextCommandParameter(TextCommandParameterType.USER, "The members you'd like the tags of", true),
        "note" to TextCommandParameter(
            TextCommandParameterType.STRING,
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        )
    )
) {
    override fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): TestCommandMemberParameters {
        val members = paramsParsed["members"]!!
            .promiseToBeOfType<ArrayList<Long>>()
            .map { id -> message.guild.getMemberById(id) }
        val note = paramsParsed["note"] as String

        return TestCommandMemberParameters(members, note)
    }

    override fun check(context: PreParseContext): Boolean {
        return true
    }

    override fun handler(context: Context<TestCommandMemberParameters>) {
        val members = context.parameters.members.map { m -> m?.user?.asTag }
        val note = context.parameters.note

        context.channel.sendSafe("members: $members; note: $note").queue()
    }


}