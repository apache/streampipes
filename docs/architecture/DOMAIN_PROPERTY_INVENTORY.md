# Domain Property Inventory

This document lists hard-coded domain properties found in the Apache StreamPipes repository, as part of the analysis for Issue #1373.

## Findings

| File Path | Component | Domain Property String | Context |
| :--- | :--- | :--- | :--- |
| `streampipes-sdk/.../helpers/EpRequirements.java` | `EpRequirements` | `http://schema.org/DateTime` | Method `timestampReq()` returns a property with this semantic type. |
| `geo-jvm/.../BufferPointProcessor.java` | `BufferPointProcessor` | `http://www.opengis.net/ont/geosparql#Geometry` | Used in `semanticTypeReq` for geometry requirement. |
| `geo-jvm/.../BufferPointProcessor.java` | `BufferPointProcessor` | `http://data.ign.fr/def/ignf#CartesianCS` | Used in `semanticTypeReq` for coordinate system requirement. |
| `geo-jvm/.../LatLngToJtsPointProcessor.java` | `LatLngToJtsPointProcessor` | `http://data.ign.fr/def/ignf#CartesianCS` | Used in `semanticTypeReq`. |
| `geo-jvm/.../LatLngToJtsPointProcessor.java` | `LatLngToJtsPointProcessor` | `http://www.opengis.net/ont/geosparql#Geometry` | Used in `.semanticType(...)` builder. |
| `geo-jvm/.../EpsgProcessor.java` | `EpsgProcessor` | `http://data.ign.fr/def/ignf#CartesianCS` | Used in `.semanticType(...)` builder. |
| `geo-jvm/.../ReprojectionProcessor.java` | `ReprojectionProcessor` | `http://www.opengis.net/ont/geosparql#Geometry` | Used in `semanticTypeReq`. |
| `geo-jvm/.../ReprojectionProcessor.java` | `ReprojectionProcessor` | `http://data.ign.fr/def/ignf#CartesianCS` | Used in `semanticTypeReq`. |
| `geo-jvm/.../BufferGeomProcessor.java` | `BufferGeomProcessor` | `http://www.opengis.net/ont/geosparql#Geometry` | Used in `semanticTypeReq`. |
| `geo-jvm/.../BufferGeomProcessor.java` | `BufferGeomProcessor` | `http://data.ign.fr/def/ignf#CartesianCS` | Used in `semanticTypeReq`. |
| `geo-jvm/.../TopologyValidationProcessor.java` | `TopologyValidationProcessor` | `http://www.opengis.net/ont/geosparql#Geometry` | Used in `semanticTypeReq`. |
| `geo-jvm/.../TopologyValidationProcessor.java` | `TopologyValidationProcessor` | `http://data.ign.fr/def/ignf#CartesianCS` | Used in `semanticTypeReq`. |
| `geo-jvm/.../GeometryValidationProcessor.java` | `GeometryValidationProcessor` | `http://www.opengis.net/ont/geosparql#Geometry` | Used in `semanticTypeReq`. |
| `geo-jvm/.../GeometryValidationProcessor.java` | `GeometryValidationProcessor` | `http://data.ign.fr/def/ignf#CartesianCS` | Used in `semanticTypeReq`. |
| `image-processing-jvm/.../GenericImageClassificationProcessor.java` | `GenericImageClassificationProcessor` | `https://image.com` | Used in `semanticTypeReq` for image input. |
| `image-processing-jvm/.../GenericImageClassificationProcessor.java` | `GenericImageClassificationProcessor` | `https://schema.org/score` | Used in output property definition. |
| `image-processing-jvm/.../GenericImageClassificationProcessor.java` | `GenericImageClassificationProcessor` | `https://schema.org/category` | Used in output property definition. |
| `image-processing-jvm/.../QrCodeReaderProcessor.java` | `QrCodeReaderProcessor` | `https://image.com` | Used in `semanticTypeReq`. |
| `image-processing-jvm/.../RequiredBoxStream.java` | `RequiredBoxStream` | `https://image.com` | Used in `semanticTypeReq`. |

## Summary of URIs

- **GeoSPARQL**: `http://www.opengis.net/ont/geosparql#Geometry` (7 occurrences)
- **IGNF**: `http://data.ign.fr/def/ignf#CartesianCS` (7 occurrences)
- **Schema.org**:
  - `http://schema.org/DateTime` (1 occurrence in SDK)
  - `https://schema.org/score` (1 occurrence)
  - `https://schema.org/category` (1 occurrence)
- **Custom/Placeholder**: `https://image.com` (3 occurrences)

## Observations

- `EpRequirements.timestampReq` uses a string literal for `http://schema.org/DateTime`. The SDK vocabulary `SO` contains a constant `SO.DATE_TIME`.
- The `Geo` vocabulary class contains constants for `LAT`, `LNG`, and `ALT`.
- `https://image.com` is used in image processing components.
