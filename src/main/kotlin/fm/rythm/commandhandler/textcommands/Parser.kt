package fm.rythm.commandhandler.textcommands

/**
 * Will try to find the prefix used out of the specified
 * list of prefixes.
 *
 * @return Prefix used, null if not found.
 */
fun findPrefixUsed(prefixes: ArrayList<String>, content: String): String? {
    return prefixes.find {
            prefix -> content.startsWith(prefix)
    }
}

/**
 * Determine, given the prefix that was used, if a given message could be
 * a command. This does **not** verify the existence of the aforementioned command.
 *
 * @return Whether the message follows the expected structure of a command.
 */
fun isPossibleCommand(prefixUsed: String, content: String): Boolean {
    // Make sure the command message content is longer than just the prefix.
    // Of course, any message that is just a prefix can't be considered a command.
    return content.length > prefixUsed.length
}

/**
 * At the point of executing this function, you should already be
 * sure the message is using a prefix AND that the message is a correctly
 * formatted command. The existence of this command does not need to be known
 * at the time of execution.
 *
 * @return The name of the command used.
 */
fun getCommandName(prefixLength: Int, content: String): String {
    val contentNoPrefix = content.removeRange(0, prefixLength)

    return contentNoPrefix.takeWhile { it != ' ' }
}

/**
 * Retrieve the portion of the content that could be the parameters of a command.
 *
 * @return The potential parameters.
 */
fun getRawParameters(prefixLength: Int, commandName: String, content: String): String {
    return content
        .removeRange(0, prefixLength + commandName.length)
}

/**
 * Compile given parameters into named regex groups.
 *
 * @see getRawParameters
 * @return Regex to match against raw parameters.
 *         Can and should be used to extract group values as well.
 */
fun compileParameterRegex(parameters: LinkedHashMap<String, Parameter>): Regex {
    val keys = parameters.keys
    val values = parameters.values.toTypedArray()
    
    val regex = keys.mapIndexed { index, s ->
        val value = values[index]
        val type = value.type
        val allowMultiple = value.allowMultiple

        if (allowMultiple)
            return@mapIndexed """(?<$s>(?:${type.regex}|\s)+)"""

        return@mapIndexed """(?<$s>${type.regex})"""
    }.joinToString("""\s+""", "^", "$")

    return Regex(regex)
}

fun applyTypeToParameter(type: ParameterType, value: String): Any {
    return when (type) {
        ParameterType.TEXT -> value
        ParameterType.INT -> value.toInt()
        ParameterType.LONG -> value.toLong()
        ParameterType.USER -> value.toLong()
    }
}

fun extractParameters(
    rawParameters: String,
    parameters: LinkedHashMap<String, Parameter>,
    regex: Regex
): HashMap<String, Any>? {
    val result = regex.find(rawParameters) ?: return null
    val groups = result.groups
    val typedParameters = hashMapOf<String, Any>()

    parameters.forEach { (name, parameter) ->
        val type = parameter.type
        val allowMultiple = parameter.allowMultiple
        val group = groups[name]!!.value
        val parameterRegex = type.regex.toRegex()

        val values = parameterRegex.findAll(group).map { result ->
            val valueGroup = result.groups[1]!!.value

            return@map applyTypeToParameter(type, valueGroup)
        }.toList()

        if (allowMultiple)
        {
            typedParameters[name] = values
            return@forEach
        }

        typedParameters[name] = values[0]
    }

    return typedParameters
}