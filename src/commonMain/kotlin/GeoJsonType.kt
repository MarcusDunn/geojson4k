// As another shorthand notation, the term "GeoJSON types" refers to
// nine case-sensitive strings: "Feature", "FeatureCollection", and
// the geometry types listed above.
sealed class GeoJsonType {
    abstract val type: String
    object Feature : GeoJsonType() {
        override val type = "Feature"
    }

    object FeatureCollection : GeoJsonType() {
        override val type = "FeatureCollection"
    }
}

