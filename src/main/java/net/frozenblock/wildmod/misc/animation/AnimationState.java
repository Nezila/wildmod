package net.frozenblock.wildmod.misc.animation;

import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class AnimationState {
    private static final long STOPPED = Long.MAX_VALUE;
    private long updatedAt = Long.MAX_VALUE;
    private long timeRunning;

    public AnimationState() {
    }

    public void start(int age) {
        this.updatedAt = (long) age * 1000L / 20L;
        this.timeRunning = 0L;
    }

    public void startIfNotRunning(int age) {
        if (!this.isRunning()) {
            this.start(age);
        }

    }

    public void stop() {
        this.updatedAt = Long.MAX_VALUE;
    }

    public void run(Consumer<AnimationState> consumer) {
        if (this.isRunning()) {
            consumer.accept(this);
        }

    }

    public void update(float animationProgress, float speedMultiplier) {
        if (this.isRunning()) {
            long l = MathHelper.lfloor(animationProgress * 1000.0F / 20.0F);
            this.timeRunning += (long) ((float) (l - this.updatedAt) * speedMultiplier);
            this.updatedAt = l;
        }
    }

    public long getTimeRunning() {
        return this.timeRunning;
    }

    public boolean isRunning() {
        return this.updatedAt != Long.MAX_VALUE;
    }

    public static void init() {
    }
}
