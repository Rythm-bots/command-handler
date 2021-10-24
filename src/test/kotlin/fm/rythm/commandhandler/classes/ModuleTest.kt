package fm.rythm.commandhandler.classes

import fm.rythm.commandhandler.textcommands.Registry
import fm.rythm.commandhandler.textcommands.TextCommand
import fm.rythm.test.TestCommandShell
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.hooks.EventListener
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

internal class ModuleTest {
    @Test
    fun registerEventsIfEnabled() {
        val jdaMocked = mock<JDA> {}
        val moduleDisabled = mock<Module> {
            on { getDisabled() } doReturn true
        }
        val eventHandlerMocked = mock<EventHandler> {
            on { getIfDisabled() } doReturn false
        }
        moduleDisabled.registerEventsIfEnabled(jdaMocked, eventHandlerMocked)

        verify(jdaMocked, times(0)).addEventListener(arrayOf<EventListener>())

        val moduleEnabled = mock<Module> {
            on { getDisabled() } doReturn false
        }
        moduleEnabled.registerEventsIfEnabled(jdaMocked, eventHandlerMocked)

        verify(jdaMocked, times(1)).addEventListener(eventHandlerMocked)
    }

    @Test
    fun registerCommands() {
        val commandsEnabled = arrayListOf<TextCommand<*>>()
        val mockedRegistryForEnabled = mock<Registry> {
            on { getCommandsList() } doReturn commandsEnabled
        }
        val moduleEnabled = mock<Module> {
            on { getDisabled() } doReturn false
            on { getRegistry() } doReturn mockedRegistryForEnabled
        }

        val testCommandOne = TestCommandShell("test-1")
        val testCommandTwo = TestCommandShell("test-2")
        val testCommandThree = TestCommandShell("test-3")

        moduleEnabled.registerCommands(
            testCommandOne,
            testCommandTwo,
            testCommandThree
        )

        val registryEnabled = mockedRegistryForEnabled.getCommandsList()

        assertEquals(registryEnabled.size, 3)
        assertEquals(registryEnabled[0], testCommandOne)
        assertEquals(registryEnabled[1], testCommandTwo)
        assertEquals(registryEnabled[2], testCommandThree)

        assertEquals(registryEnabled[0].moduleCheck, moduleEnabled::check)

        // Test disabled modules

        val commandsDisabled = arrayListOf<TextCommand<*>>()
        val mockedRegistryForDisabled = mock<Registry> {
            on { getCommandsList() } doReturn commandsDisabled
        }

        mock<Module> {
            on { getDisabled() } doReturn true
            on { getRegistry() } doReturn mockedRegistryForDisabled
        }

        val registryDisabled = mockedRegistryForDisabled.getCommandsList()

        assertEquals(registryDisabled.size, 0)
    }
}