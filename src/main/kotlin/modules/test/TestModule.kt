package modules.test

import commands.TextCommandRegistry
import modules.Module
import modules.test.commands.*

class TestModule(registry: TextCommandRegistry) : Module("automod", registry) {
    init {
        this.registerCommand(TestCommand())
        this.registerCommand(TestCommandMultiple())
        this.registerCommand(TestCommandMember())
        this.registerCommand(TestCommandNoParams())
        this.registerCommand(TestCommandError())
    }
}