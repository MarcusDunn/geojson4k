import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.*

sealed class Coordinates {
    // For type "Point", the "coordinates" member is a single position.
    @Serializable(with = PointAsFloatArray::class)
    data class Point(val position: Position) : Coordinates()

    // For type "MultiPoint", the "coordinates" member is an array of
    // positions.
    @Serializable(with = MultiPointAs2DArraySerializer::class)
    data class MultiPoint(val position: List<Position>) : Coordinates()

    // For type "LineString", the "coordinates" member is an array of two or
    // more positions.
    @Serializable(with = LineStringAs2DArraySerializer::class)
    data class LineString(val position: List<Position>) : Coordinates() {
        init {
            require(position.size >= 2) { "For type \"LineString\", the \"coordinates\" member is an array of two or more positions." }
        }

        fun isClockwise(): Boolean {
            val (index, _) = this.position.drop(1).dropLast(1)
                .foldIndexed(0 to this.position[0]) { index, acc, position ->
                    when (acc.second.latitude.compareTo(position.latitude).sign) {
                        -1 -> acc
                        0 -> when (acc.second.longitude.compareTo(position.longitude).sign) {
                            1 -> acc
                            -1 -> index + 1 to position
                            else -> throw IllegalStateException("Cannot have two equals points on the coordinates")
                        }
                        1 -> index + 1 to position
                        else -> throw IllegalStateException("Impossible to reach")
                    }
                }
            val noRepeats = this.position.drop(1)
            val a = noRepeats[(index - 1).mod(noRepeats.size)]
            val b = noRepeats[index]
            val c = noRepeats[(index + 1).mod(noRepeats.size)]
            val v1 = a.latitude - b.latitude to a.longitude - b.longitude
            val v2 = b.latitude - c.latitude to b.longitude - c.longitude
            val magv1 = sqrt(v1.second.pow(2) + v1.second.pow(2))
            val magv2 = sqrt(v2.second.pow(2) + v2.second.pow(2))
            val dot = v1.first * v2.first + v2.second * v2.second
            val angle = acos(dot / (magv1 * magv2))
            return (magv1 * magv2 * sin(angle)) > 0
        }
    }

    // For type "MultiLineString", the "coordinates" member is an array of
    // LineString coordinate arrays.
    data class MultiLineString(val lineString: List<GeometryObject.CoordinatesGeometryObject.LineString>) :
        Coordinates()

    // -  specify a constraint specific to Polygons, it is useful to
    //    introduce the concept of a linear ring:
    //
    // -  A linear ring is a closed LineString with four or more positions.
    //
    // -  The first and last positions are equivalent, and they MUST contain
    //    identical values; their representation SHOULD also be identical.
    //
    // -  A linear ring is the boundary of a surface or the boundary of a
    //    hole in a surface.
    //
    // -  A linear ring MUST follow the right-hand rule with respect to the
    //    area it bounds, i.e., exterior rings are counterclockwise, and
    //    holes are clockwise.
    //
    // Note: the [GJ2008] specification did not discuss linear ring winding
    // order.  For backwards compatibility, parsers SHOULD NOT reject
    // Polygons that do not follow the right-hand rule.
    //
    // Though a linear ring is not explicitly represented as a GeoJSON
    // geometry type, it leads to a canonical formulation of the Polygon
    // geometry type definition as follows:
    //
    // -  For type "Polygon", the "coordinates" member MUST be an array of
    //    linear ring coordinate arrays.
    //
    // -  For Polygons with more than one of these rings, the first MUST be
    //    the exterior ring, and any others MUST be interior rings.  The
    //    exterior ring bounds the surface, and the interior rings (if
    //    present) bound holes within the surface.
    data class Polygon(val lineString: GeometryObject.CoordinatesGeometryObject.LineString.LinearRing) : Coordinates()
    data class MultiPolygon(val polygons: List<Coordinates.Polygon>) : Coordinates()
}

object LineStringAs2DArraySerializer : KSerializer<Coordinates.LineString> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("LineString", ListSerializer(PositionAsFloatArraySerializer).descriptor)

    override fun deserialize(decoder: Decoder): Coordinates.LineString {
        val array2d = decoder.decodeSerializableValue(ListSerializer(PositionAsFloatArraySerializer))
        return Coordinates.LineString(array2d.map { it })
    }

    override fun serialize(encoder: Encoder, value: Coordinates.LineString) {
        encoder.encodeSerializableValue(ListSerializer(PositionAsFloatArraySerializer), value.position)
    }

}

object MultiPointAs2DArraySerializer : KSerializer<Coordinates.MultiPoint> {
    override val descriptor: SerialDescriptor =
        SerialDescriptor("MultiPoint", ListSerializer(PositionAsFloatArraySerializer).descriptor)

    override fun deserialize(decoder: Decoder): Coordinates.MultiPoint {
        val array2d = decoder.decodeSerializableValue(ListSerializer(PositionAsFloatArraySerializer))
        return Coordinates.MultiPoint(array2d.map { it })
    }

    override fun serialize(encoder: Encoder, value: Coordinates.MultiPoint) {
        encoder.encodeSerializableValue(ListSerializer(PositionAsFloatArraySerializer), value.position)
    }

}

object PointAsFloatArray : KSerializer<Coordinates.Point> {
    override val descriptor: SerialDescriptor = SerialDescriptor("Point", FloatArraySerializer().descriptor)

    override fun deserialize(decoder: Decoder): Coordinates.Point {
        return Coordinates.Point(Position(*decoder.decodeSerializableValue(FloatArraySerializer())))
    }

    override fun serialize(encoder: Encoder, value: Coordinates.Point) {
        encoder.encodeSerializableValue(PositionAsFloatArraySerializer, value.position)
    }
}
