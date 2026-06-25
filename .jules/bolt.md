## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-25 - [Configuration & Resource Caching]
**Learning:** Frequent access to `FileConfiguration` and repeated `Enum.valueOf()` lookups inside high-frequency task loops (1-tick or 20-tick) create unnecessary main-thread overhead. Converting `List` to `Set` for world lookups reduces O(N) checks to O(1).
**Action:** Implement field-based caching for all configuration values. Cache expensive resource objects (Particle, Sound, BossBar.Color) during initialization and refresh them on reload. Use `HashSet` for O(1) membership checks in world-based event logic.
