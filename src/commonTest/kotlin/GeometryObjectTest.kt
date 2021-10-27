import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

class GeometryObjectTest {
    @Test
    internal fun testJsonToPoint() {
        val json = """
                {
                    "type": "Point",
                    "coordinates": [100.0, 0.0]
                }
                """.trimIndent()
        val expected = GeometryObject.CoordinatesGeometryObject.Point(Coordinate.Point(Position(100f, 0f)))
        assertEquals(
            expected = expected,
            actual = Json.Default.decodeFromString(json)
        )
    }

    @Test
    internal fun testPointToJson() {
        val json = """
            {
                "type": "Point",
                "coordinates": [100.0, 0.0]
            }
            """.trimIndent()
        val point = GeometryObject.CoordinatesGeometryObject.Point(Coordinate.Point(Position(100f, 0f)))
        assertEquals(
            expected = Json.Default.parseToJsonElement(json),
            actual = Json.Default.encodeToJsonElement(point)
        )
    }
}