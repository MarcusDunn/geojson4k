import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class MultiPointTest {
    @Test
    fun testMultiPointToJson() {
        val json = """
                 {
                     "type": "MultiPoint",
                     "coordinates": [
                         [100.0, 0.0],
                         [101.0, 1.0]
                     ]
                 }
        """.trimIndent()
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

    @Test
    fun testJsonToMultiPoint() {
        val json = """
                 {
                     "type": "MultiPoint",
                     "coordinates": [
                         [100.0, 0.0],
                         [101.0, 1.0]
                     ]
                 }
        """.trimIndent()

    }
}