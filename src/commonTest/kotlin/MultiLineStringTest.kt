import coordinates.LineStringCoordinates
import coordinates.MultiLineStringCoordinates
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiLineStringTest {
    //language=Json
    private val json = """
     {
         "type": "MultiLineString",
         "coordinates": [
             [
                 [100.0, 0.0],
                 [101.0, 1.0]
             ],
             [
                 [102.0, 2.0],
                 [103.0, 3.0]
             ]
         ]
     }
    """.trimIndent()

    private val multiLineString = MultiLineString(
        MultiLineStringCoordinates(
            listOf(
                LineStringCoordinates(
                    listOf(
                        Position(100f, 0f),
                        Position(101f, 1f)
                    )
                ), LineStringCoordinates(
                    listOf(
                        Position(102f, 2f),
                        Position(103f, 3f)
                    )
                )
            )
        )
    )

    @Test
    fun testJsonToMultiLineString() {
        assertEquals(
            actual = Json.Default.decodeFromString(json),
            expected = multiLineString
        )
    }

    @Test
    fun testMultiLineStringToJson() {
        assertEquals(
            actual = Json.Default.encodeToJsonElement(multiLineString),
            expected = Json.Default.parseToJsonElement(json)
        )
    }
}