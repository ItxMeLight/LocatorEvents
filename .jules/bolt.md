## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-07-01 - [Configuration and Resource Caching]
**Learning:** Frequent YAML lookups and Enum parsing (valueOf) in high-frequency tasks (like 20Hz ticks) create significant CPU overhead. Additionally, repeated application of ItemStack metadata for MapView changes is redundant as MapView state is persistent.
**Action:** Implement field-based caching for configuration values and pre-parse expensive resources (Enums, GameRules) during plugin enable/reload. Use O(1) data structures (HashSet) for membership lookups.
