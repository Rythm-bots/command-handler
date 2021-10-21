package examples.basic.modules.test

import commands.text.Registry
import classes.Module
import examples.basic.modules.test.commands.*

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