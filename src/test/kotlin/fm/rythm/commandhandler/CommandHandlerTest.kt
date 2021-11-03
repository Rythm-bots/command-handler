package fm.rythm.commandhandler

import fm.rythm.commandhandler.textcommands.*
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Message
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.lang.Exception
import kotlin.test.assertEquals

class TestModule(
    private val shouldAllowCommand: Boolean,
    enabled: Boolean
) : Module(enabled) {
    override fun check(context: ModuleContext): Boolean {
        return shouldAllowCommand
    }
}

class TestCommand(
    names: ArrayList<String>,
    parameters: LinkedHashMap<String, Parameter> = linkedMapOf(),
    private val shouldError: Boolean = false,
    private val permitted: Boolean = true,
    module: Module? = null
) : TextCommand<Unit>(names, parameters, module) {

    override fun check(context: CommandContext<Unit>): Boolean {
        return permitted
    }

    override fun parameterFactory(parameters: HashMap<String, Any>) {
        return
    }

    override fun onExecuted(context: CommandContext<Unit>) {
        if (shouldError)
            throw NullPointerException("Exception thrown for testing purposes.")
    }
}

data class TestCommandParameters(val number: Int, val users: List<Long>, val text: String)

class TestCommandWithParameters(
    names: ArrayList<String>,
    parameters: LinkedHashMap<String, Parameter> = linkedMapOf()
) : TextCommand<TestCommandParameters>(names, parameters) {

    override fun check(context: CommandContext<TestCommandParameters>): Boolean {
        return true
    }

    override fun parameterFactory(parameters: HashMap<String, Any>): TestCommandParameters {
        val number = parameters["number"]!! as Int
        val users = parameters["users"]!! as List<Long> // TODO: solve warning
        val text = parameters["text"]!! as String

        return TestCommandParameters(number, users, text)
    }

    override fun onExecuted(context: CommandContext<TestCommandParameters>) {

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
        val commands = arrayListOf<TextCommand<*>>(
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
        val commands = arrayListOf<TextCommand<*>>(
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
        val commands = arrayListOf<TextCommand<*>>(
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
        val commands = arrayListOf<TextCommand<*>>(
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
        val command = TestCommand(arrayListOf("test"), linkedMapOf(), true)
        val commands = arrayListOf<TextCommand<*>>(
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
        val command = TestCommand(arrayListOf("test"), linkedMapOf(), true)
        val commands = arrayListOf<TextCommand<*>>(
            command
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
        }

        fun onError(
            message: Message,
            command: TextCommand<*>,
            exception: Exception
        ): Boolean {
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
        val command = TestCommand(arrayListOf("test"), linkedMapOf(),true)
        val commands = arrayListOf<TextCommand<*>>(
            command
        )
        val mockJda = mock<JDA> {}
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
            on { jda } doReturn mockJda
        }

        fun onError(
            message: Message,
            command: TextCommand<*>,
            exception: Exception
        ): Boolean {
            return true
        }

        val commandHandler = CommandHandler(prefixes, commands, ::onError)

        // NullPointerException is hardcoded in TestCommand
        // The triggering of the error is defined by the shouldError argument of TestCommand's constructor
        assertDoesNotThrow {
            commandHandler.handleJdaMessage(mockMessage)
        }
    }

    @DisplayName("Command execution with parameters")
    @ParameterizedTest(name = "Should find parameters ''{0}'', ''{1}'', ''{2}''")
    @CsvSource(
        "123,<@132819036282159104> 132819036282159104 <@!132819036282159104>,Per guest prepare a dozen tablespoons of ice water with cut strudel for dessert.",
        "321,<@132819036282159104>,Pin of a lively courage, pull the power.",
    )
    fun testMultiParameters(firstParam: String, secondParam: String, thirdParam: String) {
        val prefixes = arrayListOf("=")
        val command = TestCommandWithParameters(arrayListOf("test"), linkedMapOf(
            "number" to Parameter(ParameterType.INT),
            "users" to Parameter(ParameterType.USER, allowMultiple = true),
            "text" to Parameter(ParameterType.TEXT),
        ))
        val commands = arrayListOf<TextCommand<*>>(
            command
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test $firstParam $secondParam $thirdParam"
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        assertDoesNotThrow {
            val result = commandHandler.handleJdaMessage(mockMessage)
            assertEquals(Pair(CommandHandlerResult.SUCCESS, command), result)
        }
    }

    @DisplayName("Forbidden command execution")
    @Test
    fun testForbidden() {
        val prefixes = arrayListOf("=")
        val command = TestCommand(arrayListOf("test"), linkedMapOf(), permitted = false)
        val commands = arrayListOf<TextCommand<*>>(
            command
        )
        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        assertDoesNotThrow {
            val result = commandHandler.handleJdaMessage(mockMessage)
            assertEquals(Pair(CommandHandlerResult.FORBIDDEN, command), result)
        }
    }

    @DisplayName("Command with module whose check fails should not execute")
    @Test
    fun testModuleCheckFailure() {
        val prefixes = arrayListOf("=")
        val testModule = TestModule(shouldAllowCommand = false, true)
        val command = TestCommand(arrayListOf("test"), linkedMapOf(), permitted = false, module = testModule)
        val commands = arrayListOf<TextCommand<*>>(
            command
        )

        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        val (result, commandResult) = commandHandler.handleJdaMessage(mockMessage)

        assertEquals(CommandHandlerResult.FORBIDDEN, result)
        assertEquals(commandResult, command)
    }

    @DisplayName("Command with disabled module should be executed regardless of whether the module's check passes or not, provided the command's check passes")
    @Test
    fun testModuleDisabled() {
        val prefixes = arrayListOf("=")
        val testModule = TestModule(shouldAllowCommand = false, false)
        val command = TestCommand(arrayListOf("test"), linkedMapOf(), permitted = false, module = testModule)
        val commands = arrayListOf<TextCommand<*>>(
            command
        )

        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        val (result, commandResult) = commandHandler.handleJdaMessage(mockMessage)

        assertEquals(CommandHandlerResult.FORBIDDEN, result)
        assertEquals(commandResult, command)
    }

    @DisplayName("Command with module whose check does not fail should execute with a passing command check")
    @Test
    fun testModuleCheckSuccess() {
        val prefixes = arrayListOf("=")
        val testModule = TestModule(shouldAllowCommand = true, true)
        val command = TestCommand(arrayListOf("test"), linkedMapOf(), permitted = false, module = testModule)
        val commands = arrayListOf<TextCommand<*>>(
            command
        )

        val mockMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
        }

        val commandHandler = CommandHandler(prefixes, commands, null)

        val (result, commandResult) = commandHandler.handleJdaMessage(mockMessage)

        assertEquals(CommandHandlerResult.FORBIDDEN, result)
        assertEquals(commandResult, command)
    }
}