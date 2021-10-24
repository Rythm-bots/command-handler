package fm.rythm.commandhandler.classes

import net.dv8tion.jda.api.JDA
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class RegistrationTest {
    @Test
    fun registerGenericEvents() {
        // Create mocked JDA instance.
        val jda = mock<JDA> {}

        val eventHandlerOne = mock<EventHandler> {
            on { getIfDisabled() } doReturn false
        }
        val eventHandlerTwo = mock<EventHandler> {
            on { getIfDisabled() } doReturn true
        }

        assertEquals(eventHandlerOne.getIfDisabled(), false)
        assertEquals(eventHandlerTwo.getIfDisabled(), true)

        // Register our event. Only eventHandlerOne should have been added, as the second one
        // is disabled.
        registerGenericEvents(jda, eventHandlerOne)

        // Verify registerGenericEvents has run addEventListener with eventHandlerOne
        verify(jda).addEventListener(eventHandlerOne)
    }
}