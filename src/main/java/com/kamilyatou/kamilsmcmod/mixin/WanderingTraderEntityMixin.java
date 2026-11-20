package com.kamilyatou.kamilsmcmod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderEntityMixin extends AbstractVillager {
    public WanderingTraderEntityMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void addRE4MerchantTrades(ServerLevel level, CallbackInfo ci) {
        MerchantOffers offers = this.getOffers();
        if (offers == null) return;

        // "What're ya sellin', stranger?" - RE4 Merchant buys high-value treasure for emeralds!
        offers.add(new MerchantOffer(new ItemCost(Items.DIAMOND, 1), new ItemStack(Items.EMERALD, 8), 16, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.GOLD_INGOT, 2), new ItemStack(Items.EMERALD, 2), 32, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 1), new ItemStack(Items.EMERALD, 16), 8, 1, 0.05f));

        // "What're ya buyin'?" - Genuine combat weaponry, explosives & rare contraband
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(Items.GUNPOWDER, 16), 16, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(Items.SPECTRAL_ARROW, 32), 16, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(Items.FIREWORK_ROCKET, 16), 12, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 28), new ItemStack(Items.TOTEM_OF_UNDYING, 1), 2, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 32), new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1), 1, 1, 0.05f));
    }
}
