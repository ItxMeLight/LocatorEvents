## 2026-06-16 - [Inventory Polling & Particle Throttling]
**Learning:** Polling every player's inventory (O(N*M)) for map visibility is a significant bottleneck. Code corruption in this specific codebase caused massive redundancy (duplicated tasks and methods), compounding the performance issue.
**Action:** Always prefer event-driven updates (PlayerJoin, InventoryClick, Pickup, WorldChange) over polling. Throttle visual effects like particles to a reasonable frequency (e.g., 1Hz) to balance performance and user experience.

## 2026-06-24 - [Field-based Config Caching & Resource Object Pre-parsing]
**Learning:** Repeatedly parsing Enums (Particle, Sound, BossBar.Color) and performing YAML lookups in high-frequency tasks (1-20Hz) creates significant CPU overhead. Using a HashSet for world whitelists reduces membership checks from O(N) to O(1).
**Action:** Always implement field-based caching in ConfigManager for frequently accessed values. Pre-parse expensive resource objects (Enums, etc.) during reload and store them in fields to eliminate valueOf() and toUpperCase() calls in the main event loop.
