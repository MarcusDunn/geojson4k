import kotlin.test.Test
import kotlin.test.assertEquals

class GeometryObjectKtTest {
    @Test
    // works
    fun testIsRightHanded() {
        assertEquals(
            Coordinates.LineString.LinearRing(
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
            Coordinates.LineString.LinearRing(
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