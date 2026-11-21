# Wandering Trader (RE4 Merchant) - Trading System Redesign & Behavior Spec

## 1. Vision & Core Philosophy

The vanilla Wandering Trader is infamous for selling overpriced junk (moss, dyes, dirt) and never buying anything.
In this mod, the Trader is transformed into the **Resident Evil 4 Merchant**:
1. **"What're ya buyin'?"**: Sells genuine **exotic expedition items** that normally require traveling thousands of blocks across rare biomes, oceans, and dimensions — saving the player tedious exploration.
2. **"What're ya sellin'?" / "I'll buy it at a high price!"**: Acts as a wandering traveling appraiser who **buys goods from the player in exchange for Emeralds**, rewarding the player for exploring and bringing home strange finds.

---

## 2. Selling to the Trader (Player Earns Emeralds)
*"I'll buy it at a high price, stranger!"*

The Merchant should buy items in distinct categories. Each spawn, he rolls a selection from these tiers:

### Tier A: Dungeon Loot & Curiosities
*Rare items players find in chests that often sit useless in chests at base:*
* **Saddles & Name Tags**: 1 for 4–6 Emeralds.
* **Music Discs** (Cat, Blocks, Chirp, etc.): 1 for 10–14 Emeralds.
* **Pottery Sherds & Sniffer Plants**: 1 for 6–8 Emeralds.
* **Armor Trims** (Dune, Coast, Wild, Sentry): 1 for 12–16 Emeralds.

### Tier B: Rare Mob Trophies & Monster Parts
*Dangerous mob drops from nether/ocean/overworld threats:*
* **Ghast Tear**: 1 for 5–7 Emeralds.
* **Wither Skeleton Skull**: 1 for 18–24 Emeralds.
* **Shulker Shell**: 1 for 12–16 Emeralds.
* **Nautilus Shell**: 1 for 4–6 Emeralds.
* **Goat Horn**: 1 for 5–8 Emeralds.
* **Rabbit's Foot**: 1 for 3–5 Emeralds.

### Tier C: Minerals, Ores & Treasures
*Valuable geological finds:*
* **Echo Shard**: 1 for 12–16 Emeralds.
* **Amethyst Shard**: 4 for 2–3 Emeralds.
* **Gold Ingot**: 4 for 3–4 Emeralds.
* **Diamond**: 1 for 8–10 Emeralds.

### Tier D: Foraged Flora & Biome Specialties
*Bulk natural produce the player gathers while traveling:*
* **Honeycomb / Honey Bottle**: 4 for 3 Emeralds.
* **Glow Berries / Sweet Berries**: 16 for 2 Emeralds.
* **Sea Pickles**: 8 for 2 Emeralds.
* **Chorus Fruit**: 8 for 4 Emeralds.

---

## 3. Buying from the Trader (Player Spends Emeralds)
*"Got a selection of rare things on sale, stranger."*

**Rule**: No boring high-tier craftables that normal villagers sell (no generic diamond armor/tools).
Instead, focus on **items gated behind thousands of blocks of travel**:

### 1. Distant Biome Flora & Saplings (The "Save a 10,000-Block Journey" Pool)
* **Mangrove Propagule & Mud**: 1 Propagule for 5 Emeralds.
* **Cherry Sapling**: 1 for 5 Emeralds.
* **Jungle Sapling & Cocoa Beans**: 1 Sapling for 4 Emeralds.
* **Cactus & Dead Bush**: 2 for 3 Emeralds.
* **Bamboo**: 4 for 3 Emeralds.

### 2. Ocean & Deep Water Exotics
* **Heart of the Sea**: 1 for 24–32 Emeralds *(Very rare offer)*.
* **Sponge**: 1 for 8–12 Emeralds.
* **Live Coral Blocks / Coral Fans** (Brain, Bubble, Fire, Horn, Tube): 2 for 4 Emeralds.
* **Sea Lantern**: 2 for 5 Emeralds.

### 3. Frozen & Mountain Exotics
* **Blue Ice**: 4 for 6 Emeralds.
* **Packed Ice**: 8 for 4 Emeralds.
* **Powder Snow Bucket**: 1 for 4 Emeralds.

### 4. Swamp, Nether & End Travel Goods
* **Froglights** (Ochre, Pearlescent, Verdant): 2 for 8 Emeralds.
* **Nether Wart**: 4 for 5 Emeralds *(Saves looking for a Nether Fortress)*.
* **Crimson & Warped Fungi**: 2 for 4 Emeralds.
* **Ender Pearl**: 2 for 6 Emeralds.
* **Chorus Flower**: 1 for 8 Emeralds.

### 5. Ancient & Archeology Finds
* **Sniffer Egg**: 1 for 16–20 Emeralds *(Saves dredging warm ocean ruins)*.
* **Torchflower Seeds / Pitcher Pod**: 1 for 6 Emeralds.

---

## 4. Behavior & Contextual Sound Triggers

### 1. Opening & Browsing Trades
* When player right-clicks the Merchant to open trades:
  * Plays `haggle1` (*"Got a selection of rare things for sale stranger."*) or `haggle2` (*"Got some rare things on sale stranger."*).
* If the player is browsing or holds an item to sell:
  * Plays `haggle3` (*"I'll buy it at a high price."*).

### 2. Transaction Completion
* When player completes a purchase:
  * Plays `yes3` (*"Hehehe, thank you!"*), `no2` (*"Ah... an awesome choice, stranger."*), or `no4` (*"Heheh, what you need that for?"*).

### 3. Insufficient Funds / Refusal
* When the player clicks a trade they cannot afford:
  * Plays `no1` (*"Not enough cash, stranger."*).

### 4. Closing the Trade Window
* When the trade screen closes:
  * Plays `idle4` (*"Is that all, stranger?"*) or `no5` (*"Come back any time."*).

---

## 5. Summary of Differences from Vanilla

| Feature | Vanilla Wandering Trader | This Mod's RE4 Merchant |
| :--- | :--- | :--- |
| **Buys from Player?** | Never. | **Yes**: Buys mob drops, relics, gems, and dungeon loot for emeralds. |
| **Sales Focus** | Junk dyes, flowers, sand, ferns. | **Travel-saving rarities**: Biome saplings, froglights, ocean sponges, sniffer eggs, nether/end flora. |
| **Voice Lines** | Annoying villager hums. | Fully voiced RE4 Merchant lines matched to trading actions. |
| **Escorts** | Slow spitting Llamas. | Pack Mules carrying chests. |
| **Under Attack** | Runs in blind panic. | Splash Slowness potion, smoke-bomb vanish, mounts mule for high-speed getaway. |
