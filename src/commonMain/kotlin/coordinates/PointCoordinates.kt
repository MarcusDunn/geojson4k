package coordinates

import Position
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.FloatArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// For type "Point", the "coordinates" member is a single position.
@Serializable(with = PointCoordinates.Serializer::class)
data class PointCoordinates(val value: Position) : Coordinates() {
    object Serializer : KSerializer<PointCoordinates> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Point", FloatArraySerializer().descriptor)

        override fun deserialize(decoder: Decoder): PointCoordinates {
            return PointCoordinates(Position(*decoder.decodeSerializableValue(FloatArraySerializer())))
        }

        override fun serialize(encoder: Encoder, value: PointCoordinates) {
            encoder.encodeSerializableValue(Position.Serializer, value.value)
        }
    }
}