## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-23 - [Config Caching & Resource Pre-parsing]
**Learning:** Accessing FileConfiguration (tree-based map) and resolving Enums via valueOf() in high-frequency tasks (particle spawning, bossbar updates) adds unnecessary CPU overhead. HashSet is significantly faster for world list lookups than List.contains.
**Action:** Cache all config values in private fields during reload. Pre-parse and cache Particle, Sound, and Color objects once. Store unmodifiable collection wrappers to avoid repeated allocation in getters.
