## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.
## 2026-06-30 - [HashSet for World Lookups]
**Learning:** Frequent world membership checks using a `List.contains` (O(N)) in event handlers can become a bottleneck as the number of configured worlds or online players grows. Transitioning to a `HashSet` provides O(1) lookups.
**Action:** Always prefer `HashSet` for frequently queried membership lists, and cache the unmodifiable wrapper to avoid redundant object allocation.
