package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class GoToCelebrateTask<E extends MobEntity> extends Task<E> {
    private final MemoryModuleType<BlockPos> memoryModuleType;
    private final int completionRange;
    private final float speed;

    public GoToCelebrateTask(MemoryModuleType<BlockPos> memoryModuleType, int i, float f) {
        super(ImmutableMap.of(memoryModuleType, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED));
        this.memoryModuleType = memoryModuleType;
        this.completionRange = i;
        this.speed = f;
    }

    protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
        BlockPos blockPos = this.getCelebrateLocation(mobEntity);
        boolean bl = blockPos.isWithinDistance(mobEntity.getBlockPos(), this.completionRange);
        if (!bl) {
            LookTargetUtil.walkTowards(mobEntity, fuzz(mobEntity, blockPos), this.speed, this.completionRange);
        }

    }

    private static BlockPos fuzz(MobEntity mob, BlockPos pos) {
        Random random = mob.world.random;
        return pos.add(fuzz(random), 0, fuzz(random));
    }

    private static int fuzz(Random random) {
        return random.nextInt(3) - 1;
    }

    private BlockPos getCelebrateLocation(MobEntity mobEntity) {
        return mobEntity.getBrain().getOptionalMemory(this.memoryModuleType).get();
    }
}
