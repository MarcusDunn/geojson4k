import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
    data class LineString(val position: Position) : Coordinates()

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

object MultiPointAs2DArraySerializer : KSerializer<Coordinates.MultiPoint> {
    override val descriptor: SerialDescriptor = SerialDescriptor("MultiPoint", ListSerializer(FloatArraySerializer()).descriptor)

    override fun deserialize(decoder: Decoder): Coordinates.MultiPoint {
        val array2d = decoder.decodeSerializableValue(ListSerializer(FloatArraySerializer()))
        return Coordinates.MultiPoint(array2d.map { Position(*it) })
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
