import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

class LineStringTest {
    //language=json
    private val json = """
     {
             "type": "LineString",
             "coordinates": [
                 [101.0, 0.0],
                 [102.0, 1.0]
             ]
         }"""

    @Test
    fun testJsonToStandardLineString() {
        assertEquals(
            actual = Json.Default.decodeFromString(json),
            expected = GeometryObject.CoordinatesGeometryObject.LineString.Standard(
                Coordinates.LineString(
                    listOf(Position(101f, 0f), Position(102f, 1f))
                )
            )
        )
    }

    @Test
    fun testJsonToLinearRing() {
        TODO("need to figure out how to see if a LineStringTest is right handed")
    }

    @Test
    fun testStandardLineStringToJson() {
        val standard = GeometryObject.CoordinatesGeometryObject.LineString.Standard(
            Coordinates.LineString(
                listOf(Position(101f, 0f), Position(102f, 1f))
            )
        )
        val actual = Json.encodeToJsonElement(standard)
        assertEquals(
            expected = Json.Default.parseToJsonElement(json),
            actual = actual
        )
    }

    @Test
    fun testLinearRingToJson() {
        TODO()
    }
}