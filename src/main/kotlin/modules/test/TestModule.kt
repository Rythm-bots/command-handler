package modules.test

import commands.text.Registry
import modules.Module
import modules.test.commands.*

class TestModule(registry: Registry) : Module("test", registry) {
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