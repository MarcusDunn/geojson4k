package coordinates

import kotlinx.serialization.Serializable

@Serializable
data class MultiPolygonCoordinates(val value: List<PolygonCoordinates>) : Coordinates()