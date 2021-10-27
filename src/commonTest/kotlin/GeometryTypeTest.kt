import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GeometryTypeTest {
    @Test
    internal fun testPointTypeToString() {
        assertEquals("\"Point\"", Json.Default.encodeToString(GeometryType("Point")))
    }

    @Test
    internal fun stringToPointType() {
        assertEquals(Json.Default.decodeFromString("\"Point\""), GeometryType("Point"))
    }
}