package commands

import net.dv8tion.jda.api.entities.Message
import utils.Environment

class TextCommandRegistry {
    private val commands = hashMapOf<String, TextCommand<*>>()

    fun register(textCommand: TextCommand<*>) {
        commands[textCommand.name] = textCommand
    }

    fun tryDecodeAndExecute(message: Message) {
        val content = message.contentRaw

        val usedPrefix = Environment.PREFIXES.find find@{ prefix ->
            return@find message.contentRaw.startsWith(prefix)
        } ?: return

        val contentWithoutPrefix = content.slice(IntRange(usedPrefix.length, content.length - 1))
        val commandSplit = contentWithoutPrefix.split(' ', limit = 2)
        val commandName = commandSplit[0]
        val commandRightHandSide = if (commandSplit.size > 1) commandSplit[1] else ""

        val command = commands[commandName] ?: return
        command.execute(commandRightHandSide, message)
    }
}