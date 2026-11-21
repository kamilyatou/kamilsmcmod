package com.kamilyatou.kamilsmcmod.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderEntityMixin extends AbstractVillager {

    @Unique
    private int escapeCooldown = 0;

    @Unique
    private int emeraldBudget = 48;

    public WanderingTraderEntityMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void onAiStep(CallbackInfo ci) {
        if (this.escapeCooldown > 0) {
            this.escapeCooldown--;
        }

        // Mid-escape mount check: if running toward mule on foot and now in close mounting reach
        if (!this.level().isClientSide() && this.escapeCooldown > 100 && !this.isPassenger()) {
            List<Mule> reachableMules = this.level().getEntitiesOfClass(
                Mule.class,
                this.getBoundingBox().inflate(3.0),
                mule -> mule.isAlive() && (mule.getLeashHolder() == this || mule.isTamed())
            );
            if (!reachableMules.isEmpty()) {
                Mule mule = reachableMules.get(0);
                if (mule.getLeashHolder() == this) {
                    mule.dropLeash();
                }
                this.startRiding(mule, true, true);
                mule.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 1));
                mule.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 1));

                LivingEntity attacker = this.getLastHurtByMob();
                Vec3 fleeFrom = (attacker != null) ? attacker.position() : this.position();
                Vec3 diff = mule.position().subtract(fleeFrom);
                Vec3 fleeTarget = (diff.lengthSqr() > 1e-4)
                    ? mule.position().add(diff.normalize().scale(20.0))
                    : mule.position().add(this.getLookAngle().scale(20.0));
                mule.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.6);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean damaged = super.hurtServer(level, source, amount);
        this.triggerGetaway(level, source);
        return damaged;
    }

    @Unique
    private void triggerGetaway(ServerLevel level, DamageSource source) {
        if (this.escapeCooldown > 0) {
            return;
        }
        this.escapeCooldown = 240;

        // 1. Safely close trading container if trading with player
        Player player = this.getTradingPlayer();
        if (player != null) {
            if (player instanceof ServerPlayer sp) {
                sp.closeContainer();
            }
            this.setTradingPlayer(null);
        }

        // 2. Tactical retaliation against attacker
        Entity rawAttacker = source.getEntity();
        LivingEntity attacker = (rawAttacker instanceof LivingEntity living) ? living : null;
        if (attacker != null) {
            attacker.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 160, 1), this);
            level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0f, 1.0f);
        }

        // 3. Smoke bomb & Invisibility
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 30, 0.5, 0.5, 0.5, 0.05);
        level.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 0.5, this.getZ(), 20, 0.4, 0.4, 0.4, 0.05);
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.WANDERING_TRADER_DISAPPEARED, SoundSource.NEUTRAL, 1.0f, 1.0f);
        this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false));

        // Parting taunt voice line
        level.playSound(null, this.getX(), this.getY(), this.getZ(),
            SoundEvents.WANDERING_TRADER_YES, SoundSource.NEUTRAL, 1.0f, 1.0f);

        // 4. Mule escape fallback matrix
        Mule companionMule = null;
        List<Mule> nearbyMules = level.getEntitiesOfClass(
            Mule.class,
            this.getBoundingBox().inflate(16.0),
            mule -> mule.isAlive() && (mule.getLeashHolder() == this || mule.isTamed())
        );

        for (Mule mule : nearbyMules) {
            if (mule.getLeashHolder() == this) {
                companionMule = mule;
                break;
            }
        }
        if (companionMule == null && !nearbyMules.isEmpty()) {
            companionMule = nearbyMules.get(0);
        }

        double distanceToMule = companionMule != null ? this.distanceTo(companionMule) : Double.MAX_VALUE;

        if (companionMule != null && distanceToMule < 6.0) {
            // Mule nearby (< 6 blocks) and accessible: mount and turbo escape
            if (companionMule.getLeashHolder() == this) {
                companionMule.dropLeash();
            }
            this.startRiding(companionMule, true, true);
            companionMule.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 1));
            companionMule.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 1));

            Vec3 fleeFrom = (attacker != null) ? attacker.position() : this.position();
            Vec3 diff = companionMule.position().subtract(fleeFrom);
            Vec3 fleeTarget = (diff.lengthSqr() > 1e-4)
                ? companionMule.position().add(diff.normalize().scale(20.0))
                : companionMule.position().add(this.getLookAngle().scale(20.0));
            companionMule.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.6);
        } else if (companionMule != null) {
            // Mule too far: navigate towards mule with Speed II boost
            this.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 1));
            this.getNavigation().moveTo(companionMule, 1.5);
        } else {
            // Mule missing, dead, or blocked: Emergency Foot Escape
            this.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 1));
            Vec3 fleeFrom = (attacker != null) ? attacker.position() : this.position();
            Vec3 diff = this.position().subtract(fleeFrom);
            Vec3 fleeTarget = (diff.lengthSqr() > 1e-4)
                ? this.position().add(diff.normalize().scale(20.0))
                : this.position().add(new Vec3(level.getRandom().nextDouble() - 0.5, 0, level.getRandom().nextDouble() - 0.5).normalize().scale(20.0));
            this.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.5);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onAddAdditionalSaveData(ValueOutput output, CallbackInfo ci) {
        output.putInt("EmeraldBudget", this.emeraldBudget);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onReadAdditionalSaveData(ValueInput input, CallbackInfo ci) {
        this.emeraldBudget = input.getIntOr("EmeraldBudget", 48);
        this.updateOfferAvailability();
    }

    @Inject(method = "rewardTradeXp", at = @At("TAIL"))
    private void onRewardTradeXp(MerchantOffer offer, CallbackInfo ci) {
        if (this.level().isClientSide() || offer == null) {
            return;
        }

        // 1. Selling to player: Cost is Emeralds -> Merchant earns emeralds
        ItemCost costA = offer.getItemCostA();
        if (costA != null && costA.itemStack().is(Items.EMERALD)) {
            this.emeraldBudget += costA.count();
        }
        if (offer.getItemCostB().isPresent()) {
            ItemCost costB = offer.getItemCostB().get();
            if (costB.itemStack().is(Items.EMERALD)) {
                this.emeraldBudget += costB.count();
            }
        }

        // 2. Buying from player: Result is Emeralds -> Merchant pays out emeralds
        ItemStack result = offer.getResult();
        if (result.is(Items.EMERALD)) {
            int payout = result.getCount();
            this.emeraldBudget = Math.max(0, this.emeraldBudget - payout);

            if (this.emeraldBudget < 1) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.WANDERING_TRADER_NO, SoundSource.NEUTRAL, 1.0f, 1.0f);
            }
        }

        // 3. Update stock/availability based on remaining budget
        this.updateOfferAvailability();
    }

    @Unique
    private void updateOfferAvailability() {
        MerchantOffers offers = this.getOffers();
        if (offers == null) {
            return;
        }

        for (MerchantOffer offer : offers) {
            // Check offers where Merchant buys from player (payout in emeralds)
            if (offer.getResult().is(Items.EMERALD)) {
                int payout = offer.getResult().getCount();
                if (this.emeraldBudget < payout || this.emeraldBudget < 1) {
                    // Out of budget: lock buy offer so player cannot trade
                    offer.setToOutOfStock();
                } else if (offer.isOutOfStock()) {
                    // Budget replenished: restore buy offer availability
                    offer.resetUses();
                }
            }
        }
    }

    @Unique
    public int getEmeraldBudget() {
        return this.emeraldBudget;
    }

    @Unique
    public void setEmeraldBudget(int emeraldBudget) {
        this.emeraldBudget = emeraldBudget;
        this.updateOfferAvailability();
    }

    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void addRE4MerchantTrades(ServerLevel level, CallbackInfo ci) {
        MerchantOffers offers = this.getOffers();
        if (offers == null) return;

        // Replace vanilla junk trades with the curated RE4 trade pool
        offers.clear();

        // A. "I'LL BUY IT AT A HIGH PRICE!" (Player earns emeralds by selling rare curiosities):
        offers.add(new MerchantOffer(new ItemCost(Items.SADDLE, 1), new ItemStack(Items.EMERALD, 5), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.NAME_TAG, 1), new ItemStack(Items.EMERALD, 4), 12, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.MUSIC_DISC_CAT, 1), new ItemStack(Items.EMERALD, 12), 4, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.MUSIC_DISC_BLOCKS, 1), new ItemStack(Items.EMERALD, 12), 4, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.MUSIC_DISC_CHIRP, 1), new ItemStack(Items.EMERALD, 12), 4, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.GHAST_TEAR, 1), new ItemStack(Items.EMERALD, 6), 12, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.WITHER_SKELETON_SKULL, 1), new ItemStack(Items.EMERALD, 20), 4, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.SHULKER_SHELL, 1), new ItemStack(Items.EMERALD, 14), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.NAUTILUS_SHELL, 1), new ItemStack(Items.EMERALD, 5), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 1), new ItemStack(Items.EMERALD, 14), 6, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.DIAMOND, 1), new ItemStack(Items.EMERALD, 8), 16, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.GOLD_INGOT, 4), new ItemStack(Items.EMERALD, 3), 16, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.AMETHYST_SHARD, 4), new ItemStack(Items.EMERALD, 2), 16, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.HONEYCOMB, 4), new ItemStack(Items.EMERALD, 3), 16, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.GLOW_BERRIES, 16), new ItemStack(Items.EMERALD, 2), 16, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.CHORUS_FRUIT, 8), new ItemStack(Items.EMERALD, 4), 12, 1, 0.05f));

        // B. "GOT A SELECTION OF RARE THINGS ON SALE, STRANGER" (Travel-saving exotic expedition wares):
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(Items.MANGROVE_PROPAGULE, 1), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(Items.CHERRY_SAPLING, 1), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(Items.JUNGLE_SAPLING, 1), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(Items.BAMBOO, 4), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 3), new ItemStack(Items.CACTUS, 2), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 10), new ItemStack(Items.SPONGE, 1), 4, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 28), new ItemStack(Items.HEART_OF_THE_SEA, 1), 1, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(Items.BLUE_ICE, 4), 6, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(Items.PACKED_ICE, 8), 6, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(Items.OCHRE_FROGLIGHT, 2), 6, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(Items.VERDANT_FROGLIGHT, 2), 6, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 8), new ItemStack(Items.PEARLESCENT_FROGLIGHT, 2), 6, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 5), new ItemStack(Items.NETHER_WART, 4), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(Items.CRIMSON_FUNGUS, 2), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 4), new ItemStack(Items.WARPED_FUNGUS, 2), 8, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 18), new ItemStack(Items.SNIFFER_EGG, 1), 2, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 6), new ItemStack(Items.PITCHER_POD, 1), 4, 1, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 28), new ItemStack(Items.TOTEM_OF_UNDYING, 1), 1, 1, 0.05f));

        // Enforce initial budget constraints on buy offers
        this.updateOfferAvailability();
    }
}
