sealed class FeatureObject {
    val type: Type = Type.Feature
    abstract val geometry: GeometryObject?
    abstract val properties: JsonObject

    enum class Type {
        Feature
    }

    class JsonObject {
        init {
            TODO()
        }
    }

    data class Standard(
        override val geometry: GeometryObject?,
        override val properties: JsonObject
    ) : FeatureObject()

    data class WithId(
        override val geometry: GeometryObject?,
        override val properties: JsonObject,
        val id: FeatureObjectId,
    ) : FeatureObject() {

        // TODO change to kotlinx serialization nodes
        sealed class FeatureObjectId {
            data class StringId(val inner: String?) : FeatureObjectId()
            data class JsonNumberId(val inner: Float) : FeatureObjectId()
        }
    }

}