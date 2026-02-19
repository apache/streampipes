# AGENTS Guide (Storage CouchDB)

## Scope
Applies to `streampipes-storage-couchdb/`.

## Module Intent
- CouchDB implementation of storage interfaces (`impl/*`, `dao/*`, `serializer/*`).
- Central wiring via `CouchDbStorageManager`.

## High-Risk Areas
- DAO/query/view behavior (`dao`, `CouchDbViewGenerator`).
- Serialization compatibility (`serializer/*`).
- Storage manager wiring and interface coverage.

## Best Practices
- Keep implementations aligned with `streampipes-storage-api` contracts.
- Preserve document and view compatibility unless migrations are included.
- When adding a storage type, wire it through `CouchDbStorageManager` and corresponding API abstractions.
- Keep serializer changes backward compatible for persisted entities.

## Validation
- `mvn -pl streampipes-storage-couchdb -am test`
