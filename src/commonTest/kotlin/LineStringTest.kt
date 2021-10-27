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
                 [102.0, 1.0],
                 [101.0, 1.0],
                 [101.0, 0.0]
             ]
         }"""

    private val coordinates = Coordinates.LineString.LinearRing(
        listOf(
            Position(101f, 0f),
            Position(102f, 1f),
            Position(101f, 1f),
            Position(101f, 0f)
        )
    )

    @Test
    fun testJsonToStandardLineString() {
        assertEquals(
            actual = Json.Default.decodeFromString(json),
            expected = LineString(coordinates) as GeometryObject
        )
    }

    @Test
    fun testJsonToLinearRing() {
        assertEquals(
            actual = Json.Default.decodeFromString(json),
            expected = LineString(coordinates) as GeometryObject
        )
    }

    @Test
    fun testStandardLineStringToJson() {
        val standard = LineString(coordinates) as GeometryObject
        val actual = Json.encodeToJsonElement(standard)
        assertEquals(
            expected = Json.Default.parseToJsonElement(json),
            actual = actual
        )
    }

    @Test
    fun testLinearRingToJson() {
        assertEquals(
            actual = Json.Default.parseToJsonElement(json),
            expected = Json.Default.encodeToJsonElement(
                LineString(coordinates) as GeometryObject
            )
        )
    }

    @Test
    fun testLineStringToJson() {
        assertEquals(
            actual = Json.Default.parseToJsonElement(json),
            expected = Json.Default.encodeToJsonElement(LineString(coordinates) as GeometryObject)
        )
    }
}