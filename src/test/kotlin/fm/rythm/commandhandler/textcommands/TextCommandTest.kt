package fm.rythm.commandhandler.textcommands

import fm.rythm.commandhandler.classes.CheckFailedException
import fm.rythm.commandhandler.classes.InvalidParametersException
import fm.rythm.commandhandler.textcommands.TextCommand.Companion.preParseContext
import fm.rythm.test.TestCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.lang.NullPointerException

open class ErrorHandlerCallback {
    open fun handler(e: Exception, m: Message, command: TextCommand<*>): Boolean {
        return true
    }
}

internal class TextCommandTest {
    @Test
    fun generateEmbed() {
    }

    @Test
    fun execute() {
        val mockedChannel = mock<TextChannel> {}
        val mockedJda = mock<JDA> {}
        val mockedMember = mock<Member> {}
        val mockedMessage = mock<Message> {
            on { contentRaw } doReturn "=test"
            on { textChannel } doReturn mockedChannel
            on { jda } doReturn mockedJda
            on { member } doReturn mockedMember
        }
        val parameters = linkedMapOf(
            "p1" to TextCommandParameter(TextCommandParameterType.INT),
            "p2" to TextCommandParameter(TextCommandParameterType.STRING),
            "p3" to TextCommandParameter(TextCommandParameterType.USER)
        )
        val commandNameUsed = "test"
        val rightHandSide = "1 lorem ipsum <@132819036282159104>"
        val preParseContext = preParseContext(mockedMessage, commandNameUsed)
        val commandAllowAllCheck = mock<TestCommand> {
            on { check(any()) } doReturn true
            on { getParameters() } doReturn parameters
            on { getPattern() } doReturn TextCommand.buildValidationRegex(parameters).toRegex()
        }

        // The above scenario shouldn't throw any errors.
        assertDoesNotThrow {
            commandAllowAllCheck.execute(
                rightHandSide,
                mockedMessage,
                null,
                preParseContext
            )
        }

        verify(commandAllowAllCheck).check(any())

        // Make sure an InvalidParametersException error on a command execution without an error handler
        // is thrown.
        assertThrows<InvalidParametersException> {
            commandAllowAllCheck.execute(
                "",
                mockedMessage,
                null,
                preParseContext
            )
        }

        commandAllowAllCheck.moduleCheck = { false }

        assertThrows<CheckFailedException> {
            commandAllowAllCheck.execute(
                rightHandSide,
                mockedMessage,
                null,
                preParseContext
            )
        }

        // Make sure an error anywhere in the pipeline of the command is
        // thrown with a generic exception using jargon provided by /u/SteveTenants.
        commandAllowAllCheck.moduleCheck = {
            throw NullPointerException("You can't index the malware without synthesizing the virtual TPS bus!")
        }

        assertThrows<NullPointerException> {
            commandAllowAllCheck.execute(
                rightHandSide,
                mockedMessage,
                null,
                preParseContext
            )
        }

        // Make sure an error that *is* handled does not throw.

        val errorHandler = mock<ErrorHandlerCallback> {
            on { handler(any(), any(), any()) } doReturn true
        }

        assertDoesNotThrow {
            commandAllowAllCheck.execute(
                rightHandSide,
                mockedMessage,
                errorHandler::handler,
                preParseContext
            )
        }

        verify(errorHandler).handler(any(), any(), any())

        // Make sure command with a check that fails throws CheckFailedException
        // on an execution without an error handler.
        val commandAllowNoneCheck = mock<TestCommand> {
            on { check(any()) } doReturn false
            on { getParameters() } doReturn parameters
            on { getPattern() } doReturn TextCommand.buildValidationRegex(parameters).toRegex()
        }

        assertThrows<CheckFailedException> {
            commandAllowNoneCheck.execute(
                "",
                mockedMessage,
                null,
                preParseContext
            )
        }
    }
}