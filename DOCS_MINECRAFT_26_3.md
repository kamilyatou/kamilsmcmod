# Fabric Modding for Minecraft 26.3 ("Wilderness Bound")

This document details the toolchain, API conventions, and specific entity/sound/trading structures used in Minecraft 26.3 for this mod.

---

## 1. Toolchain & Dependencies

* **Minecraft Version**: `26.3` (*Wilderness Bound* update)
* **Fabric Loader**: `0.20.1` (requires >= `0.19.5`)
* **Fabric API**: `0.166.2+26.3`
* **Fabric Loom**: `1.17.21`
* **Java Release**: `Java 25` (configured via Gradle toolchain)
* **Mapping Set**: Mojang Official Mappings (`mappings.jar`)

---

## 2. Package Names (Mojang Mappings in 26.3)

* **Wandering Trader**: `net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader`
* **Abstract Villager**: `net.minecraft.world.entity.npc.villager.AbstractVillager`
* **Villager**: `net.minecraft.world.entity.npc.villager.Villager`
* **Mule**: `net.minecraft.world.entity.animal.horse.Mule`
* **Llama**: `net.minecraft.world.entity.animal.horse.Llama` / `TraderLlama`
* **Trading / Offers**:
  * `net.minecraft.world.item.trading.MerchantOffer`
  * `net.minecraft.world.item.trading.ItemCost`
  * `net.minecraft.world.item.trading.MerchantOffers`
* **Level**: `net.minecraft.server.level.ServerLevel` / `net.minecraft.world.level.Level`
* **Data Components**: `net.minecraft.core.component.DataComponents`

---

## 3. Notable Modern Vanilla Systems in 26.3

### A. Data Components vs Old NBT
In Minecraft 26.3, item data is stored in **Data Components**, not raw NBT tags:
* `itemStack.set(DataComponents.CUSTOM_NAME, Component.literal("..."))`
* `itemStack.set(DataComponents.LORE, new ItemLore(...))`
* Custom mod data uses: `CustomData.update(DataComponents.CUSTOM_DATA, itemStack, tag -> tag.put(...))`

### B. Trading Offers (`ItemCost`)
Trades in 26.3 do not take bare `ItemStack` for costs; they use `ItemCost`:
```java
new MerchantOffer(
    new ItemCost(Items.EMERALD, 4),  // Primary cost
    new ItemStack(Items.SPONGE, 1),  // Result item
    8,                               // Max uses
    1,                               // Merchant XP
    0.05f                            // Price multiplier
);
```

### C. Sound Registration
Sound events for vanilla entity overrides live in `src/main/resources/assets/minecraft/sounds.json`.
Entity sound triggers use namespace `minecraft:entity/wandering_trader/<filename>` without the `.ogg` extension.

### D. Mob Damage & Escape Hooks
In modern Minecraft entity code:
* Damage handling: `hurtServer(ServerLevel level, DamageSource source, float amount)` or `actuallyHurt(DamageSource source, float amount)`
* Mount handling: `startRiding(Entity vehicle, boolean force)`
* Potion effects: `MobEffectInstance` with `MobEffects.SLOWNESS`, `MobEffects.INVISIBILITY`, `MobEffects.MOVEMENT_SPEED`
* Potion projectile: `ThrownPotion` / `PotionContents`
