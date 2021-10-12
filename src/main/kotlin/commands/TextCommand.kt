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
        val groups = parameters.map { (name, value) ->
            val type = value.type
            val multiple = value.allowMultiple

            val pattern = type.pattern

            if (!multiple)
                return@map "(?<$name>$pattern)"

            return@map """(?<$name>(?:$pattern|\s)+)"""
        }

        return groups.joinToString("\\s+", "^", "$")
    }

    private fun applyTypeToParameter(
        type: TextCommandParameterType,
        value: String
    ): Any {
        var typedValue: Any = value
        if (type === TextCommandParameterType.INT)
            typedValue = value.toLong()

        return typedValue
    }

    private fun gatherParameters(rightHandSide: String): HashMap<String, Any>? {
        val pattern = buildValidationRegex().toRegex()

        if (!pattern.matches(rightHandSide))
            return null

        val groups = pattern.find(rightHandSide)!!.groups

        val hashMap = hashMapOf<String, Any>()
        parameters.forEach { (name, param) ->
            val type = param.type
            val multiple = param.allowMultiple
            val value = groups[name]!!.value

            if (multiple) {
                val patternInstance = "(${type.pattern})".toRegex()

                val values = arrayListOf<Any>()

                patternInstance.findAll(value).forEach { match ->
                    val group = match.groups[0]

                    values.add(
                        applyTypeToParameter(type, group!!.value)
                    )
                }

                hashMap[name] = values
                return@forEach
            }

            val typedValue = applyTypeToParameter(type, value)
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