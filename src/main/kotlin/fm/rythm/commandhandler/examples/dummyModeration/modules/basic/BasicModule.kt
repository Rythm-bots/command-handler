package fm.rythm.commandhandler.examples.dummyModeration.modules.basic

import fm.rythm.commandhandler.classes.Module
import fm.rythm.commandhandler.textcommands.PreParseContext
import fm.rythm.commandhandler.textcommands.Registry
import fm.rythm.commandhandler.examples.dummyModeration.modules.basic.commands.PingCommand
import fm.rythm.commandhandler.examples.dummyModeration.modules.basic.commands.UserInfoCommand
import net.dv8tion.jda.api.Permission

class BasicModule(registry: Registry) : Module("basic", registry) {
    override fun check(context: PreParseContext): Boolean {
        val member = context.author

        return member.hasPermission(Permission.MESSAGE_MANAGE)
    }

    init {
        registerCommands(
            PingCommand(),
            UserInfoCommand()
        )
    }
}