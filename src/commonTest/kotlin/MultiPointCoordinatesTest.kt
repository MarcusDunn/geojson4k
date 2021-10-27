import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiPointCoordinatesTest {
    //language=json
    private val json = """
            [[1.0, 2.0], [3.0, 4.0]]
            """.trimIndent()

    @Test
    fun testJsonToMultiPointCoordinate() {
        val multiPoint = Coordinates.MultiPoint(listOf(Position(1f, 2f), Position(3f, 4f)))
        assertEquals(
            actual = Json.Default.decodeFromString(json),
            expected = multiPoint
        )
    }

    @Test
    fun testMultiPointCoordinateToJson() {
        val multiPoint = Coordinates.MultiPoint(listOf(Position(1f, 2f), Position(3f, 4f)))
        assertEquals(
            actual = Json.Default.encodeToJsonElement(multiPoint),
            expected = Json.Default.parseToJsonElement(json)
        )
    }
}