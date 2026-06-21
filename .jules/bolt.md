## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-21 - [Maven Central Rate Limiting & Field-based Caching]
**Learning:** Maven Central 429 "Too Many Requests" errors can halt the development cycle in this environment. Typed field-based caching in ConfigManager not only saves CPU cycles by avoiding YAML traversal but also improves type safety and reduces string-to-enum parsing overhead in hot paths (1Hz-20Hz).
**Action:** If Maven builds fail due to rate limits, use a temporary mirror (e.g., Aliyun) in a local `settings.xml` but ENSURE it is deleted before submission. Always pre-parse Enums and use HashSets for O(1) collection lookups.
