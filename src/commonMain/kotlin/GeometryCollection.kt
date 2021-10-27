import kotlinx.serialization.Serializable

@Serializable
open class GeometryCollection private constructor(
    override val geometries: List<GeoJsonObject>,
    override val type: GeometryObject.GeometryObjectType.GeometryCollection
) : GeometryObject.CollectionBased {
    constructor(geometries: List<GeoJsonObject>) : this(
        geometries,
        GeometryObject.GeometryObjectType.GeometryCollection
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GeometryCollection

        if (geometries != other.geometries) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = geometries.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    override fun toString(): String {
        return "GeometryCollection(geometries=$geometries, type=$type)"
    }
}