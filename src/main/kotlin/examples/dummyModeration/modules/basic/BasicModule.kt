package examples.dummyModeration.modules.basic

import classes.Module
import commands.text.PreParseContext
import commands.text.Registry
import examples.dummyModeration.modules.basic.commands.PingCommand
import examples.dummyModeration.modules.basic.commands.UserInfoCommand
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