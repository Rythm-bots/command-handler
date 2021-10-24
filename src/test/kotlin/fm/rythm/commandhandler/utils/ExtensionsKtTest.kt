package fm.rythm.commandhandler.utils

import fm.rythm.commandhandler.classes.TypePromiseBroken
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.requests.restaction.MessageAction
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class ExtensionsKtTest {
    @Test
    fun sendSafe() {
        val content = "test"
        val mockedMessageAction = mock<MessageAction> {}
        val mockedChannel = mock<MessageChannel> {
            on { sendMessage(any<Message>()) } doReturn mockedMessageAction
        }

        mockedChannel.sendSafe(content)

        val captor = ArgumentCaptor.forClass(Message::class.java)
        verify(mockedChannel).sendMessage(captor.capture())
        assertEquals(captor.value.contentRaw, content)
    }

    @Test
    fun asDiscordTimestamp() {
        val timestampLong = 1L

        val timestampShortDateTime = timestampLong.asDiscordTimestamp()
        assertEquals(timestampShortDateTime, "<t:1:f>")

        val timestampShortTime = timestampLong.asDiscordTimestamp(DiscordTimestampStyle.ShortTime)
        assertEquals(timestampShortTime, "<t:1:t>")

        val timestampLongTime = timestampLong.asDiscordTimestamp(DiscordTimestampStyle.LongTime)
        assertEquals(timestampLongTime, "<t:1:T>")

        val timestampShortDate = timestampLong.asDiscordTimestamp(DiscordTimestampStyle.ShortDate)
        assertEquals(timestampShortDate, "<t:1:d>")

        val timestampLongDate = timestampLong.asDiscordTimestamp(DiscordTimestampStyle.LongDate)
        assertEquals(timestampLongDate, "<t:1:D>")

        val timestampLongDateTime = timestampLong.asDiscordTimestamp(DiscordTimestampStyle.LongDateTime)
        assertEquals(timestampLongDateTime, "<t:1:F>")

        val timestampRelative = timestampLong.asDiscordTimestamp(DiscordTimestampStyle.Relative)
        assertEquals(timestampRelative, "<t:1:R>")
    }

    @Test
    fun truncateIfNecessary() {
        val stringWithinBounds = "honse"
        val stringTooLong = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."

        assertEquals(stringWithinBounds.truncateIfNecessary(), stringWithinBounds)
        assertEquals(stringTooLong.truncateIfNecessary(10), "Lorem ipsum... (+113)")
    }

    @Test
    fun promiseToBeOfType() {
        val value: Any = 800L
        assertDoesNotThrow {
            value.promiseToBeOfType<Long>()
        }

        assertThrows<TypePromiseBroken> {
            value.promiseToBeOfType<Message>()
        }
    }
}