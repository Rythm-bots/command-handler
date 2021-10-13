package modules.test

import commands.TextCommandRegistry
import modules.Module
import modules.test.commands.*

class TestModule(registry: TextCommandRegistry) : Module("test", registry) {
    init {
        this.registerCommands(
            TestCommand(),
            TestCommandError(),
            TestCommandMember(),
            TestCommandMultiple(),
            TestCommandNoParams(),
            TestCommandMultiTrigger(),
            TestCommandWithSubcommands()
        )
    }
}