import coordinates.PolygonCoordinates
import kotlinx.serialization.Serializable

// -  For type "Polygon", the "coordinates" member MUST be an array of
//    linear ring coordinate arrays.
//
// -  For Polygons with more than one of these rings, the first MUST be
//    the exterior ring, and any others MUST be interior rings.  The
//    exterior ring bounds the surface, and the interior rings (if
//    present) bound holes within the surface.
@Serializable
open class Polygon private constructor(
    final override val coordinates: PolygonCoordinates,
    final override val type: GeometryObject.GeometryObjectType.Polygon
) : GeometryObject.CoordinateBased {
    constructor(coordinates: PolygonCoordinates) : this(coordinates, GeometryObject.GeometryObjectType.Polygon)
    init {
        require(coordinates.value.isNotEmpty()) { "must contain at at least one linestring" }
        val outerRing = coordinates.value.first()
        val rest = coordinates.value.drop(1)
        require(rest.all { outerRing.contains(it) }) { "must contain at at least one linestring" }
    }
}