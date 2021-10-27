package fm.rythm.commandhandler.utils

import fm.rythm.commandhandler.textcommands.TextCommand
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.assertEquals

class UtilsTest {
    @Nested
    @DisplayName("recursivelyFindCommandUsed")
    inner class RecursivelyFindTest {

        @DisplayName("Ensure normal commands are found.")
        @ParameterizedTest(name = "Using prefix length {0}, command ''{1}'' should be found in ''{2}''")
        @CsvSource(
            "1,test,=test",
            "2,test,>>test",
            "3,test,-->test",
            "1,test,=test 132819036282159104"
        )
        fun testValidCommands(prefixLength: Int, commandName: String, commandContent: String) {
            val command = mock<TextCommand> {
                on { getNames() } doReturn arrayListOf(commandName)
            }
            val commandUsed = recursivelyFindCommandUsed(prefixLength, arrayListOf(command), commandContent)

            assertEquals(command, commandUsed)
        }

        @DisplayName("Ensure subcommands are found.")
        @ParameterizedTest(name = "Using prefix length {0}, subcommand ''{2}'' should be found in ''{3}''")
        @CsvSource(
            "1,command,subcommand,=command subcommand",
            "2,command,subcommand,>>command subcommand",
            "3,command,subcommand,-->command subcommand",
            "1,command,subcommand,=command subcommand 132819036282159104"
        )
        fun testSubcommands(
            prefixLength: Int,
            baseCommandName: String,
            subCommandName: String,
            commandContent: String
        ) {
            val subCommand = mock<TextCommand> {
                on { getNames() } doReturn arrayListOf(subCommandName)
            }
            val command = mock<TextCommand> {
                on { getNames() } doReturn arrayListOf(baseCommandName)
                on { getRegistry() } doReturn arrayListOf(subCommand)
            }
            val commandUsed = recursivelyFindCommandUsed(prefixLength, arrayListOf(command), commandContent)

            assertEquals(subCommand, commandUsed)
        }
    }
}