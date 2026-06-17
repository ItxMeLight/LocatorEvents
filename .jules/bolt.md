## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-17 - [Redundant Config Lookups & Disallowed Map Logic]
**Learning:** Frequent configuration access via `FileConfiguration.get()` in high-frequency tasks (like BossBar updates or particle spawning) creates significant overhead due to string parsing and Map lookups. Additionally, inventory-based map tracking (scanning every item on every click/pickup) is extremely inefficient compared to modern GameRule-based solutions.
**Action:** Always cache configuration values in private fields during `reload()`. Use `HashSet` for O(1) world checks. Favor global server-side toggles (like GameRules) over scanning player inventories.
