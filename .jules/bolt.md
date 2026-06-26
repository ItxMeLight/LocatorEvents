## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-26 - [Field-based Caching & Resource Pre-parsing]
**Learning:** Repeatedly fetching values from FileConfiguration and parsing Strings into Enums (Particle, Sound, BossBar types) inside tasks or frequent events is a CPU bottleneck and creates garbage.
**Action:** Implement field-based caching in ConfigManager and pre-parse/cache resource objects in their respective managers. Use HashSet for world lookups to ensure O(1) membership checks during event processing.
