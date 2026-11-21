# Kamil's Mod - Master Design & Behavior Specification

This document brings together all the systems for the **RE4 Wandering Trader** into one unified, cohesive plan. Every mechanic feeds into the next to make the entity feel like a living, intelligent, and funny character.

---

## 1. Character Identity & Concept
* **Role**: The iconic, raspy-voiced traveling merchant inspired by Resident Evil 4.
* **Companion**: Travels with a saddled, chest-carrying **Pack Mule** (replacing slow, spitting llamas).
* **Atmosphere**: Mysterious, opportunistic, but well-prepared. He buys rare curiosities, sells exotic travel items, and has an emergency getaway plan if anyone tries to rob or attack him.

---

## 2. The Appraisal & Merchant Budget System (Trading)

### A. The Merchant's Emerald Wallet (Anti-Abuse Budget)
Merchants in realistic RPGs don't have infinite cash.
* **Starting Wallet**: Each Trader spawns with a random purse of **32 to 64 Emeralds**.
* **Spending Budget**: Every time he buys an item from the player, his wallet decreases.
* **Running Out of Cash**: If the player tries to sell more than he can afford:
  * The transaction is refused.
  * He plays his voice line: *"Not enough cash, stranger!"* (`no1.ogg`).
* **Wallet Replenishment**: If the player buys travel items from him, the emeralds spent are added back into his purse, allowing him to buy more from the player again!

### B. Blind Appraisal ("Put it in and see if it fits")
The screen does **not** show a giant cheat-sheet list of everything he buys.
* In the trading interface, there is a dedicated **Appraisal Slot** (or an appraisal trade option).
* The player places an item into the input slot to test it:
  * **If he recognizes and values the item**: He plays *"I'll buy it at a high price!"* (`haggle3.ogg`), and emeralds appear in the output slot based on its rarity.
  * **If he has no interest in it** (e.g. dirt, sticks, cobblestone): The output slot stays empty, and he refuses the trade.
  * **If he wants it but is broke**: Emeralds don't appear, and he says *"Not enough cash, stranger!"* (`no1.ogg`).

### C. Valuation Hierarchy (What He Values)
Value is determined by **danger and exploration distance**, not crafting recipes:
1. **Dungeon Curiosities**: Music discs, Saddles, Name tags, Armor trims (5–16 emeralds).
2. **Dangerous Mob Trophies**: Wither skulls, Ghast tears, Shulker shells, Nautilus shells (4–20 emeralds).
3. **Deep Underground Relics**: Echo shards, Amethyst clusters, Diamonds (3–14 emeralds).
4. **Exotic Foraged Goods**: Honeycombs, Glow berries, Sea pickles, Chorus fruit (2–4 emeralds).

### D. The Travel Wares (What He Sells)
He only sells items that **save the player from traveling 10,000 blocks** across rare biomes:
* **Biome Saplings & Flora**: Mangrove Propagule, Cherry Sapling, Jungle Sapling, Bamboo, Cactus.
* **Ocean Rarities**: Sponges, Coral blocks/fans, Heart of the Sea, Sea Lanterns.
* **Peak Mountain & Cold**: Blue Ice, Packed Ice, Powder Snow bucket.
* **Nether & End Flora**: Froglights, Nether Wart, Crimson/Warped Fungi, Chorus Flowers.
* **Archaeology**: Sniffer Eggs, Pitcher Pods, Torchflower Seeds.

---

## 3. Contextual Sound Triggers (Audio Engine)

Every voice line has a single, strictly defined purpose (no misplaced sounds, no silent gaps):

| Event / Trigger | Voice Line Played | Audio File |
| :--- | :--- | :--- |
| **Player approaches within 4 blocks** | *"Welcome"* / *"What are you buying?"* | `idle1.ogg`, `idle3.ogg` |
| **Open Trade Window** | *"Got a selection of rare things on sale, stranger."* | `haggle1.ogg`, `haggle2.ogg` |
| **Player offers a valuable item** | *"I'll buy it at a high price."* | `haggle3.ogg` |
| **Completed a purchase** | Random: *"Hehehe, thank you!"* / *"Ah... an awesome choice, stranger."* / *"Heheh, what you need that for?"* | `yes3.ogg`, `no2.ogg`, `no4.ogg` |
| **Cannot afford / Out of budget** | *"Not enough cash, stranger."* | `no1.ogg` |
| **Closing trade window** | *"Is that all, stranger?"* / *"Come back any time."* | `idle4.ogg`, `no5.ogg` |
| **Killed** | *"Juust aghhhhh"* | `death.ogg` |

*Note: All 4,474-byte silent files (`hurt1-4`, `drink_milk1-5`, `idle5`, `no3`, `yes4`) are purged from `sounds.json` so they never trigger awkward silence.*

---

## 4. Getaway AI & Tactical Combat Escape

Instead of walking into walls or crying when punched, the Merchant executes a staged tactical escape:

1. **Impact Detection**:
   * When an entity (player, mob) hits the Merchant, combat evasion activates instantly.
2. **Debuffing the Attacker**:
   * He throws a **Splash Potion of Slowness** right at the attacker's feet, cutting their movement speed.
3. **Smoke-Bomb Vanish**:
   * A cloud of dense smoke particles (`CAMPFIRE_COSY_SMOKE` & `POOF`) bursts around him.
   * He receives brief Invisibility so the attacker loses visual track of him.
4. **Mounting the Getaway Mule**:
   * He pathfinds directly to his pack mule and mounts its saddle (`startRiding`).
5. **Turbo Escape**:
   * The mule receives **Speed III** for 10 seconds.
   * The mule panics and gallops away from the attacker's position.
6. **Parting Taunt**:
   * As he rides off into the distance, he shouts: *"Come back any time!"* (`no5.ogg`) or *"Heheh, what you need that for?"* (`no4.ogg`).

---

## 5. Summary Flowchart

```text
               [Player Meets Trader]
                        │
       ┌────────────────┴────────────────┐
       ▼                                 ▼
 [Opens Trades]                   [Attacks Trader]
       │                                 │
 • Haggle sound plays             • Throws Slowness Potion
 • Offers travel-saving wares     • Smoke bomb explosion
 • Appraisal slot tests items     • Mounts Pack Mule
 • Budget limits selling          • Turbo Speed III Getaway!
 • Authentic RE4 voice lines      • Yells: "Come back any time!"
```
