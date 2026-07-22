# PaperShelf Architecture

PaperShelf is an offline-first Android app organized as a small Clean Architecture project.

## Modules

- `app`: Android entry point, Hilt application, activity shell, and future navigation graph.
- `core`: shared UI theme, architecture primitives, dispatchers, and lightweight common utilities.
- `domain`: pure Kotlin models and repository contracts.
- `database`: Room database, entities, DAOs, converters, and schema exports.
- `data`: repository implementations, file scanning, metadata extraction, and cache coordination.
- `library`: library, home, favorites, statistics, and book info UI.
- `reader`: PDF and EPUB reader UI/adapters around MuPDF and Readium.
- `settings`: settings UI and settings state.

## Dependency Direction

`app` depends on every feature module.

Feature modules depend on `core` and `domain`.

`data` depends on `database`, `domain`, and `core`.

`database` does not depend on UI or feature modules.

`domain` does not depend on Android.

## Offline Boundary

The app must not add internet permissions, account flows, telemetry, ads, or background services. File scanning should be explicit and bounded: app start or user request.

## Performance Boundary

Scanning, metadata extraction, thumbnail generation, and persistence must run off the main thread through coroutines. UI surfaces should expose Flow-backed state and use lazy collections.
