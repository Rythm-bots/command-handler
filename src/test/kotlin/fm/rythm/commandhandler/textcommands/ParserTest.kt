package fm.rythm.commandhandler.textcommands

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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

    @Nested
    @DisplayName("createParameterRegex")
    inner class CreateParameterRegexTest {

        @DisplayName("Test Text regex creation.")
        @Test
        fun testTextRegex() {
            val parameter = Parameter(ParameterType.TEXT)

            val map = linkedMapOf(
                "test" to parameter
            )

            val regex = compileParameterRegex(map)

            assertEquals("""^(?<test>(.+))$""", regex.toString())
        }

        @DisplayName("Test Int regex creation.")
        @Test
        fun testIntRegex() {
            val parameter = Parameter(ParameterType.INT)

            val map = linkedMapOf(
                "test" to parameter
            )

            val regex = compileParameterRegex(map)

            assertEquals("""^(?<test>(\d+))$""", regex.toString())
        }

        @DisplayName("Test Long regex creation.")
        @Test
        fun testLongRegex() {
            val parameter = Parameter(ParameterType.LONG)

            val map = linkedMapOf(
                "test" to parameter
            )

            val regex = compileParameterRegex(map)

            assertEquals("""^(?<test>(\d+))$""", regex.toString())
        }

        @DisplayName("Test User regex creation.")
        @Test
        fun testUserRegex() {
            val parameter = Parameter(ParameterType.USER)

            val map = linkedMapOf(
                "test" to parameter
            )

            val regex = compileParameterRegex(map)

            assertEquals("""^(?<test>(?:<@!?)?(\d+)>?)$""", regex.toString())
        }

        @DisplayName("Test multiple User regex creation.")
        @Test
        fun testMultiUserRegex() {
            val parameter = Parameter(ParameterType.USER, allowMultiple = true)

            val map = linkedMapOf(
                "test" to parameter
            )

            val regex = compileParameterRegex(map)

            assertEquals("""^(?<test>(?:(?:<@!?)?(\d+)>?|\s)+)$""", regex.toString())
        }

        @DisplayName("Test Text, Int, Long & User combined")
        @Test
        fun testCombined() {
            val parameters = linkedMapOf(
                "text" to Parameter(ParameterType.TEXT),
                "int" to Parameter(ParameterType.INT),
                "long" to Parameter(ParameterType.LONG),
                "user" to Parameter(ParameterType.USER),
            )

            val regex = compileParameterRegex(parameters)

            assertEquals(
                """^(?<text>(.+))\s+(?<int>(\d+))\s+(?<long>(\d+))\s+(?<user>(?:<@!?)?(\d+)>?)$""",
                regex.toString()
            )
        }
    }

    @Nested
    @DisplayName("extractParameters")
    inner class ExtractParametersTest {

        @DisplayName("Test the parsing & extraction of the parameters of a command.")
        @ParameterizedTest(name = "''{0}'', ''{1}'', ''{2}'' should be parsed correctly.")
        @CsvSource(
            "1,Travel heavily like a undead cannibal.,<@132819036282159104>,132819036282159104",
            "123456,'None of these sonic showers infiltrate intelligent, sub-light transporters.',132819036282159104,132819036282159104",
            "6544214,Going to the realm of futility doesn’t respect paradox anymore than receiving creates eternal result.,<@!132819036282159104>,132819036282159104",
        )
        fun testParameterExtraction(
            number: String,
            text: String,
            user: String,
            userId: String
        ) {
            val commandRawParameters = "$number $text $user"
            val parameters = linkedMapOf(
                "number" to Parameter(ParameterType.INT),
                "text" to Parameter(ParameterType.TEXT),
                "user" to Parameter(ParameterType.USER)
            )
            val regex = """^(?<number>(\d+))\s+(?<text>(.+))\s+(?<user>(?:<@!?)?(\d+)>?)$""".toRegex()

            val extractedParameters = extractParameters(commandRawParameters, parameters, regex)
                ?: fail("extractedParameters was null")

            assertEquals(number.toInt(), extractedParameters["number"] as Int)
            assertEquals(text, extractedParameters["text"])
            assertEquals(userId.toLong(), extractedParameters["user"] as Long)
        }

        @DisplayName("Test the extraction of multiple allowed values.")
        @ParameterizedTest(name = "''{0}'', ''{1}'', ''{2}'' should be parsed correctly.")
        @CsvSource(
            "1,<@132819036282159104> 132819036282159104 <@!132819036282159104>,Travel heavily like a undead cannibal.,132819036282159104",
            "123456,<@132819036282159104> 132819036282159104 <@!132819036282159104>,'None of these sonic showers infiltrate intelligent, sub-light transporters.',132819036282159104",
            "6544214,<@132819036282159104> 132819036282159104 <@!132819036282159104>,Going to the realm of futility doesn’t respect paradox anymore than receiving creates eternal result.,132819036282159104",
        )
        fun testMultiParameterExtraction(
            number: String,
            users: String,
            text: String,
            userId: String
        ) {
            val commandRawParameters = "$number $users $text"
            val parameters = linkedMapOf(
                "number" to Parameter(ParameterType.LONG),
                "users" to Parameter(ParameterType.USER, allowMultiple = true),
                "text" to Parameter(ParameterType.TEXT)
            )
            val regex = """^(?<number>(\d+))\s+(?<users>(?:(?:<@!?)?(\d+)>?|\s)+)\s+(?<text>(.+))$""".toRegex()
            val expectedUsersValue = listOf(
                userId.toLong(),
                userId.toLong(),
                userId.toLong()
            )

            val extractedParameters = extractParameters(commandRawParameters, parameters, regex)
                ?: fail("extractedParameters was null")

            assertEquals(number.toLong(), extractedParameters["number"] as Long)
            assertEquals(text, extractedParameters["text"])
            assertEquals(expectedUsersValue, extractedParameters["users"] as List<*>)
        }
    }
}