package commands.text

import classes.CheckFailedException
import classes.InvalidParametersException
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message

abstract class TextCommand<Parameters>(
    val triggers: ArrayList<String>,
    private val parameters: LinkedHashMap<String, TextCommandParameter> = linkedMapOf(),
    subCommands: ArrayList<TextCommand<*>> = arrayListOf()
) : Registry() {
    private val pattern = buildValidationRegex().toRegex()

    init {
        subCommands.forEach { subCommand ->
            register(subCommand)
        }
    }

    abstract fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): Parameters

    abstract fun check(context: PreParseContext): Boolean

    abstract fun handler(context: Context<Parameters>)

    fun generateEmbed(color: Int): EmbedBuilder {
        val embedBuilder = EmbedBuilder()

        embedBuilder.setTitle(triggers.joinToString(", "))
        embedBuilder.setColor(color)

        if (this.commands.size > 0)
        {
            val subCommandsNames = this.commands
                .joinToString("\n") {command ->
                    command.triggers.joinToString(", ", prefix = "• ")
                }
            embedBuilder.addField("Subcommands", subCommandsNames, false)
        }

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

    private fun executeUnsafe(
        commandNameUsed: String,
        rightHandSide: String,
        message: Message,
        onError: ((e: Exception, m: Message, command: TextCommand<*>) -> Boolean)?
    ) {
        val preParseContext = PreParseContext(
            message.textChannel,
            message.jda,
            message.member!!,
            message.contentRaw,
            commandNameUsed
        )

        if (!check(preParseContext))
            throw CheckFailedException()

        val rightHandSideSplit = rightHandSide.split(Regex("\\s+"), limit = 2)
        if (rightHandSideSplit.isNotEmpty())
        {
            val potentialSubCommandName = rightHandSideSplit[0]
            val command = findByTrigger(potentialSubCommandName)
            val subCommandRightHandSide = if (rightHandSideSplit.size > 1) rightHandSideSplit[1] else ""
            if (command !== null)
            {
                command.execute(potentialSubCommandName, subCommandRightHandSide, message, onError)
                return
            }
        }

        val parameters = gatherParameters(rightHandSide)
            ?: throw InvalidParametersException()
        val constructedParameters = parameterBuilder(message, parameters)
        val context = preParseContext.constructTextCommandContext(constructedParameters)


        handler(context)
    }

    fun execute(
        commandNameUsed: String,
        rightHandSide: String,
        message: Message,
        onError: ((e: Exception, m: Message, command: TextCommand<*>) -> Boolean)?
    ) {
        try {
            executeUnsafe(commandNameUsed, rightHandSide, message, onError)
        } catch (e: Exception) {
            val result = onError?.let { it(e, message, this) }

            if (result == false || result == null)
                throw e
        }
    }
}