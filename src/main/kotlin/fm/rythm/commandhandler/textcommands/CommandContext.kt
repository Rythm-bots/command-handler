package fm.rythm.commandhandler.textcommands

import net.dv8tion.jda.api.entities.Message

data class CommandContext<Parameters>(
    val message: Message,
    private val parameters: Parameters
) : BaseContext(message) {
    val getParameters: Parameters
		get() { return parameters }
}