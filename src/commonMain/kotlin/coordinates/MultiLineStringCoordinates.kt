package coordinates

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MultiLineStringCoordinates.Serializer::class)
data class MultiLineStringCoordinates(val value: List<LineStringCoordinates>) : Coordinates() {
    object Serializer : KSerializer<MultiLineStringCoordinates> {
        override fun deserialize(decoder: Decoder): MultiLineStringCoordinates {
            val list = decoder.decodeSerializableValue(ListSerializer(LineStringCoordinates.serializer()))
            return MultiLineStringCoordinates(list)
        }

        override val descriptor: SerialDescriptor =
            SerialDescriptor("MultiLineString", ListSerializer(LineStringCoordinates.serializer()).descriptor)

        override fun serialize(encoder: Encoder, value: MultiLineStringCoordinates) {
            encoder.encodeSerializableValue(ListSerializer(LineStringCoordinates.serializer()), value.value)
        }
    }
}