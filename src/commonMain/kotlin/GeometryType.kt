//Inside this document, the term "geometry type" refers to seven
//case-sensitive strings: "Point", "MultiPoint", "LineString",
//"MultiLineString", "Polygon", "MultiPolygon", and
//"GeometryCollection".
sealed class GeometryType : GeoJsonType() {
    abstract override val type: String

    object Point : GeometryType() {
        override val type = "Point"
    }

    object MultiPoint : GeometryType() {
        override val type = "MultiPoint"
    }

    object LineString : GeometryType() {
        override val type = "LineString"
    }

    object MultiLineString : GeometryType() {
        override val type = "MultiLineString"
    }

    object Polygon : GeometryType() {
        override val type = "Polygon"
    }

    object MultiPolygon : GeometryType() {
        override val type = "MultiPolygon"
    }

    object GeometryCollection : GeometryType() {
        override val type = "GeometryCollection"
    }
}
