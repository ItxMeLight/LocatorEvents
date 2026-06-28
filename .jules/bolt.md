## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.
## 2026-06-28 - [Field-based Caching & Resource Pre-parsing]
**Learning:** Frequent configuration lookups (YAML string keys) and resource parsing (Enum.valueOf) in hot paths like per-tick tasks or frequent events introduce significant cumulative CPU overhead.
**Action:** Implement field-based caching for all config values on reload. Pre-parse expensive objects like Particles, Sounds, and UI styles once and cache them to eliminate repeated parsing overhead in performance-critical sections.
