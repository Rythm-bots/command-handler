package fm.rythm.commandhandler.textcommands

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@DisplayName("All parser related functions.")
internal class ParserTest {

    @Nested
    @DisplayName("getPrefixUsed")
    inner class GetPrefixUsedTest {

        @DisplayName("Make sure prefixes are recognised and returned.")
        @ParameterizedTest(name = "Prefix ''{0}'' should be found in ''{1}''")
        @CsvSource(
            "=,=",
            "=,=userinfo",
            "=,=userinfo 132819036282159104",
            ">>>,>>>",
            ">>>,>>>userinfo",
            ">>>,>>>userinfo 132819036282159104"
        )
        fun testMatches(prefix: String, messageContent: String) {
            val prefixUsed = findPrefixUsed(arrayListOf(prefix), messageContent)
            assertEquals(prefix, prefixUsed)
        }

        @DisplayName("Make sure prefixes are not recognised in messages that don't start with them.")
        @ParameterizedTest(name = "Prefix ''{0}'' should not be found in ''{1}''")
        @CsvSource(
            "-,=",
            "-,=userinfo",
            "-,=userinfo 132819036282159104",
            "=,1 + 1 = 2",
            "=,>>>",
            "=,>>>userinfo",
            "=,>>>userinfo 132819036282159104",
            ">>>,waffles >>> pancakes"
        )
        fun testMismatches(prefix: String, messageContent: String) {
            val prefixUsed = findPrefixUsed(arrayListOf(prefix), messageContent)
            assertNull(prefixUsed)
        }
    }

    @Nested
    @DisplayName("isPossibleCommand")
    inner class IsPossibleCommandTest {

        @DisplayName("Make sure messages that are possible commands are detected as such.")
        @ParameterizedTest(name = "Message ''{1}'' should be marked a possible command using prefix ''{0}''.")
        @CsvSource(
            "=,=userinfo",
            "=,=userinfo 132819036282159104",
            ">>>,>>>userinfo",
            ">>>,>>>userinfo 132819036282159104"
        )
        fun testMatches(prefix: String, messageContent: String) {
            val possibleCommand = isPossibleCommand(prefix, messageContent)
            assertTrue(possibleCommand)
        }

        @DisplayName("Make sure messages that are not possible commands are not detected as possible commands.")
        @ParameterizedTest(name = "Message ''{1}'' should not be marked a possible command using prefix ''{0}''.")
        @CsvSource(
            "=,=",
            ">>>,>>>"
        )
        fun testMismatches(prefix: String, messageContent: String) {
            val possibleCommand = isPossibleCommand(prefix, messageContent)
            assertFalse(possibleCommand)
        }
    }

    @Nested
    @DisplayName("getCommandName")
    inner class GetCommandNameTests {

        @DisplayName("Test that valid commands' names are found.")
        @ParameterizedTest(name = "''{0}'' should be found on message ''{1}''")
        @CsvSource(
            "userinfo,=userinfo",
            "userinfo,=userinfo 132819036282159104",
            "ui,=ui"
        )
        fun testValidMessages(name: String, messageContent: String) {
            val commandName = getCommandName(1, messageContent)
            assertEquals(name, commandName)
        }
    }

    @Nested
    @DisplayName("getRawParameters")
    inner class GetRawParametersTest {

        @DisplayName("Test that parameters are correctly retrieved from command.")
        @ParameterizedTest(name = "''{2}'' should yield ''{3}''")
        @CsvSource(
            "=,cmd,=cmd 1 2 3,1 2 3",
            "=,cmd,=cmd a b c,a b c",
            "=,cmd,=cmd,''"
        )
        fun testParameterRetrieval(
            prefix: String,
            commandName: String,
            messageContent: String,
            expectedRawParameters: String
        ) {
            val rawParametersRetrieved = getRawParameters(prefix.length, commandName, messageContent)
            assertEquals(expectedRawParameters, rawParametersRetrieved)
        }
    }
}