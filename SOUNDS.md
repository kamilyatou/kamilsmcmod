# Kamil's Mod - Audio Documentation & Sound Mapping

This document details all voice clips, their exact transcribed lines, their tone, and where they should be defined and triggered in code for the **Resident Evil 4 Merchant (Wandering Trader)**.

---

## 1. Voice Clip Transcriptions & Target Events

| Filename | Transcribed Line | Character Tone | Target Sound Event | Notes & Fixes |
| :--- | :--- | :--- | :--- | :--- |
| **`death.ogg`** | *"Juust aghhhhh"* | Death groan | `entity.wandering_trader.death` | Plays when the Merchant dies. |
| **`idle1.ogg`** | *"Welcome"* | Greeting | `entity.wandering_trader.ambient` | Ambient greeting when player is nearby. |
| **`idle2.ogg`** | *"What are you selling?"* | Inquisitive | `entity.wandering_trader.ambient` | Ambient line hinting at trading items for emeralds. |
| **`idle3.ogg`** | *"What are you buying?"* | Inquisitive | `entity.wandering_trader.ambient` | Classic RE4 line when player is nearby. |
| **`idle4.ogg`** | *"Is that all, stranger?"* | Dismissive / parting | `entity.wandering_trader.ambient` (or trade exit) | Better suited when closing trade or wandering. |
| **`haggle1.ogg`** | *"Got a selection of rare things for sale, stranger."* | Sales pitch | `entity.wandering_trader.haggle` | Plays when player opens the trade window or selects a trade. |
| **`haggle2.ogg`** | *"Got some rare things on sale, stranger."* | Sales pitch | `entity.wandering_trader.haggle` | Variation of sales pitch. |
| **`haggle3.ogg`** | *"I'll buy it at a high price."* | Enthusiastic buyer | `entity.wandering_trader.haggle` | Triggered especially when player offers rare treasures. |
| **`yes1.ogg`** | *"Hehehe, thank you."* | Satisfied | `entity.wandering_trader.yes`, `trade` | Successful transaction. |
| **`yes2.ogg`** | *"Hehehe, thank you."* | Satisfied | `entity.wandering_trader.yes`, `trade` | Variation / identical duplicate. |
| **`yes3.ogg`** | *"Hehehe, thank you."* | Satisfied | `entity.wandering_trader.yes`, `trade` | Successful transaction. |
| **`no1.ogg`** | *"Not enough cash, stranger."* | Rejection | `entity.wandering_trader.no` | **THE ONLY REAL 'NO' LINE**. Trigger when player lacks emeralds/items. |
| **`no2.ogg`** | *"Ah... an awesome choice, stranger."* | Praise | `entity.wandering_trader.yes`, `trade` | **CRITICAL FIX**: Was mistakenly placed in `no`. Move to `trade` / `yes`. |
| **`no4.ogg`** | *"Heheh, what you need that for?"* | Amused | `entity.wandering_trader.yes`, `trade` | **CRITICAL FIX**: Was mistakenly placed in `no`. Move to `trade` / `yes`. |
| **`no5.ogg`** | *"Come back any time."* | Friendly farewell | `entity.wandering_trader.yes`, `trade` (or trade close) | **CRITICAL FIX**: Was mistakenly placed in `no`. Move to `trade` / `yes`. |

---

## 2. Silent Placeholders (Need Custom Audio or Removal)

The following files are ~0.6-second silent placeholders (4,474 bytes) in `soundsforwanderingtrader/`:
- `hurt1.ogg` through `hurt4.ogg` *(needs hurt grunts if available, or fallback to vanilla sound)*
- `drink_milk1.ogg` through `drink_milk5.ogg` *(silent)*
- `drink_potion1.ogg`, `drink_potion2.ogg` *(silent)*
- `disappeared1.ogg`, `disappeared2.ogg` *(silent)*
- `reappeared1.ogg`, `reappeared2.ogg` *(silent)*
- `idle5.ogg`, `no3.ogg`, `yes4.ogg` *(silent duplicates)*

---

## 3. Recommended `sounds.json` Configuration

```json
{
  "entity.wandering_trader.ambient": {
    "sounds": [
      "minecraft:entity/wandering_trader/idle1",
      "minecraft:entity/wandering_trader/idle2",
      "minecraft:entity/wandering_trader/idle3",
      "minecraft:entity/wandering_trader/idle4"
    ]
  },
  "entity.wandering_trader.death": {
    "sounds": [
      "minecraft:entity/wandering_trader/death"
    ]
  },
  "entity.wandering_trader.haggle": {
    "sounds": [
      "minecraft:entity/wandering_trader/haggle1",
      "minecraft:entity/wandering_trader/haggle2",
      "minecraft:entity/wandering_trader/haggle3"
    ]
  },
  "entity.wandering_trader.no": {
    "sounds": [
      "minecraft:entity/wandering_trader/no1"
    ]
  },
  "entity.wandering_trader.trade": {
    "sounds": [
      "minecraft:entity/wandering_trader/yes1",
      "minecraft:entity/wandering_trader/yes2",
      "minecraft:entity/wandering_trader/yes3",
      "minecraft:entity/wandering_trader/no2",
      "minecraft:entity/wandering_trader/no4",
      "minecraft:entity/wandering_trader/no5"
    ]
  },
  "entity.wandering_trader.yes": {
    "sounds": [
      "minecraft:entity/wandering_trader/yes1",
      "minecraft:entity/wandering_trader/yes2",
      "minecraft:entity/wandering_trader/yes3",
      "minecraft:entity/wandering_trader/no2",
      "minecraft:entity/wandering_trader/no4",
      "minecraft:entity/wandering_trader/no5"
    ]
  }
}
```
