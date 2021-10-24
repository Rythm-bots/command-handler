package fm.rythm.commandhandler.textcommands

import fm.rythm.commandhandler.classes.CheckFailedException
import fm.rythm.commandhandler.classes.InvalidParametersException
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message

abstract class TextCommand<Parameters>(
    val triggers: ArrayList<String>,
    private val parameters: LinkedHashMap<String, TextCommandParameter> = linkedMapOf(),
    subCommands: ArrayList<TextCommand<*>> = arrayListOf()
) : Registry() {
    private val pattern = buildValidationRegex(parameters).toRegex()
    internal var moduleCheck: ((context: PreParseContext) -> Boolean)? = null

    internal open fun getPattern(): Regex {
        return pattern
    }

    internal open fun getParameters(): LinkedHashMap<String, TextCommandParameter> {
        return parameters
    }

    companion object {
        internal fun buildValidationRegex(parameters: LinkedHashMap<String, TextCommandParameter>): String {
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

        internal fun preParseContext(
            message: Message,
            commandNameUsed: String
        ): PreParseContext {
            return PreParseContext(
                message.textChannel,
                message.jda,
                message.member!!,
                message.contentRaw,
                commandNameUsed
            )
        }
    }

    init {
        subCommands.forEach { subCommand ->
            register(subCommand)
        }
    }

    abstract fun parameterBuilder(message: Message, paramsParsed: HashMap<String, Any>): Parameters

    /**
     * Has a lower priority than the module check.
     */
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

        getParameters().forEach { param ->
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
        if (!getPattern().matches(rightHandSide))
            return null

        val groups = getPattern().find(rightHandSide)!!.groups

        val hashMap = hashMapOf<String, Any>()
        getParameters().forEach { (name, param) ->
            val type = param.type
            val multiple = param.allowMultiple
            val value = groups[name]!!.value

            val patternInstance = type.pattern.toRegex()
            val values = arrayListOf<Any>()

            patternInstance.findAll(value).forEach { match ->
                val group = match.groups[1]

                values.add(
                    applyTypeToParameter(type, group!!.value)
                )
            }

            if (multiple) {
                hashMap[name] = values
                return@forEach
            }

            hashMap[name] = values[0]
        }

        return hashMap
    }

    private fun executeUnsafe(
        rightHandSide: String,
        message: Message,
        onError: ((e: Exception, m: Message, command: TextCommand<*>) -> Boolean)?,
        preParseContext: PreParseContext
    ) {
        val moduleCheckPassed = moduleCheck?.let { it(preParseContext) } ?: true
        val commandCheckPassed = check(preParseContext)
        if (!moduleCheckPassed || !commandCheckPassed)
            throw CheckFailedException()

        val rightHandSideSplit = rightHandSide.split(Regex("\\s+"), limit = 2)
        if (rightHandSideSplit.isNotEmpty())
        {
            val potentialSubCommandName = rightHandSideSplit[0]
            val command = findByTrigger(potentialSubCommandName)
            val subCommandRightHandSide = if (rightHandSideSplit.size > 1) rightHandSideSplit[1] else ""
            if (command !== null)
            {
                val subCommandPreParseContext = preParseContext(message, potentialSubCommandName)
                command.execute(
                    subCommandRightHandSide,
                    message,
                    onError,
                    subCommandPreParseContext
                )
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
        rightHandSide: String,
        message: Message,
        onError: ((e: Exception, m: Message, command: TextCommand<*>) -> Boolean)?,
        preParseContext: PreParseContext
    ) {
        try {
            executeUnsafe(rightHandSide, message, onError, preParseContext)
        } catch (e: Exception) {
            val result = onError?.let { it(e, message, this) }

            if (result == false || result == null)
                throw e
        }
    }
}