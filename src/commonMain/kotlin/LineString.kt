import kotlinx.serialization.Serializable

// For type "LineString", the "coordinates" member is an array of two or
// more positions.
@Serializable
open class LineString private constructor(
    override val coordinates: Coordinates.LineString,
    override val type: GeometryObject.GeometryObjectType.LineString
) : GeometryObject.CoordinateBased {
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