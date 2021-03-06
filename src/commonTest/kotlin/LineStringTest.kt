import coordinates.LineStringCoordinatesLinearRing
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

    private val coordinates = LineStringCoordinatesLinearRing(
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
            expected = LineString(coordinates)
        )
    }

    @Test
    fun testJsonToLinearRing() {
        assertEquals(
            actual = Json.Default.decodeFromString(json),
            expected = LineString(coordinates)
        )
    }

    @Test
    fun testStandardLineStringToJson() {
        val standard = LineString(coordinates)
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
                LineString(coordinates)
            )
        )
    }

    @Test
    fun testLineStringToJson() {
        assertEquals(
            actual = Json.Default.parseToJsonElement(json),
            expected = Json.Default.encodeToJsonElement(LineString(coordinates))
        )
    }

    @Test
    // works
    fun testIsRightHanded() {
        assertEquals(
            LineStringCoordinatesLinearRing(
                listOf(
                    Position(0f, 0f),
                    Position(1f, 1f),
                    Position(1f, 0f),
                    Position(0f, 0f)
                )
            ).isClockwise, true
        )
    }

    @Test
    // fails
    fun testIsRightHandedOtherWay() {
        assertEquals(
            LineStringCoordinatesLinearRing(
                listOf(
                    Position(0f, 0f),
                    Position(1f, 0f),
                    Position(1f, 1f),
                    Position(0f, 0f)
                )
            ).isClockwise, false
        )
    }
}