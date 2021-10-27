import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiPointTest {
    //language=json
    private val json = """
                 {
                     "type": "MultiPoint",
                     "coordinates": [
                         [100.0, 0.0],
                         [101.0, 1.0]
                     ]
                 }
        """.trimIndent()

    @Test
    fun testMultiPointToJson() {
        val multiPoint = GeometryObject.CoordinatesGeometryObject.MultiPoint(
            Coordinates.MultiPoint(
                listOf(
                    Position(100f, 0f),
                    Position(101f, 1f)
                )
            )
        )

        assertEquals(
            actual = Json.Default.encodeToJsonElement(multiPoint),
            expected = Json.Default.parseToJsonElement(json)
        )
    }

    @Test
    fun testJsonToMultiPoint() {
        val expected = GeometryObject.CoordinatesGeometryObject.MultiPoint(
            Coordinates.MultiPoint(
                listOf(
                    Position(100f, 0f),
                    Position(101f, 1f)
                )
            )
        )
        assertEquals(expected = expected, actual = Json.Default.decodeFromString(json))
    }
}