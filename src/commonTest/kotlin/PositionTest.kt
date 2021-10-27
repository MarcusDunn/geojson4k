import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal class PositionTest {
    @Test
    fun testPositionToJson() {
        assertEquals("[5.0,10.0,5.0]", Json.Default.encodeToString(Position(5.0f, 10.0f, 5.0f)))
    }

    @Test
    fun testJsonToPosition() {
        assertEquals(Position(5.0f, 10.0f, 5.0f), Json.Default.decodeFromString("[5.0 , 10.0 ,    5.0  ]   "))
    }
}