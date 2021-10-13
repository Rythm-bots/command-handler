package commands

import classes.CheckFailedException
import classes.InvalidParametersException
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message

abstract class TextCommand<Parameters>(
    val triggers: ArrayList<String>,
    private val parameters: LinkedHashMap<String, TextCommandParameter>,
    private val subCommands: HashMap<String, TextCommand<*>>
) {
    private val pattern = buildValidationRegex().toRegex()

    abstract fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): Parameters

    abstract fun check(context: TextCommandContext<Parameters>): Boolean

    abstract fun handler(context: TextCommandContext<Parameters>)

    fun generateEmbed(color: Int): EmbedBuilder {
        val embedBuilder = EmbedBuilder()

        embedBuilder.setTitle(triggers.joinToString(", "))
        embedBuilder.setColor(color)

        parameters.forEach { param ->
            val amountLimit = if (param.value.amountLimit == Int.MAX_VALUE)
                "∞"
            else
                param.value.amountLimit

            val range = if (param.value.allowMultiple)
                "(min: 1; max: $amountLimit)"
            else
                ""

            embedBuilder.addField(
                "${param.key} $range",
                "${param.value.type.humanReadable} — ${param.value.description}",
                true
            )
        }

        return embedBuilder
    }

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
        val typedValue = when (type) {
            TextCommandParameterType.INT,
            TextCommandParameterType.USER -> value.toLong()
            TextCommandParameterType.STRING -> value
        }

        return typedValue
    }

    private fun gatherParameters(rightHandSide: String): HashMap<String, Any>? {
        if (!pattern.matches(rightHandSide))
            return null

        val groups = pattern.find(rightHandSide)!!.groups

        val hashMap = hashMapOf<String, Any>()
        parameters.forEach { (name, param) ->
            val type = param.type
            val multiple = param.allowMultiple
            val value = groups[name]!!.value

            if (multiple) {
                val patternInstance = type.pattern.toRegex()

                val values = arrayListOf<Any>()

                patternInstance.findAll(value).forEach { match ->
                    val group = match.groups[1]

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

    fun execute(commandNameUsed: String, rightHandSide: String, message: Message) {
        val parameters = gatherParameters(rightHandSide)
            ?: throw InvalidParametersException()
        val constructedParameters = parameterBuilder(message, parameters)
        val context = TextCommandContext(
            message.textChannel,
            message.jda,
            message.member!!,
            message.contentRaw,
            constructedParameters,
            commandNameUsed
        )

        if (!check(context))
            throw CheckFailedException()

        val rightHandSideSplit = rightHandSide.split(Regex("\\s+"), limit = 2)
        if (rightHandSideSplit.isNotEmpty())
        {
            val potentialSubCommandName = rightHandSideSplit[0]
            val command = subCommands[potentialSubCommandName]
            if (command !== null)
            {
                command.execute(potentialSubCommandName, rightHandSideSplit[1], message)
                return
            }
        }

        handler(context)
    }
}