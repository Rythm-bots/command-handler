package commands

import net.dv8tion.jda.api.entities.Message

abstract class TextCommand(
    val name: String,
    private val parameters: HashMap<String, TextCommandParameter>,
    private val subCommands: HashMap<String, TextCommand>
) {
    abstract fun check(context: TextCommandContext): Boolean

    abstract fun handler(context: TextCommandContext)

    private fun buildValidationRegex(): String {
        val tokens = arrayListOf<String>()

        parameters.forEach { (name, value) ->
            tokens.add(
                when(value) {
                    TextCommandParameter.INT -> "(?<$name>\\d+)"
                    TextCommandParameter.STRING -> "(?<$name>.+)"
                    else -> ""
                }
            )
        }

        return tokens.joinToString(" ", "^", "$")
    }

    private fun gatherParameters(rightHandSide: String): HashMap<String, Any>? {
        val pattern = buildValidationRegex().toRegex()

        if (!pattern.matches(rightHandSide))
            return null

        val groups = pattern.find(rightHandSide)!!.groups

        val hashMap = hashMapOf<String, Any>()
        parameters.forEach { (name, type) ->
            val value = groups[name]!!.value

            var typedValue: Any = value
            if (type === TextCommandParameter.INT)
                typedValue = value.toLong()

            hashMap[name] = typedValue
        }

        return hashMap
    }

    fun execute(rightHandSide: String, message: Message) {
        val parameters = gatherParameters(rightHandSide)
            ?: return /* TODO: reporting to the user the params are invalid */
        val context = TextCommandContext(
            message.textChannel,
            message.member!!,
            message.contentRaw,
            parameters
        )

        if (!check(context))
            return

        val rightHandSideSplit = rightHandSide.split(Regex("\\s+"), limit = 2)
        if (rightHandSideSplit.isNotEmpty())
        {
            val potentialSubCommandName = rightHandSideSplit[0]
            val command = subCommands[potentialSubCommandName]
            if (command !== null)
            {
                command.execute(rightHandSideSplit[1], message)
                return
            }
        }

        handler(context)
    }
}