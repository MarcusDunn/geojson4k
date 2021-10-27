import kotlinx.serialization.KSerializer
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

    interface CoordinateBased : GeometryObject {
        val coordinates: Coordinates
    }

    interface CollectionBased : GeometryObject {
        val geometries: List<GeoJsonObject>
    }
}


