package frozenblock.wild.mod.entity.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

public class LargeEntitySpawnHelper {
    public LargeEntitySpawnHelper() {
    }

    public static <T extends MobEntity> Optional<T> trySpawnAt(
            EntityType<T> entityType, SpawnReason reason, ServerWorld world, BlockPos pos, int tries, int horizontalRange, int verticalRange
    ) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        for(int i = 0; i < tries; ++i) {
            int j = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            int k = MathHelper.nextBetween(world.random, -horizontalRange, horizontalRange);
            if (findSpawnPos(world, verticalRange, mutable.set(pos, j, verticalRange, k))) {
                T mobEntity = (T)entityType.create(world, null, null, null, mutable, reason, false, false);
                if (mobEntity != null) {
                    if (mobEntity.canSpawn(world, reason) && mobEntity.canSpawn(world)) {
                        world.spawnEntityAndPassengers(mobEntity);
                        return Optional.of(mobEntity);
                    }

                    mobEntity.discard();
                }
            }
        }

        return Optional.empty();
    }

    private static boolean findSpawnPos(ServerWorld world, int verticalRange, BlockPos.Mutable pos) {
        if (!world.getWorldBorder().contains(pos)) {
            return false;
        } else {
            BlockState blockState = world.getBlockState(pos);

            for(int i = verticalRange; i >= -verticalRange; --i) {
                pos.move(Direction.DOWN);
                BlockState blockState2 = world.getBlockState(pos);
                if ((blockState.isAir() || blockState.getMaterial().isLiquid()) && blockState2.getMaterial().blocksLight()) {
                    pos.move(Direction.UP);
                    return true;
                }

                blockState = blockState2;
            }

            return false;
        }
    }
}
