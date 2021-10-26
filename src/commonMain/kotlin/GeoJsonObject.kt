// A GeoJSON object represents a Geometry, Feature, or collection of
// Features.
// -  A GeoJSON object is a JSON object.
//
// -  A GeoJSON object has a member with the name "type".  The value of
// the member MUST be one of the GeoJSON types.
//
// -  A GeoJSON object MAY have a "bbox" member, the value of which MUST
// be a bounding box array (see Section 5).
//
// -  A GeoJSON object MAY have other members (see Section 6).
sealed class GeoJsonObject {
    abstract val type: GeoJsonType
}


