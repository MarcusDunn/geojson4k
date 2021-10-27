import kotlinx.serialization.Serializable

@Serializable
open class MultiPolygon private constructor(
    override val coordinates: Coordinates.MultiPolygon,
    override val type: GeometryObject.GeometryObjectType.MultiPolygon
) : GeometryObject.CoordinateBased {
    constructor(coordinates: Coordinates.MultiPolygon) : this(
        coordinates,
        GeometryObject.GeometryObjectType.MultiPolygon
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MultiPolygon

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
        return "MultiPolygon(coordinates=$coordinates, type=$type)"
    }
}