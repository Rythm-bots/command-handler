package fm.rythm.commandhandler.examples.basic.modules.test

import fm.rythm.commandhandler.textcommands.Registry
import fm.rythm.commandhandler.classes.Module
import fm.rythm.commandhandler.examples.basic.modules.test.commands.*

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