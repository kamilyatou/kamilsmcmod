package com.kamilyatou.kamilsmcmod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(WanderingTraderSpawner.class)
public class WanderingTraderSpawnerMixin {

    @Inject(method = "tryToSpawnLlamaFor", at = @At("HEAD"), cancellable = true)
    private void onTryToSpawnLlamaFor(ServerLevel serverLevel, WanderingTrader wanderingTrader, int range, CallbackInfo ci) {
        ci.cancel();

        // Avoid spawning duplicate mules if spawner invokes tryToSpawnLlamaFor multiple times
        List<Mule> nearbyMules = serverLevel.getEntitiesOfClass(
            Mule.class,
            wanderingTrader.getBoundingBox().inflate(16.0),
            mule -> mule.isAlive() && mule.isLeashed() && mule.getLeashHolder() == wanderingTrader
        );
        if (!nearbyMules.isEmpty()) {
            return;
        }

        BlockPos traderPos = wanderingTrader.blockPosition();
        BlockPos spawnPos = traderPos;
        for (int i = 0; i < 10; i++) {
            int dx = wanderingTrader.getRandom().nextInt(range * 2 + 1) - range;
            int dz = wanderingTrader.getRandom().nextInt(range * 2 + 1) - range;
            BlockPos candidate = traderPos.offset(dx, 0, dz);
            BlockPos surface = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, candidate);
            if (serverLevel.getBlockState(surface).isAir() && !serverLevel.getBlockState(surface.below()).isAir()) {
                spawnPos = surface;
                break;
            }
        }

        Mule mule = new Mule(EntityTypes.MULE, serverLevel);
        mule.snapTo(spawnPos.getX() + 0.5, (double) spawnPos.getY(), spawnPos.getZ() + 0.5, wanderingTrader.getYRot(), 0.0f);
        mule.setAge(0);
        mule.setTamed(true);
        mule.setChest(true);
        mule.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));

        if (serverLevel.addFreshEntity(mule)) {
            mule.setLeashedTo(wanderingTrader, true);
        }
    }
}
