package coordinates

import kotlinx.serialization.Serializable

@Serializable
data class PolygonCoordinates(val value: List<LineStringCoordinatesLinearRing>) : Coordinates()