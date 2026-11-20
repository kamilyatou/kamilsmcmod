package com.kamilyatou.kamilsmcmod.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Villager.class)
public abstract class VillagerEntityMixin extends AbstractVillager {
    public VillagerEntityMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract VillagerData getVillagerData();
    @Shadow private void startTrading(Player player) {}

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onNitwitInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.getVillagerData().profession().is(VillagerProfession.NITWIT)) {
            if (!this.level().isClientSide()) {
                if (this.offers == null || this.offers.isEmpty()) {
                    this.offers = new MerchantOffers();

                    // 1. "The Bargain Blade" (Diamond sword with 1 durability left!)
                    ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
                    sword.set(DataComponents.CUSTOM_NAME, Component.literal("The Bargain Blade").withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
                    sword.set(DataComponents.DAMAGE, sword.getMaxDamage() - 1);
                    List<Component> swordLore = List.of(
                        Component.literal("A pristine, razor-sharp blade... almost.").withStyle(ChatFormatting.GRAY),
                        Component.literal("Durability: 1 / 1561").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                        Component.literal("Curse of Vanishing").withStyle(ChatFormatting.RED)
                    );
                    sword.set(DataComponents.LORE, new ItemLore(swordLore));
                    this.offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 12), sword, 1, 2, 0.05f));

                    // 2. "Defective Crossbow" (with Unreliability)
                    ItemStack crossbow = new ItemStack(Items.CROSSBOW);
                    crossbow.set(DataComponents.CUSTOM_NAME, Component.literal("Defective Crossbow").withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
                    List<Component> bowLore = List.of(
                        Component.literal("Unreliability I").withStyle(ChatFormatting.DARK_RED),
                        Component.literal("Purchased from a very questionable source.").withStyle(ChatFormatting.GRAY),
                        Component.literal("Has a tendency to jam or explode in your hands.").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
                    );
                    crossbow.set(DataComponents.LORE, new ItemLore(bowLore));
                    CustomData.update(DataComponents.CUSTOM_DATA, crossbow, tag -> tag.putBoolean("Unreliable", true));
                    this.offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 8), crossbow, 1, 2, 0.05f));

                    // 3. "Suspicious Invisibility Brew"
                    ItemStack potion = new ItemStack(Items.POTION);
                    potion.set(DataComponents.CUSTOM_NAME, Component.literal("Suspicious 'Invisibility' Brew").withStyle(ChatFormatting.LIGHT_PURPLE));
                    List<Component> potLore = List.of(
                        Component.literal("Guaranteed to turn you invisible*").withStyle(ChatFormatting.GRAY),
                        Component.literal("*Terms and conditions apply. No refunds.").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC)
                    );
                    potion.set(DataComponents.LORE, new ItemLore(potLore));
                    this.offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), potion, 4, 2, 0.05f));

                    // 4. "Counterfeit Emerald Scam"
                    ItemStack fakeEmeralds = new ItemStack(Items.DYE.green(), 32);
                    fakeEmeralds.set(DataComponents.CUSTOM_NAME, Component.literal("Totally Real Emerald").withStyle(ChatFormatting.GREEN));
                    this.offers.add(new MerchantOffer(new ItemCost(Items.DIAMOND, 1), fakeEmeralds, 5, 2, 0.05f));
                }
                this.startTrading(player);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
