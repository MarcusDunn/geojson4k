import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

class GeometryObjectTest {
    //language=Json
    private val json = """
                {
                    "type": "Point",
                    "coordinates": [100.0, 0.0]
                }
                """.trimIndent()

    @Test
    internal fun testJsonToPoint() {
        val expected = Point(Coordinates.Point(Position(100f, 0f))) as GeometryObject
        assertEquals(
            expected = expected,
            actual = Json.Default.decodeFromString(json)
        )
    }

    @Test
    internal fun testPointToJson() {
        val point = Point(coordinates = Coordinates.Point(Position(100f, 0f))) as GeometryObject
        assertEquals(
            expected = Json.Default.parseToJsonElement(json),
            actual = Json.Default.encodeToJsonElement(point)
        )
    }
}