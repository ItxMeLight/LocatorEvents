## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-22 - [Config and Enum Caching]
**Learning:** Accessing `FileConfiguration` methods and parsing enums (like `Particle`, `Sound`, `BossBar.Color`) repeatedly in high-frequency tasks (e.g., 20Hz update loops or per-player iterations) creates measurable CPU overhead. $O(N)$ list lookups for world whitelists further degrade performance during event checks.
**Action:** Implement field-based caching for all configuration values and pre-parse Enums during initialization and reloads. Use `HashSet` for $O(1)$ lookup performance in world lists and return unmodifiable views to preserve cache integrity.
