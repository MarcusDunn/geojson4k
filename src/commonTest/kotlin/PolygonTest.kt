import coordinates.LineStringCoordinatesLinearRing
import coordinates.PolygonCoordinates
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

class PolygonTest {
    //language=Json
    private val jsonPolygonNoHoles = """
     {
         "type": "Polygon",
         "coordinates": [
             [
                 [100.0, 0.0],
                 [101.0, 0.0],
                 [101.0, 1.0],
                 [100.0, 1.0],
                 [100.0, 0.0]
             ]
         ]
     }
    """.trimIndent()

    private val polygonNoHoles = Polygon(
        PolygonCoordinates(
            listOf(
                LineStringCoordinatesLinearRing(
                    listOf(
                        Position(100f, 0f),
                        Position(101f, 0f),
                        Position(101f, 1f),
                        Position(100f, 1f),
                        Position(100f, 0f),
                    )
                )
            )
        )
    )


    //language=Json
    private val jsonPolygonWithHoles = """
     {
         "type": "Polygon",
         "coordinates": [
             [
                 [100.0, 0.0],
                 [101.0, 0.0],
                 [101.0, 1.0],
                 [100.0, 1.0],
                 [100.0, 0.0]
             ],
             [
                 [100.8, 0.8],
                 [100.8, 0.2],
                 [100.2, 0.2],
                 [100.2, 0.8],
                 [100.8, 0.8]
             ]
         ]
     }
    """.trimIndent()

    private val polygonWithHoles = Polygon(
        PolygonCoordinates(
            listOf(
                LineStringCoordinatesLinearRing(
                    listOf(
                        Position(100.0f, 0.0f),
                        Position(101.0f, 0.0f),
                        Position(101.0f, 1.0f),
                        Position(100.0f, 1.0f),
                        Position(100.0f, 0.0f)
                    )
                ),
                LineStringCoordinatesLinearRing(
                    listOf(
                        Position(100.8f, 0.8f),
                        Position(100.8f, 0.2f),
                        Position(100.2f, 0.2f),
                        Position(100.2f, 0.8f),
                        Position(100.8f, 0.8f)
                    )
                ),
            )
        )
    )

    @Test
    fun testJsonToPolygonNoHoles() {
        assertEquals(
            expected = polygonNoHoles,
            actual = Json.Default.decodeFromString(jsonPolygonNoHoles)
        )
    }

    @Test
    fun testJsonToPolygonWithHole() {
        assertEquals(
            expected = polygonWithHoles,
            actual = Json.Default.decodeFromString(jsonPolygonWithHoles)
        )
    }

    @Test
    fun testPolygonNoHolesToJson() {
        assertEquals(
            actual = Json.encodeToJsonElement(polygonNoHoles),
            expected = Json.Default.parseToJsonElement(jsonPolygonNoHoles)
        )
    }

    @Test
    fun testPolygonWithHoleToJson() {
        assertEquals(
            actual = Json.encodeToJsonElement(polygonWithHoles),
            expected = Json.Default.parseToJsonElement(jsonPolygonWithHoles)
        )
    }
}