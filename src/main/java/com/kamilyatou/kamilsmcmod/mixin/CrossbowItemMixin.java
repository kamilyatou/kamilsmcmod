package com.kamilyatou.kamilsmcmod.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Inject(method = "performShooting", at = @At("HEAD"), cancellable = true)
    private void onPerformShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, float velocity, float divergence, LivingEntity target, CallbackInfo ci) {
        CustomData customData = crossbow.get(DataComponents.CUSTOM_DATA);
        boolean isUnreliable = (customData != null && customData.copyTag().getBoolean("Unreliable").orElse(false))
            || (crossbow.has(DataComponents.CUSTOM_NAME) && crossbow.getHoverName().getString().contains("Defective"));

        if (isUnreliable) {
            float roll = level.getRandom().nextFloat();
            if (roll < 0.15f) {
                // Jam: Click sound, fails to shoot
                level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 1.6f);
                if (shooter instanceof Player player) {
                    player.sendOverlayMessage(Component.literal("*Click* ... The crossbow jammed!").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                }
                ci.cancel();
            } else if (roll < 0.35f) {
                // Backfire: Explodes in hands, recoil damage
                level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 1.0f, 0.7f);
                if (level instanceof ServerLevel serverLevel) {
                    shooter.hurtServer(serverLevel, level.damageSources().generic(), 2.0f);
                }
                shooter.push(shooter.getLookAngle().x * -0.4, 0.2, shooter.getLookAngle().z * -0.4);
                if (shooter instanceof Player player) {
                    player.sendOverlayMessage(Component.literal("*BANG!* Defective crossbow backfired!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
                }
                ci.cancel();
            }
        }
    }
}
