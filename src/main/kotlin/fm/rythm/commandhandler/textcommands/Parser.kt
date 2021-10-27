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
        .trim()
}
