package net.frozenblock.wildmod.entity.ai.task;

import com.google.common.collect.ImmutableMap;
import net.frozenblock.wildmod.entity.WardenEntity;
import net.frozenblock.wildmod.entity.WildDamageSource;
import net.frozenblock.wildmod.registry.RegisterMemoryModules;
import net.frozenblock.wildmod.registry.RegisterParticles;
import net.frozenblock.wildmod.registry.RegisterSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Optional;

public class SonicBoomTask extends Task<WardenEntity> {
    private static final int SOUND_DELAY = MathHelper.ceil(34.0) + 5;
    private static final int RUN_TIME = MathHelper.ceil(60.0F);

    public SonicBoomTask() {
        super(
                ImmutableMap.of(
                        MemoryModuleType.ATTACK_TARGET,
                        MemoryModuleState.VALUE_PRESENT,
                        RegisterMemoryModules.SONIC_BOOM_COOLDOWN,
                        MemoryModuleState.VALUE_ABSENT,
                        RegisterMemoryModules.SONIC_BOOM_SOUND_COOLDOWN,
                        MemoryModuleState.REGISTERED,
                        RegisterMemoryModules.SONIC_BOOM_SOUND_DELAY,
                        MemoryModuleState.REGISTERED
                ),
                RUN_TIME
        );
    }

    protected boolean shouldRun(ServerWorld serverWorld, WardenEntity wardenEntity) {
        return wardenEntity.isInRange(wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0, 20.0);
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        return true;
    }

    protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        wardenEntity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)RUN_TIME);
        wardenEntity.getBrain().remember(RegisterMemoryModules.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, (long)SOUND_DELAY);
        serverWorld.sendEntityStatus(wardenEntity, (byte)62);
        wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_SONIC_CHARGE, 3.0F, 1.0F);
    }

    protected void keepRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
        wardenEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(target -> wardenEntity.getLookControl().lookAt(target.getPos()));
        if (!wardenEntity.getBrain().hasMemoryModule(RegisterMemoryModules.SONIC_BOOM_SOUND_DELAY)
                && !wardenEntity.getBrain().hasMemoryModule(RegisterMemoryModules.SONIC_BOOM_SOUND_COOLDOWN)) {
            wardenEntity.getBrain().remember(RegisterMemoryModules.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, (long)(RUN_TIME - SOUND_DELAY));
            wardenEntity.getBrain()
                    .getOptionalMemory(MemoryModuleType.ATTACK_TARGET)
                    .filter(wardenEntity::isValidTarget)
                    .filter(target -> wardenEntity.isInRange(target, 15.0, 20.0))
                    .ifPresent(target -> {
                        Vec3d vec3d = wardenEntity.getPos().add(0.0, 1.6F, 0.0);
                        Vec3d vec3d2 = target.getEyePos().subtract(vec3d);
                        Vec3d vec3d3 = vec3d2.normalize();

                        for(int i = 1; i < MathHelper.floor(vec3d2.length()) + 7; ++i) {
                            Vec3d vec3d4 = vec3d.add(vec3d3.multiply((double)i));
                            serverWorld.spawnParticles(RegisterParticles.SONIC_BOOM, vec3d4.x, vec3d4.y, vec3d4.z, 1, 0.0, 0.0, 0.0, 0.0);
                        }

                        wardenEntity.playSound(RegisterSounds.ENTITY_WARDEN_SONIC_BOOM, 3.0F, 1.0F);
                        target.damage(WildDamageSource.sonicBoom(wardenEntity), 10.0F);
                        double d = 0.5 * (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        double e = 2.5 * (1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
                        target.addVelocity(vec3d3.getX() * e, vec3d3.getY() * d, vec3d3.getZ() * e);
                    });
        }
    }

    protected void finishRunning(ServerWorld serverWorld, WardenEntity wardenEntity, long time) {
        cooldown(wardenEntity, 40);
    }

    public static void cooldown(LivingEntity warden, int cooldown) {
        warden.getBrain().remember(RegisterMemoryModules.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, cooldown);
    }
}
