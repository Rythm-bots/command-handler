package fm.rythm.commandhandler.textcommands

import net.dv8tion.jda.api.entities.Message

data class ModuleContext(
    val message: Message,
    val command: TextCommand<*>
) : BaseContext(message)