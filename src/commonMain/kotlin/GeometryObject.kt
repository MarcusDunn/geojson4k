import kotlinx.serialization.Serializable

// A Geometry object represents points, curves, and surfaces in
// coordinate space.  Every Geometry object is a GeoJSON object no
// matter where it occurs in a GeoJSON text.
//
// -  The value of a Geometry object's "type" member MUST be one of the
// seven geometry types (see Section 1.4).
//
// -  A GeoJSON Geometry object of any type other than
// "GeometryCollection" has a member with the name "coordinates".
// The value of the "coordinates" member is an array.  The structure
// of the elements in this array is determined by the type of
// geometry.  GeoJSON processors MAY interpret Geometry objects with
// empty "coordinates" arrays as null objects.
//
// A line between two positions is a straight Cartesian line, the
// shortest line between those two points in the coordinate reference
// system (see Section 4).
//
// In other words, every point on a line that does not cross the
// antimeridian between a point (lon0, lat0) and (lon1, lat1) can be
// calculated as
//
// F(lon, lat) = (lon0 + (lon1 - lon0) * t, lat0 + (lat1 - lat0) * t)
//
// with t being a real number greater than or equal to 0 and smaller
// than or equal to 1.  Note that this line may markedly differ from the
// geodesic path along the curved surface of the reference ellipsoid.
//
// The same applies to the optional height element with the proviso that
// the direction of the height is as specified in the coordinate
// reference system.
//
// Note that, again, this does not mean that a surface with equal height
// follows, for example, the curvature of a body of water.  Nor is a
// surface of equal height perpendicular to a plumb line.
//
// Examples of positions and geometries are provided in Appendix A,
// "Geometry Examples".
@Serializable
sealed class GeometryObject : GeoJsonObject() {
    abstract override val type: GeometryType


    sealed class CoordinatesGeometryObject : GeometryObject() {
        abstract val coordinates: Coordinates

        // For type "Point", the "coordinates" member is a single position.
        @Serializable
        class Point private constructor(
            override val coordinates: Coordinates.Point,
            override val type: GeometryType.Point,
        ) : CoordinatesGeometryObject() {
            constructor(coordinate: Coordinates.Point) : this(coordinate, GeometryType.Point)

            override fun toString(): String {
                return "Point(coordinates=$coordinates, type=$type)"
            }

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as Point

                if (coordinates != other.coordinates) return false
                if (type != other.type) return false

                return true
            }

            override fun hashCode(): Int {
                var result = coordinates.hashCode()
                result = 31 * result + type.hashCode()
                return result
            }

        }

        // For type "MultiPoint", the "coordinates" member is an array of
        // positions.
        @Serializable
        class MultiPoint private constructor(
            override val type: GeometryType.MultiPoint,
            override val coordinates: Coordinates.MultiPoint
        ) : CoordinatesGeometryObject() {
            constructor(coordinates: Coordinates.MultiPoint) : this(GeometryType.MultiPoint, coordinates)

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other == null || this::class != other::class) return false

                other as MultiPoint

                if (type != other.type) return false
                if (coordinates != other.coordinates) return false

                return true
            }

            override fun hashCode(): Int {
                var result = type.hashCode()
                result = 31 * result + coordinates.hashCode()
                return result
            }

            override fun toString(): String {
                return "MultiPoint(type=$type, coordinates=$coordinates)"
            }

        }


        // For type "LineString", the "coordinates" member is an array of two or
        // more positions.
        @Serializable
        sealed class LineString : CoordinatesGeometryObject() {
            abstract override val type: GeometryType.LineString

            companion object {
                operator fun invoke(coordinates: Coordinates.LineString): LineString? {
                    return LinearRing(coordinates) ?: Standard(coordinates)
                }
            }

            @Serializable
            class Standard private constructor(
                override val coordinates: Coordinates.LineString,
                override val type: GeometryType.LineString
            ) : LineString() {
                companion object {
                    operator fun invoke(coordinate: Coordinates.LineString): Standard? {
                        return if (coordinate.position.size < 2) {
                            null
                        } else {
                            Standard(coordinate, GeometryType.LineString)
                        }
                    }
                }

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other == null || this::class != other::class) return false

                    other as Standard

                    if (coordinates != other.coordinates) return false

                    return true
                }

                override fun hashCode(): Int {
                    return coordinates.hashCode()
                }

                override fun toString(): String {
                    return "Standard(coordinates=$coordinates)"
                }


            }

            @Serializable
            class LinearRing private constructor(
                override val coordinates: Coordinates.LineString,
                override val type: GeometryType.LineString
            ) : LineString() {
                companion object {
                    operator fun invoke(coordinates: Coordinates.LineString): LinearRing? {
                        return if (
                            coordinates.position.first() == coordinates.position.last()
                            && coordinates.position.size >= 4
                            && coordinates.isClockwise()
                        ) {
                            LinearRing(coordinates, GeometryType.LineString)
                        } else {
                            null
                        }
                    }
                }

                fun contains(linearRing: LinearRing): Boolean {
                    TODO("Not yet implemented")
                }

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (other == null || this::class != other::class) return false

                    other as LinearRing

                    if (coordinates != other.coordinates) return false

                    return true
                }

                override fun hashCode(): Int {
                    return coordinates.hashCode()
                }

                override fun toString(): String {
                    return "LinearRing(coordinates=$coordinates)"
                }

            }
        }

        // For type "MultiLineString", the "coordinates" member is an array of
        // LineString coordinate arrays.
        data class MultiLineString(
            override val type: GeometryType.MultiLineString,
            override val coordinates: Coordinates.MultiLineString
        ) : CoordinatesGeometryObject()

        // -  For type "Polygon", the "coordinates" member MUST be an array of
        //    linear ring coordinate arrays.
        //
        // -  For Polygons with more than one of these rings, the first MUST be
        //    the exterior ring, and any others MUST be interior rings.  The
        //    exterior ring bounds the surface, and the interior rings (if
        //    present) bound holes within the surface.
        class Polygon private constructor(
            override val coordinates: Coordinates.Polygon
        ) : CoordinatesGeometryObject() {
            override val type: GeometryType.Polygon = GeometryType.Polygon

            companion object {
                operator fun invoke(coordinates: List<Coordinates.Polygon>): Polygon? {
                    return if (coordinates.size > 1) {
                        val outer = coordinates.first()
                        val rest = coordinates.drop(1)
                        if (rest.all { outer.lineString.contains(it.lineString) }) {
                            Polygon(coordinates)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
            }
        }

        data class MultiPolygon(
            override val type: GeometryType.MultiPolygon,
            override val coordinates: Coordinates.MultiPolygon
        ) : CoordinatesGeometryObject()
    }


    data class GeometryCollection(
        override val type: GeometryType.GeometryCollection,
        val geometries: List<GeoJsonObject>,
    ) : GeometryObject()
}