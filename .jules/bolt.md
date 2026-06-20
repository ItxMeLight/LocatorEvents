## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-20 - [Redundant Metadata & GameRule Updates]
**Learning:** Modifying `MapView` state (e.g., `setTrackingPosition`) is persistent for the map ID; calling `setItemMeta()` afterwards is redundant and triggers expensive NMS metadata cloning. Similarly, frequent `setGameRule` calls without checking the current value can cause unnecessary internal state updates and packet broadcasts.
**Action:** Always check the current state before applying updates to `MapView` or `GameRules`. Skip `setItemMeta` when only modifying the underlying map view.
