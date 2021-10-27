package fm.rythm.commandhandler.textcommands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@DisplayName("Command utils")
internal class CommandUtilsTest {

    @DisplayName("Get command from arraylist of commands by name.")
    @ParameterizedTest(name = "Getting command ''{0}'' with list [''{0}''].")
    @ValueSource(
        strings = [
            "test",
            "command",
            "waffle-generator"
        ]
    )
    fun getCommand(commandName: String) {
        val command = mock<TextCommand> {
            on { getNames() } doReturn arrayListOf(commandName)
        }
        val commands = arrayListOf(command)

        val foundCommand = getCommand(commands, commandName)
        assertEquals(command, foundCommand)
    }
}