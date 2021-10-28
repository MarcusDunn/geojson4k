package coordinates

import Position
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// For type "MultiPoint", the "coordinates" member is an array of
// positions.
@Serializable(with = MultiPointCoordinates.Serializer::class)
data class MultiPointCoordinates(val value: List<Position>) : Coordinates() {
    object Serializer : KSerializer<MultiPointCoordinates> {
        override val descriptor: SerialDescriptor =
            SerialDescriptor("MultiPoint", ListSerializer(Position.Serializer).descriptor)

        override fun deserialize(decoder: Decoder): MultiPointCoordinates {
            val array2d = decoder.decodeSerializableValue(ListSerializer(Position.Serializer))
            return MultiPointCoordinates(array2d.map { it })
        }

        override fun serialize(encoder: Encoder, value: MultiPointCoordinates) {
            encoder.encodeSerializableValue(ListSerializer(Position.Serializer), value.value)
        }
    }
}