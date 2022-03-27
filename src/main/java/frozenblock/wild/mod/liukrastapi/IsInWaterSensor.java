package frozenblock.wild.mod.liukrastapi;

import com.google.common.collect.ImmutableSet;
import frozenblock.wild.mod.registry.RegisterEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Set;

public class IsInWaterSensor extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {return ImmutableSet.of(RegisterEntities.IS_IN_WATER);}

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        if (entity.isTouchingWater()) {
            entity.getBrain().remember(RegisterEntities.IS_IN_WATER, Unit.INSTANCE);
        } else {
            entity.getBrain().forget(RegisterEntities.IS_IN_WATER);
        }

    }
}
