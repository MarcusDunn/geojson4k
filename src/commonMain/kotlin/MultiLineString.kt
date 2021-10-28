import coordinates.Coordinates
import coordinates.MultiLineStringCoordinates
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// For type "MultiLineString", the "coordinates" member is an array of
// LineString coordinate arrays.
@Serializable
@SerialName("MultiLineString")
open class MultiLineString private constructor(
    override val coordinates: MultiLineStringCoordinates,
    override val type: GeometryObject.GeometryObjectType.MultiLineString
) : GeometryObject.CoordinateBased {
    constructor(coordinates: MultiLineStringCoordinates) : this(coordinates,
        GeometryObject.GeometryObjectType.MultiLineString
    )
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiLineString

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
        return "MultiLineString(coordinates=$coordinates, type=$type)"
    }
}