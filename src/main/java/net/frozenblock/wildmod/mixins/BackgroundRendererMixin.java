package net.frozenblock.wildmod.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.frozenblock.wildmod.liukrastapi.MathAddon;
import net.frozenblock.wildmod.registry.RegisterStatusEffects;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {





    @Inject(at = @At("TAIL"), method = "applyFog")
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
        Entity entity = camera.getFocusedEntity();

        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        float y;
        if (cameraSubmersionType == CameraSubmersionType.WATER) {
            y = 192.0F;
            if (entity instanceof ClientPlayerEntity clientPlayerEntity) {
                y *= Math.max(0.25F, clientPlayerEntity.getUnderwaterVisibility());
                RegistryEntry<Biome> biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
                Biome.Category category = Biome.getCategory(biome);
                if (biome.getKeyOrValue().right().equals(Biome.Category.SWAMP)) {
                    y *= 0.85F;
                }
            }

            RenderSystem.setShaderFogStart(-8.0F);
            RenderSystem.setShaderFogEnd(y * 0.5F);
        } else {
            float ab;
            if (cameraSubmersionType == CameraSubmersionType.LAVA) {
                if (entity.isSpectator()) {
                    y = -8.0F;
                    ab = viewDistance * 0.5F;
                } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
                    y = 0.0F;
                    ab = 3.0F;
                } else {
                    y = 0.25F;
                    ab = 1.0F;
                }
            } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) {
                int m = Objects.requireNonNull(((LivingEntity) entity).getStatusEffect(StatusEffects.BLINDNESS)).getDuration();
                float n = MathHelper.lerp(Math.min(1.0F, (float)m / 20.0F), viewDistance, 5.0F);
                if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
                    y = 0.0F;
                    ab = n * 0.8F;
                } else {
                    y = n * 0.25F;
                    ab = n;
                }
            } else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
                if (entity.isSpectator()) {
                    y = -8.0F;
                    ab = viewDistance * 0.5F;
                } else {
                    y = 0.0F;
                    ab = 2.0F;
                }
            } else if (thickFog) {
                y = viewDistance * 0.05F;
                ab = Math.min(viewDistance, 192.0F) * 0.5F;
            } else if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
                y = 0.0F;
                ab = viewDistance;
            } else {
                y = (viewDistance * 0.75f);
                ab = viewDistance;
            }
            float math;

            if(entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(RegisterStatusEffects.DARKNESS)) {
                float offset = 0.5f;
                float multiplier = viewDistance*1.4f;
                float equation = (float) MathAddon.cutCos(MathAddon.time, offset, true);
                math = (equation * multiplier) - offset * (multiplier);
            } else {
                math = 0;
            }

            y = y - math;
            ab = ab - math;

            RenderSystem.setShaderFogStart(y);
            RenderSystem.setShaderFogEnd(ab);
            //RenderSystem.setShaderFogColor(math, math, math);
        }
    }
}
