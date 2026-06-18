## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-18 - [Config & Resource Caching]
**Learning:** Frequent configuration lookups and enum resolution (Sound/Particle.valueOf) in tasks running every tick or for every player create significant CPU overhead. List.contains for world filtering is O(N) and adds up quickly during mass updates.
**Action:** Cache all config values in fields during reload. Pre-resolve and cache Sound/Particle objects. Use HashSet for world lookups to achieve O(1) performance. Ensure resource reloaders are called in the main reload command.
