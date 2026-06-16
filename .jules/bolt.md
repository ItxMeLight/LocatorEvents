## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.
