import coordinates.Coordinates
import coordinates.MultiPointCoordinates
import kotlinx.serialization.Serializable

// For type "MultiPoint", the "coordinates" member is an array of
// positions.
@Serializable
open class MultiPoint private constructor(
    override val coordinates: MultiPointCoordinates,
    override val type: GeometryObject.GeometryObjectType.MultiPoint
) : GeometryObject.CoordinateBased {
    constructor(coordinates: MultiPointCoordinates) : this(coordinates, GeometryObject.GeometryObjectType.MultiPoint)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiPoint

        if (coordinates != other.coordinates) return false

        return true
    }

    override fun hashCode(): Int {
        return coordinates.hashCode()
    }

    override fun toString(): String {
        return "MultiPoint(coordinates=$coordinates)"
    }
}