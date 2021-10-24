package fm.rythm.commandhandler.textcommands

import fm.rythm.commandhandler.classes.CommandTriggerConflictException
import fm.rythm.test.TestCommandShell
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class RegistryTest {
    @Test
    fun findByTrigger() {
        // Create our registry.
        val registry = Registry()

        // At this point, the registry should be empty.
        assertEquals(registry.commands.size, 0)
        assertNull(registry.findByTrigger("test-1"))

        /*
         Make single command and register it.
         We're going to be registering manually, bypassing the register method.
         This is necessary as register isn't guaranteed to perform as expected at this
         point in testing.
         */
        val commandOne = TestCommandShell("test-1")
        registry.commands.add(commandOne)

        // The registry should contain exactly one command, and it should be the aforementioned one.
        assertEquals(registry.commands.size, 1)
        assertEquals(registry.commands[0], commandOne)

        // The findByTrigger method should now find our registered command.
        assertNotNull(registry.findByTrigger("test-1"))
    }

    @Test
    fun register() {
        // Create our registry.
        val registry = Registry()

        // At this point, the registry should be empty.
        assertEquals(registry.commands.size, 0)

        // Make single command and register it.
        val commandOne = TestCommandShell("test-1")
        registry.register(commandOne)

        // The registry should contain exactly one command, and it should be the aforementioned one.
        assertEquals(registry.commands.size, 1)
        assertEquals(registry.commands[0], commandOne)

        // Make & register multiple commands using varargs.
        val commandTwo = TestCommandShell("test-2")
        val commandThree = TestCommandShell("test-3")
        registry.register(commandTwo, commandThree)

        // At this point we should have 3 registered commands (test-1, test-2, test-3).
        assertEquals(registry.commands.size, 3)
        assertEquals(registry.commands[0], commandOne)
        assertEquals(registry.commands[1], commandTwo)
        assertEquals(registry.commands[2], commandThree)

        // This operation should not succeed. It should throw a CommandTriggerConflictException exception.
        // It should not succeed because registering two commands with conflicting triggers should fail.
        assertThrows<CommandTriggerConflictException> {
            registry.register(commandOne)
        }
    }
}