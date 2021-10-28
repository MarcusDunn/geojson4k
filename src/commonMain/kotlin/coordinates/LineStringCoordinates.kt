package coordinates

import Position
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.math.*

// For type "LineString", the "coordinates" member is an array of two or
// more positions.
@Serializable(with = LineStringCoordinates.Serializer::class)
sealed class LineStringCoordinates : Coordinates() {
    object Serializer : KSerializer<LineStringCoordinates> {
        override val descriptor: SerialDescriptor =
            SerialDescriptor("LineString", ListSerializer(Position.Serializer).descriptor)

        override fun deserialize(decoder: Decoder): LineStringCoordinates {
            val array2d = decoder.decodeSerializableValue(ListSerializer(Position.Serializer))
            return LineStringCoordinates(array2d.map { it })
        }

        override fun serialize(encoder: Encoder, value: LineStringCoordinates) {
            val coords = when (value) {
                is LineStringCoordinatesLinearRing -> value.value
                is LineStringCoordinatesStandard -> value.value
            }
            encoder.encodeSerializableValue(ListSerializer(Position.Serializer), coords)
        }
    }

    companion object {
        operator fun invoke(value: List<Position>): LineStringCoordinates {
            return runCatching { LineStringCoordinatesLinearRing(value) }.getOrElse {
                LineStringCoordinatesStandard(
                    value
                )
            }
        }
    }
}

@Serializable(with = LineStringCoordinates.Serializer::class)
data class LineStringCoordinatesStandard(val value: List<Position>) : LineStringCoordinates() {
    init {
        require(value.size >= 2) { "For type \"LineString\", the \"coordinates\" member is an array of two or more positions." }
    }
}

@Serializable(with = LineStringCoordinates.Serializer::class)
data class LineStringCoordinatesLinearRing(val value: List<Position>) : LineStringCoordinates() {
    init {
        require(value.size >= 4)
        { "linear rings must have at least 4 points" }
        require(value.first() == value.last())
        { "linear rings must have the first element match the last" }
    }

    // TODO: Bentley–Ottmann_algorithm
    fun contains(other: LineStringCoordinatesLinearRing): Boolean {
        val otherVectors = other.toVectors()
        val vectors = this.toVectors()
        for ((p, r) in vectors) {
            val range = p.x..(p.x + r.x)
            for ((q, s) in otherVectors) {
                if (q.x in range || q.x + s.x in range) {
                    val rCrossS = r cross s
                    val qMinusPCrossR = (q - p) cross r
                    val u = qMinusPCrossR / rCrossS
                    val t = ((q - p) cross s) / (r cross s)
                    return if (rCrossS.absoluteValue < 0.0000001f && qMinusPCrossR.absoluteValue < 0.00000001f) {
                        val t1 = ((q + s - p) dot r) / (r dot r)
                        val t0 = ((q - p) dot r) / (r dot r)
                        if ((1f in t0..t1) or (0f in t0..t1)) {
                            true
                        } else {
                            continue
                        }
                    } else if (rCrossS.absoluteValue > 0.0000001f && (t in 0f..1f) && (u in 0f..1f)) {
                        true
                    } else {
                        continue
                    }
                } else {
                    continue
                }
            }
        }
        return true
    }

    private fun toVectors(): List<Pair<Pos2, Vec2>> {
        return (this.value.drop(1) + this.value.first())
            .windowed(2)
            .map { list -> list.first() to list.last() }
            .fold(emptyList()) { acc, pair ->
                acc + (Pos2(pair.first) to Vec2(
                    pair.first.latitude - pair.second.latitude,
                    pair.first.longitude - pair.second.longitude
                ))
            }
    }

    data class Vec2(val x: Float, val y: Float) {
        infix fun cross(v: Vec2) = (this.x * v.y) - (this.y * v.x)
        operator fun minus(v: Pos2) = Vec2(x + v.x, y + v.y)
        infix fun dot(v: Vec2) = x * v.x + y * v.y

        constructor(position: Position) : this(position.latitude, position.longitude)
    }

    data class Pos2(val x: Float, val y: Float) {
        operator fun minus(p: Pos2) = Vec2(x - p.x, y - p.y)
        operator fun plus(p: Vec2) = Vec2(x + p.x, y + p.y)

        constructor(position: Position) : this(position.latitude, position.longitude)
    }

    val isClockwise: Boolean by lazy {
        val (index, _) = this.value.drop(1).dropLast(1)
            .foldIndexed(0 to this.value[0]) { index, acc, position ->
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
        val noRepeats = this.value.drop(1)
        val a = noRepeats[(index - 1).mod(noRepeats.size)]
        val b = noRepeats[index]
        val c = noRepeats[(index + 1).mod(noRepeats.size)]
        val v1 = a.latitude - b.latitude to a.longitude - b.longitude
        val v2 = b.latitude - c.latitude to b.longitude - c.longitude
        val magv1 = sqrt(v1.second.pow(2) + v1.second.pow(2))
        val magv2 = sqrt(v2.second.pow(2) + v2.second.pow(2))
        val dot = v1.first * v2.first + v2.second * v2.second
        val angle = acos(dot / (magv1 * magv2))
        (magv1 * magv2 * sin(angle)) > 0
    }
}