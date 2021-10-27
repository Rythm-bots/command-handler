package fm.rythm.commandhandler

import fm.rythm.commandhandler.textcommands.TextCommand
import net.dv8tion.jda.api.entities.Message
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.lang.NullPointerException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TestCommand(names: ArrayList<String>, private val shouldError: Boolean = false) : TextCommand(names) {
    override fun execute() {
        if (shouldError)
            throw NullPointerException("Exception thrown for testing purposes.")
    }
}

@Nested
@DisplayName("CommandHandler class")
internal class CommandHandlerTest {

    @DisplayName("Messages without prefix should fail accordingly. (prefixes => ['='])")
    @ParameterizedTest(name = "Command content ''{0}'' should not trigger any commands from list ['test', 'command'].")
    @ValueSource(
        strings = [
            "hi",
            "It's pronounced GIF not GIF!",
            "Cheemsburger"
        ]
    )
    fun testWithNoPrefix(content: String) {
        val prefixes = arrayListOf("=")
        val commands = arrayListOf<TextCommand>(
            TestCommand(arrayListOf("test")),
            TestCommand(arrayListOf("command"))
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn content
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        val (result, _) = commandHandler.handleJdaMessage(mockMessage)

        assertEquals(CommandHandlerResult.NO_PREFIX, result)
    }

    @DisplayName("Messages that contain a prefix but are otherwise not valid commands should fail accordingly.")
    @ParameterizedTest(name = "Command content ''{0}'' should not trigger any commands from list ['test', 'command'].")
    @ValueSource(
        strings = [
            "=",
            ">>>",
            "!",
            "-"
        ]
    )
    fun testNotCommand(content: String) {
        val prefixes = arrayListOf(content)
        val commands = arrayListOf<TextCommand>(
            TestCommand(arrayListOf("test")),
            TestCommand(arrayListOf("command"))
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn content
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        val (result, _) = commandHandler.handleJdaMessage(mockMessage)

        assertEquals(CommandHandlerResult.NOT_COMMAND, result)
    }

    @DisplayName("Messages that contain a prefix, but use a command that doesn't exist, should fail accordingly.")
    @ParameterizedTest(name = "Command content ''{0}'' should not trigger any commands from list ['test', 'command'].")
    @ValueSource(
        strings = [
            "=pineapple",
            "=pizza"
        ]
    )
    fun testNotFound(content: String) {
        val prefixes = arrayListOf("=")
        val commands = arrayListOf<TextCommand>(
            TestCommand(arrayListOf("test")),
            TestCommand(arrayListOf("command"))
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn content
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        val (result, _) = commandHandler.handleJdaMessage(mockMessage)

        assertEquals(CommandHandlerResult.COMMAND_NOT_FOUND, result)
    }

    @DisplayName("Messages that contain valid and found commands should execute.")
    @ParameterizedTest(name = "Command content ''{0}'' should trigger a commands from list ['test', 'command'].")
    @ValueSource(
        strings = [
            "=test",
            "=command"
        ]
    )
    fun testValid(content: String) {
        val prefixes = arrayListOf("=")
        val command = TestCommand(arrayListOf("test", "command"))
        val commands = arrayListOf<TextCommand>(
            command
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn content
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        val (result, commandResult) = commandHandler.handleJdaMessage(mockMessage)

        assertEquals(CommandHandlerResult.SUCCESS, result)
        assertEquals(command, commandResult)
    }

    @DisplayName("Unhandled command execution errors should be thrown.")
    @Test
    fun testExecutionErrorWithoutOnError() {
        val prefixes = arrayListOf("=")
        val command = TestCommand(arrayListOf("test"), true)
        val commands = arrayListOf<TextCommand>(
            command
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        // NullPointerException is hardcoded in TestCommand
        // The triggering of the error is defined by the shouldError argument of TestCommand's constructor
        assertThrows<NullPointerException> {
            commandHandler.handleJdaMessage(mockMessage)
        }
    }

    @DisplayName("Command execution errors with an onError callback that returns false should throw the exception.")
    @Test
    fun testUnhandledExecutionError() {
        val prefixes = arrayListOf("=")
        val command = TestCommand(arrayListOf("test"), true)
        val commands = arrayListOf<TextCommand>(
            command
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
        }

        fun onError(): Boolean{
            return false
        }

        val commandHandler = CommandHandler(prefixes, commands, ::onError)

        // NullPointerException is hardcoded in TestCommand
        // The triggering of the error is defined by the shouldError argument of TestCommand's constructor
        assertThrows<NullPointerException> {
            commandHandler.handleJdaMessage(mockMessage)
        }
    }

    @DisplayName("Command execution errors with an onError callback that returns true should not throw the exception.")
    @Test
    fun testHandledExecutionError() {
        val prefixes = arrayListOf("=")
        val command = TestCommand(arrayListOf("test"), true)
        val commands = arrayListOf<TextCommand>(
            command
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
        }

        fun onError(): Boolean{
            return true
        }

        val commandHandler = CommandHandler(prefixes, commands, ::onError)

        // NullPointerException is hardcoded in TestCommand
        // The triggering of the error is defined by the shouldError argument of TestCommand's constructor
        assertDoesNotThrow {
            commandHandler.handleJdaMessage(mockMessage)
        }
    }
}