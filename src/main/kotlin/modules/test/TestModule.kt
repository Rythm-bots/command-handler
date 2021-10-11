package modules.test

import commands.TextCommandRegistry
import modules.Module
import modules.test.commands.TestCommand

class TestModule(registry: TextCommandRegistry) : Module("automod", registry) {
    init {
        this.registerCommand(TestCommand())
    }
}