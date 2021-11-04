package fm.rythm.commandhandler.textcommands

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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
        val command = mock<TextCommand<Unit>> {
            on { getNames() } doReturn arrayListOf(commandName)
        }
        val commands = arrayListOf<TextCommand<*>>(command)

        val foundCommand = getCommand(commands, commandName)
        assertEquals(command, foundCommand)
    }

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
            val command = mock<TextCommand<Unit>> {
                on { getNames() } doReturn arrayListOf(commandName)
            }
            val (commandUsedLength, commandUsed) = commandContent.recursivelyFindCommandUsed(prefixLength, arrayListOf(command))
                ?: fail("recursivelyFindCommandUsed returned null")

            assertEquals(command, commandUsed)
            assertEquals(prefixLength, commandUsedLength)
        }

        @DisplayName("Ensure subcommands are found.")
        @ParameterizedTest(name = "Using prefix length {0}, subcommand ''{2}'' should be found in ''{3}''")
        @CsvSource(
            "1,command,subcommand,=command subcommand,'=command '",
            "2,command,subcommand,>>command subcommand,'>>command '",
            "3,command,subcommand,-->command subcommand,'-->command '",
            "3,command,subcommand,-->command          subcommand,'-->command          '",
            "1,command,subcommand,=command subcommand 132819036282159104,'=command '",
            "1,command,subcommand,=command subcommand       132819036282159104,'=command '",
        )
        fun testSubcommands(
            prefixLength: Int,
            baseCommandName: String,
            subCommandName: String,
            commandContent: String,
            baseCommand: String
        ) {
            val subCommand = mock<TextCommand<Unit>> {
                on { getNames() } doReturn arrayListOf(subCommandName)
            }
            val command = mock<TextCommand<Unit>> {
                on { getNames() } doReturn arrayListOf(baseCommandName)
                on { getSubcommandRegistry() } doReturn arrayListOf(subCommand)
            }
            val (commandUsedLength, commandUsed) = commandContent.recursivelyFindCommandUsed(prefixLength, arrayListOf(command))
                ?: fail("recursivelyFindCommandUsed returned null")

            assertEquals(subCommand, commandUsed)
            assertEquals(baseCommand.length, commandUsedLength)
        }

        @DisplayName("Ensure subcommand parameters are found.")
        @ParameterizedTest(
            name = "''{4}'' should be found on ''{3}''"
        )
        @CsvSource(
            "1,command,subcommand,=command subcommand 132819036282159104,132819036282159104",
            "1,command,subcommand,=command subcommand       132819036282159104,132819036282159104",
            "1,command,subcommand,=command subcommand       123,123",
        )
        fun testSubcommandParameterResolution(
            prefixLength: Int,
            baseCommandName: String,
            subCommandName: String,
            commandContent: String,
            expectedParameter: String
        ) {
            val parameters = getRawParameters(
                prefixLength
                    + baseCommandName.length
                        + " ".length,
                subCommandName,
                commandContent
            ).trim()
            assertEquals(expectedParameter, parameters)
        }
    }
}

