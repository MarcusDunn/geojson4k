import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//Inside this document, the term "geometry type" refers to seven
//case-sensitive strings: "Point", "MultiPoint", "LineString",
//"MultiLineString", "Polygon", "MultiPolygon", and
//"GeometryCollection".
@Serializable(with = GeometryTypeAsString::class)
sealed class GeometryType : GeoJsonType() {
    abstract override val type: String

    companion object {
        operator fun invoke(type: String): GeometryType? {
            return when (type) {
                Point.type -> Point
                MultiPoint.type -> MultiPoint
                LineString.type -> LineString
                MultiLineString.type -> MultiLineString
                Polygon.type -> Polygon
                MultiPolygon.type -> MultiPolygon
                GeometryCollection.type -> GeometryCollection
                else -> null
            }
        }
    }

    @Serializable(with = GeometryTypeAsString::class)
    object Point : GeometryType() {
        override val type = "Point"
    }

    @Serializable(with = GeometryTypeAsString::class)
    object MultiPoint : GeometryType() {
        override val type = "MultiPoint"
    }

    @Serializable(with = GeometryTypeAsString::class)
    object LineString : GeometryType() {
        override val type = "LineString"
    }

    @Serializable(with = GeometryTypeAsString::class)
    object MultiLineString : GeometryType() {
        override val type = "MultiLineString"
    }

    @Serializable(with = GeometryTypeAsString::class)
    object Polygon : GeometryType() {
        override val type = "Polygon"
    }

    @Serializable(with = GeometryTypeAsString::class)
    object MultiPolygon : GeometryType() {
        override val type = "MultiPolygon"
    }

    @Serializable(with = GeometryTypeAsString::class)
    object GeometryCollection : GeometryType() {
        override val type = "GeometryCollection"
    }
}

object GeometryTypeAsString : KSerializer<GeometryType> {
    override fun deserialize(decoder: Decoder): GeometryType {
        val type = decoder.decodeString()
        return GeometryType(type)
            ?: throw SerializationException("failed to parse GeometryType from $type")
    }

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GeometryType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GeometryType) {
        encoder.encodeString(value.type)
    }
}