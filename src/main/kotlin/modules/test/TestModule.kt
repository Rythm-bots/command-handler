package modules.test

import commands.TextCommandRegistry
import modules.Module
import modules.test.commands.TestCommand
import modules.test.commands.TestCommandMultiple

class TestModule(registry: TextCommandRegistry) : Module("automod", registry) {
    init {
        this.registerCommand(TestCommand())
        this.registerCommand(TestCommandMultiple())
    }
}