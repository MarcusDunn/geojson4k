import kotlinx.serialization.Serializable

// For type "Point", the "coordinates" member is a single position.
@Serializable
open class Point private constructor(
    override val coordinates: Coordinates.Point,
    override val type: GeometryObject.GeometryObjectType.Point
) : GeometryObject.CoordinateBased {
    constructor(coordinates: Coordinates.Point) : this(coordinates, GeometryObject.GeometryObjectType.Point)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Point

        if (coordinates != other.coordinates) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinates.hashCode()
    }

    override fun toString(): String {
        return "Point(coordinates=$coordinates)"
    }
}