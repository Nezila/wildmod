package frozenblock.wild.mod.entity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import frozenblock.wild.mod.registry.RegisterEntities;
import frozenblock.wild.mod.registry.RegisterItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.control.AquaticMoveControl;
import net.minecraft.entity.ai.control.YawAdjustingLookControl;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class TadpoleEntity extends FishEntity {
    @VisibleForTesting
    public static int MAX_TADPOLE_AGE = Math.abs(-24000);
    private int tadpoleAge;
    protected static final ImmutableList<SensorType<? extends Sensor<? super TadpoleEntity>>> SENSORS;
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_MODULES;

    public TadpoleEntity(EntityType<? extends FishEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new AquaticMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new YawAdjustingLookControl(this, 10);
    }

    protected EntityNavigation createNavigation(World world) {
        return new SwimNavigation(this, world);
    }

    protected Brain.Profile<TadpoleEntity> createBrainProfile() {
        return Brain.createProfile(MEMORY_MODULES, SENSORS);
    }

    protected Brain<?> deserializeBrain(Dynamic<?> dynamic) {
        return TadpoleBrain.create(this.createBrainProfile().deserialize(dynamic));
    }


    public Brain<TadpoleEntity> getBrain() {return (Brain<TadpoleEntity>) super.getBrain();}

    protected SoundEvent getFlopSound() {
        return SoundEvents.ENTITY_TROPICAL_FISH_FLOP;
    }

    protected void mobTick() {
        this.world.getProfiler().push("tadpoleBrain");
        this.getBrain().tick((ServerWorld)this.world, this);
        this.world.getProfiler().pop();
        this.world.getProfiler().push("tadpoleActivityUpdate");
        TadpoleBrain.method_41401(this);
        this.world.getProfiler().pop();
        super.mobTick();
    }

    public static DefaultAttributeContainer.Builder createTadpoleAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0D);
    }

    public void tickMovement() {
        super.tickMovement();
        if (!this.world.isClient) {
            this.setTadpoleAge(this.tadpoleAge + 1);
        }

    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Age", this.tadpoleAge);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setTadpoleAge(nbt.getInt("Age"));
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_COD_HURT;
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SALMON_DEATH;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_TROPICAL_FISH_FLOP, 0.15F, 1.0F);
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.isSlimeBall(itemStack)) {
            this.eatSlimeBall(player, itemStack);
            return ActionResult.success(this.world.isClient);
        } else {
            return (ActionResult) Bucketable.tryBucket(player, hand, this).orElse(super.interactMob(player, hand));
        }
    }

    protected void sendAiDebugData() {
        super.sendAiDebugData();
        DebugInfoSender.sendBrainDebugData(this);
    }

    public boolean isFromBucket() {
        return true;
    }

    public void setFromBucket(boolean fromBucket) {
    }

    @Deprecated
    public void copyDataToStack(ItemStack stack) {
        Bucketable.copyDataToStack(this, stack);
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putInt("Age", this.getTadpoleAge());
    }

    @Deprecated
    public void copyDataFromNbt(NbtCompound nbt) {
        Bucketable.copyDataFromNbt(this, nbt);
        if (nbt.contains("Age")) {
            this.setTadpoleAge(nbt.getInt("Age"));
        }

    }

    public ItemStack getBucketItem() {
        return new ItemStack(RegisterItems.TADPOLE_BUCKET);
    }

    public SoundEvent getBucketedSound() {
        return SoundEvents.ITEM_BUCKET_FILL_FISH;
    }

    private boolean isSlimeBall(ItemStack stack) {
        return FrogEntity.SLIME_BALL.test(stack);
    }

    private void eatSlimeBall(PlayerEntity player, ItemStack stack) {
        this.decrementItem(player, stack);
        this.increaseAge(method_41321(this.getTicksUntilGrowth()));
        this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), 0.0D, 0.0D, 0.0D);
        this.emitGameEvent(GameEvent.MOB_INTERACT, this.getCameraBlockPos());
    }

    private void decrementItem(PlayerEntity player, ItemStack stack) {
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }

    }

    public static int method_41321(int i) {
        return (int)((float)(i / 20) * 0.1F);
    }

    private int getTadpoleAge() {
        return this.tadpoleAge;
    }

    private void increaseAge(int seconds) {
        this.setTadpoleAge(this.tadpoleAge + seconds * 20);
    }

    private void setTadpoleAge(int tadpoleAge) {
        this.tadpoleAge = tadpoleAge;
        if (this.tadpoleAge >= MAX_TADPOLE_AGE) {
            this.growUp();
        }

    }

    private void growUp() {
        World var2 = this.world;
        if (var2 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var2;
            FrogEntity frogEntity = (FrogEntity)RegisterEntities.FROG.create(this.world);
            frogEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
            frogEntity.initialize(serverWorld, this.world.getLocalDifficulty(frogEntity.getBlockPos()), SpawnReason.CONVERSION, (EntityData)null, (NbtCompound)null);
            frogEntity.setAiDisabled(this.isAiDisabled());
            if (this.hasCustomName()) {
                frogEntity.setCustomName(this.getCustomName());
                frogEntity.setCustomNameVisible(this.isCustomNameVisible());
            }

            frogEntity.setPersistent();
            //this.playSound(SoundEvents.ENTITY_TADPOLE_GROW_UP, 0.15F, 1.0F);
            serverWorld.spawnEntityAndPassengers(frogEntity);
            this.discard();
        }

    }

    private int getTicksUntilGrowth() {
        return Math.max(0, MAX_TADPOLE_AGE - this.tadpoleAge);
    }

    static {
        SENSORS = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY);
        MEMORY_MODULES = ImmutableList.of(MemoryModuleType.LOOK_TARGET, MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.WALK_TARGET, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.PATH, MemoryModuleType.NEAREST_VISIBLE_ADULT);
    }
}

