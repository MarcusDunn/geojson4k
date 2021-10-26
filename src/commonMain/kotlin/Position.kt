// A position is the fundamental geometry construct.  The "coordinates"
// member of a Geometry object is composed of either:
//
// -  one position in the case of a Point geometry,
//
// -  an array of positions in the case of a LineString or MultiPoint
// geometry,
//
// -  an array of LineString or linear ring (see Section 3.1.6)
// coordinates in the case of a Polygon or MultiLineString geometry, or
//
// -  an array of Polygon coordinates in the case of a MultiPolygon
// geometry.
//
// A position is an array of numbers.  There MUST be two or more
// elements.  The first two elements are longitude and latitude, or
// easting and northing, precisely in that order and using decimal
// numbers.  Altitude or elevation MAY be included as an optional third
// element.
//
// Implementations SHOULD NOT extend positions beyond three elements
// because the semantics of extra elements are unspecified and
// ambiguous.  Historically, some implementations have used a fourth
// element to carry a linear referencing measure (sometimes denoted as
// "M") or a numerical timestamp, but in most situations a parser will
// not be able to properly interpret these values.  The interpretation
// and meaning of additional elements is beyond the scope of this
// specification, and additional elements MAY be ignored by parsers.
data class Position(val latitude: Double, val longitude: Double, val altitude: Double? = null)