import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
sealed interface GeometryObject {
    val type: GeometryObjectType

    @Serializable(with = GeometryObjectType.Serializer::class)
    sealed class GeometryObjectType {
        abstract val name: String

        object Serializer : KSerializer<GeometryObjectType> {
            override fun deserialize(decoder: Decoder): GeometryObjectType {
                return when (val type = decoder.decodeString()) {
                    LineString.name -> LineString
                    MultiPolygon.name -> MultiPolygon
                    GeometryCollection.name -> GeometryCollection
                    Polygon.name -> Polygon
                    MultiLineString.name -> MultiLineString
                    Point.name -> Point
                    MultiPoint.name -> MultiPoint
                    else -> throw SerializationException("Invalid GeometryObjectType $type")
                }
            }

            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("GeometryObjectType", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: GeometryObjectType) {
                encoder.encodeString(value = value.name)
            }

        }


        @Serializable(with = Serializer::class)
        object LineString : GeometryObjectType() {
            override val name = "LineString"
        }

        @Serializable(with = Serializer::class)
        object MultiPolygon : GeometryObjectType() {
            override val name = "MultiPolygon"
        }

        @Serializable(with = Serializer::class)
        object GeometryCollection : GeometryObject.GeometryObjectType() {
            override val name = "GeometryCollection"
        }

        @Serializable(with = Serializer::class)
        object Polygon : GeometryObject.GeometryObjectType() {
            override val name = "Polygon"
        }

        @Serializable(with = Serializer::class)
        object MultiLineString : GeometryObject.GeometryObjectType() {
            override val name = "MultiLineString"
        }

        @Serializable(with = Serializer::class)
        object Point : GeometryObject.GeometryObjectType() {
            override val name = "Point"
        }

        @Serializable(with = Serializer::class)
        object MultiPoint : GeometryObjectType() {
            override val name = "MultiPoint"
        }
    }

    interface Coordinate : GeometryObject {
        val coordinates: Coordinates
    }

    interface Collection : GeometryObject {
        val geometries: List<GeoJsonObject>
    }
}

@Serializable
open class GeometryCollection private constructor(
    override val geometries: List<GeoJsonObject>,
    override val type: GeometryObject.GeometryObjectType.GeometryCollection
) : GeometryObject.Collection {
    constructor(geometries: List<GeoJsonObject>) : this(
        geometries,
        GeometryObject.GeometryObjectType.GeometryCollection
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GeometryCollection

        if (geometries != other.geometries) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = geometries.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "GeometryCollection(geometries=$geometries, type=$type)"
    }
}

// For type "Point", the "coordinates" member is a single position.
@Serializable
open class Point private constructor(
    override val coordinates: Coordinates.Point,
    override val type: GeometryObject.GeometryObjectType.Point
) : GeometryObject.Coordinate {
    constructor(coordinates: Coordinates.Point) : this(coordinates, GeometryObject.GeometryObjectType.Point)

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
open class MultiPoint private constructor(
    override val coordinates: Coordinates.MultiPoint,
    override val type: GeometryObject.GeometryObjectType.MultiPoint
) : GeometryObject.Coordinate {
    constructor(coordinates: Coordinates.MultiPoint) : this(coordinates, GeometryObject.GeometryObjectType.MultiPoint)

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
    override val coordinates: Coordinates.MultiLineString,
    override val type: GeometryObject.GeometryObjectType.MultiLineString
) : GeometryObject.Coordinate

// -  For type "Polygon", the "coordinates" member MUST be an array of
//    linear ring coordinate arrays.
//
// -  For Polygons with more than one of these rings, the first MUST be
//    the exterior ring, and any others MUST be interior rings.  The
//    exterior ring bounds the surface, and the interior rings (if
//    present) bound holes within the surface.
open class Polygon private constructor(
    override val coordinates: Coordinates.Polygon,
    override val type: GeometryObject.GeometryObjectType.Polygon

) : GeometryObject.Coordinate {
    init {
        require(coordinates.value.size > 1) { "must contain at at least one linestring" }
        val outerRing = coordinates.value.first()
        val rest = coordinates.value.drop(1)
        require(rest.all { outerRing.contains(it) }) { "must contain at at least one linestring" }
    }
}

@Serializable
open class MultiPolygon private constructor(
    override val coordinates: Coordinates.MultiPolygon,
    override val type: GeometryObject.GeometryObjectType.MultiPolygon
) : GeometryObject.Coordinate {
    constructor(coordinates: Coordinates.MultiPolygon) : this(
        coordinates,
        GeometryObject.GeometryObjectType.MultiPolygon
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiPolygon

        if (coordinates != other.coordinates) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coordinates.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "MultiPolygon(coordinates=$coordinates, type=$type)"
    }
}

// For type "LineString", the "coordinates" member is an array of two or
// more positions.
@Serializable
open class LineString private constructor(
    override val coordinates: Coordinates.LineString,
    override val type: GeometryObject.GeometryObjectType.LineString
) : GeometryObject.Coordinate {
    constructor(coordinates: Coordinates.LineString) : this(coordinates, GeometryObject.GeometryObjectType.LineString)

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