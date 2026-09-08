# AGENTS Guide (Storage CouchDB)

## Scope

Applies to `streampipes-storage-couchdb/`. Root `AGENTS.md` applies as well; build and
validation commands live there.

## Module intent

- CouchDB implementation of the `streampipes-storage-api` interfaces (`impl/*`, `dao/*`,
  `serializer/*`), wired centrally in `CouchDbStorageManager`.

## High-risk areas

- DAO, query and view behaviour (`dao`, `CouchDbViewGenerator`). Views are created on
  startup; a changed view needs a migration in `streampipes-service-core` (see
  `AddDataLakeMeasureViewMigration` and the other `Add*ViewMigration` classes).
- Serialization compatibility (`serializer/*`) for already-persisted documents.

## Recipe: add a storage type

1. Define the interface in `streampipes-storage-api`.
2. Implement it under `impl/` (DAO under `dao/` if it needs custom queries or views).
3. Expose it through `CouchDbStorageManager` and the matching `streampipes-storage-api`
   abstraction so management modules obtain it through the manager, never directly.

## Rules

- Keep persisted documents and views readable by the previous version unless the change
  ships with a migration.
