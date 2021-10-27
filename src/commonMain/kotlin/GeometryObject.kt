import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonClassDiscriminator

// A Geometry object represents points, curves, and surfaces in
// coordinate space.  Every Geometry object is a GeoJSON object no
// matter where it occurs in a GeoJSON text.
//
// -  The value of a Geometry object's "type" member MUST be one of the
// seven geometry types (see Section 1.4).
//
// -  A GeoJSON Geometry object of any type other than
// "GeometryCollection" has a member with the name "coordinates".
// The value of the "coordinates" member is an array.  The structure
// of the elements in this array is determined by the type of
// geometry.  GeoJSON processors MAY interpret Geometry objects with
// empty "coordinates" arrays as null objects.
//
// A line between two positions is a straight Cartesian line, the
// shortest line between those two points in the coordinate reference
// system (see Section 4).
//
// In other words, every point on a line that does not cross the
// antimeridian between a point (lon0, lat0) and (lon1, lat1) can be
// calculated as
//
// F(lon, lat) = (lon0 + (lon1 - lon0) * t, lat0 + (lat1 - lat0) * t)
//
// with t being a real number greater than or equal to 0 and smaller
// than or equal to 1.  Note that this line may markedly differ from the
// geodesic path along the curved surface of the reference ellipsoid.
//
// The same applies to the optional height element with the proviso that
// the direction of the height is as specified in the coordinate
// reference system.
//
// Note that, again, this does not mean that a surface with equal height
// follows, for example, the curvature of a body of water.  Nor is a
// surface of equal height perpendicular to a plumb line.
//
// Examples of positions and geometries are provided in Appendix A,
// "Geometry Examples".
@Serializable
sealed class GeometryObject : GeoJsonObject()

@Serializable
@SerialName("GeometryCollection")
data class GeometryCollection(
    val geometries: List<GeoJsonObject>,
) : GeometryObject()

// For type "Point", the "coordinates" member is a single position.
@Serializable
@SerialName("Point")
open class Point(
    val coordinates: Coordinates.Point,
) : GeometryObject() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Point

        if (coordinates != other.coordinates) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinates.hashCode()
    }

    override fun toString(): String {
        return "Point(coordinates=$coordinates)"
    }
}


// For type "MultiPoint", the "coordinates" member is an array of
// positions.
@Serializable
@SerialName("MultiPoint")
open class MultiPoint(
    val coordinates: Coordinates.MultiPoint
) : GeometryObject() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiPoint

        if (coordinates != other.coordinates) return false

        return true
    }
    override fun hashCode(): Int {
        return coordinates.hashCode()
    }
    override fun toString(): String {
        return "MultiPoint(coordinates=$coordinates)"
    }
}


// For type "MultiLineString", the "coordinates" member is an array of
// LineString coordinate arrays.
@Serializable
@SerialName("MultiLineString")
open class MultiLineString(
    val coordinates: Coordinates.MultiLineString
) : GeometryObject()

// -  For type "Polygon", the "coordinates" member MUST be an array of
//    linear ring coordinate arrays.
//
// -  For Polygons with more than one of these rings, the first MUST be
//    the exterior ring, and any others MUST be interior rings.  The
//    exterior ring bounds the surface, and the interior rings (if
//    present) bound holes within the surface.
class Polygon private constructor(
    val coordinates: Coordinates.Polygon
) : GeometryObject() {
    init {
        require(coordinates.value.size > 1) { "must contain at at least one linestring" }
        val outerRing = coordinates.value.first()
        val rest = coordinates.value.drop(1)
        require(rest.all { outerRing.contains(it) }) { "must contain at at least one linestring" }
    }
}

data class MultiPolygon(
    val coordinates: Coordinates.MultiPolygon
) : GeometryObject()

// For type "LineString", the "coordinates" member is an array of two or
// more positions.
@Serializable
@SerialName("LineString")
open class LineString(val coordinates: Coordinates.LineString) : GeometryObject() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as LineString

        if (coordinates != other.coordinates) return false

        return true
    }
    override fun hashCode(): Int {
        return coordinates.hashCode()
    }
    override fun toString(): String {
        return "LineString(coordinates=$coordinates)"
    }
}