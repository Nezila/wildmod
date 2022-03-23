package frozenblock.wild.mod.entity;

import frozenblock.wild.mod.liukrastapi.AnimationAPI;
import frozenblock.wild.mod.liukrastapi.MathAddon;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import static java.lang.Math.PI;

public class WardenEntityModel<T extends WardenEntity> extends EntityModel<WardenEntity> {
    private final ModelPart bone;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart right_tendril;
    private final ModelPart left_tendril;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public WardenEntityModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.body = this.bone.getChild("body");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.head = this.body.getChild("head");
        this.left_tendril = this.head.getChild("left_tendril");
        this.right_tendril = this.head.getChild("right_tendril");
        this.left_leg = this.bone.getChild("left_leg");
        this.right_leg = this.bone.getChild("right_leg");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData1 = modelPartData.addChild("bone", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        ModelPartData modelPartData2 = modelPartData1.addChild("body", ModelPartBuilder.create().uv(0,0).cuboid(-9.0F, -13.0F, -4.0F, 18.0F, 21.0F, 11.0F), ModelTransform.pivot(0.0F,-21.0F,0.0F));
        ModelPartData modelPartData3 = modelPartData2.addChild("head", ModelPartBuilder.create().uv(0,32).cuboid(-8.0F, -16.0F, -5.0F, 16.0F, 16.0F, 10.0F), ModelTransform.pivot(0.0F,-13.0F,0.0F));
        modelPartData3.addChild("right_tendril", ModelPartBuilder.create().uv(52,32).cuboid(-16.0F, -12.5F, 0.0F, 16.0F, 16.0F, 0.002F), ModelTransform.pivot(-8.0F,-12.5F,0.0F));
        modelPartData3.addChild("left_tendril", ModelPartBuilder.create().uv(58,0).cuboid(0.0F, -12.5F, 0.0F, 16.0F, 16.0F, 0.002F), ModelTransform.pivot(8.0F,-12.5F,0.0F));
        modelPartData2.addChild("right_arm", ModelPartBuilder.create().uv(44,50).cuboid(-4.0F, 0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(-13.0F,-13.0F,1.0F));
        modelPartData2.addChild("left_arm", ModelPartBuilder.create().uv(0,58).cuboid(-4.0F, 0F, -4.0F, 8.0F, 28.0F, 8.0F), ModelTransform.pivot(13.0F,-13.0F,1.0F));
        modelPartData1.addChild("right_leg", ModelPartBuilder.create().uv(76,48).cuboid(-3.1F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(-5.9F,-13.0F,0.0F));
        modelPartData1.addChild("left_leg", ModelPartBuilder.create().uv(76,76).cuboid(-2.9F, 0.0F, -3.0F, 6.0F, 13.0F, 6.0F), ModelTransform.pivot(5.9F,-13.0F,0.0F));
        return TexturedModelData.of(modelData,128,128);
    }
    @Override
    public void setAngles(WardenEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        /* STARTING ANIMATIONS */
        if (entity.canEmergeAnim) {
            entity.emergeAnimStartTime = animationProgress;
            entity.canEmergeAnim = false;
        }
        if (entity.canSniffAnim) {
            entity.sniffAnimStartTime = animationProgress;
            entity.canSniffAnim = false;
        }
        if (entity.canDigAnim) {
            entity.digAnimStartTime = animationProgress;
            entity.canDigAnim = false;
        }
        if (entity.canRoarAnim) {
            entity.roarAnimStartTime = animationProgress;
            entity.canRoarAnim = false;
        }
        if (entity.canAttackAnim) {
            entity.attackAnimStartTime = animationProgress;
            entity.canAttackAnim = false;
        }
        if (entity.canTendrilAnim) {
            entity.tendrilAnimStartTime = animationProgress;
            entity.canTendrilAnim = false;
        }
        /* CANCELLING ANIMATIONS (TWITTER) */
        if (entity.stopEmergeAnim) {
            entity.emergeAnimStartTime = -200;
            entity.stopEmergeAnim = false;
        }
        if (entity.stopSniffAnim) {
            entity.sniffAnimStartTime = -200;
            entity.stopSniffAnim = false;
        }
        if (entity.stopDigAnim) {
            entity.digAnimStartTime = -200;
            entity.stopDigAnim = false;
        }
        if (entity.stopRoarAnim) {
            entity.roarAnimStartTime = -200;
            entity.stopRoarAnim = false;
        }
        if (entity.stopAttackAnim) {
            entity.attackAnimStartTime = -200;
            entity.stopAttackAnim = false;
        }

        float emergeTime = AnimationAPI.animationTimer(animationProgress, entity.emergeAnimStartTime, entity.emergeAnimStartTime + 135) / 10;
        float sniffTime = AnimationAPI.animationTimer(animationProgress, entity.sniffAnimStartTime, entity.sniffAnimStartTime + 53) / 10;
        float digTime = AnimationAPI.animationTimer(animationProgress, entity.digAnimStartTime, entity.digAnimStartTime + 61) / 10;
        float roarTime = AnimationAPI.animationTimer(animationProgress, entity.roarAnimStartTime, entity.roarAnimStartTime + 70) / 10;
        float attackTime = AnimationAPI.animationTimer(animationProgress, entity.attackAnimStartTime, entity.attackAnimStartTime + 15) / 10;
        float tendrilTime = AnimationAPI.animationTimer(animationProgress, entity.tendrilAnimStartTime, entity.tendrilAnimStartTime + 10) / 10;

        boolean canEmerge = emergeTime != 0;
        boolean canSniff = sniffTime != 0;
        boolean canDig = digTime != 0;
        boolean canRoar = roarTime != 0;
        boolean canAttack = attackTime != 0;
        boolean canTendril = tendrilTime != 0;

        float k = animationProgress - (float)entity.age;
        float l = Math.min(0.5F, 3.0F * limbDistance);
        float m = animationProgress * 0.1F;
        float n = limbAngle * 0.8662F;
        float o = (float) Math.cos(n);
        float p = (float) Math.sin(n);
        float q = (float) Math.cos(m);
        float r = (float) Math.sin(m);
        float s = Math.min(0.35F, l);
        float t = 2; //Multiplier for animation length
        float j = (float) (180 / PI); //Converts degrees to radians
        float u = -((entity.vibrationTimer - entity.world.getTime()) / j) * (float)(Math.cos(Math.cos((double)animationProgress * 2.25D) * 3.141592653589793D * 0.10000000149011612D));

        float bodyY = 3;
        float legY = 11;
        float armY = -13;
        float headY = -13; //Default pivots

        /* TENDRIL CLICK */
        if (canTendril) {
            //Left Tendril
            this.left_tendril.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.04f, 35 / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.04f, t * 0.13f, -65.24f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.13f, t * 0.22f, 49.2f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.22f, t * 0.31f, -26.64f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.31f, t * 0.4f, 9.07f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.4f, t * 0.49f, -1.39f / j, tendrilTime)
            );
            this.left_tendril.yaw = (AnimationAPI.easeInOutSine(t * 0f, t * 0.04f, 35 / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.04f, t * 0.13f, -65.24f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.13f, t * 0.22f, 49.2f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.22f, t * 0.31f, -26.64f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.31f, t * 0.4f, 9.07f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.4f, t * 0.49f, -1.39f / j, tendrilTime)
            );
            //Right Tendril
            this.right_tendril.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.04f, 35 / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.04f, t * 0.13f, -65.24f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.13f, t * 0.22f, 49.2f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.22f, t * 0.31f, -26.64f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.31f, t * 0.4f, 9.07f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.4f, t * 0.49f, -1.39f / j, tendrilTime)
            );
            this.right_tendril.yaw = (AnimationAPI.easeInOutSine(t * 0f, t * 0.04f, -35 / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.04f, t * 0.13f, 65.24f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.13f, t * 0.22f, -49.2f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.22f, t * 0.31f, 26.64f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.31f, t * 0.4f, -9.07f / j, tendrilTime) +
                    AnimationAPI.easeInOutSine(t * 0.4f, t * 0.49f, 1.39f / j, tendrilTime)
            );
        } else {
            this.right_tendril.pitch = 0;
            this.right_tendril.yaw = 0;
            this.left_tendril.pitch = 0;
            this.left_tendril.yaw = 0;
        }

        /* EMERGE */
        if (canEmerge) {

            //Stop Syncing Anims
            this.head.yaw = 0;
            this.head.pivotY = headY;
            this.head.pivotZ = 0;

            this.body.yaw = 0;
            this.body.pivotX = 0;
            this.body.pivotZ = 0;

            this.left_tendril.yaw = 0;
            this.right_tendril.yaw = 0;

            this.left_leg.pitch = 0;
            this.right_leg.pitch = 0;

            //Body
            this.body.pitch = (AnimationAPI.linear(t * 0f, t * 2.88f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.88f, t * 3.76f, 25f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 3.92f, 10f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.92f, t * 4.08f, -10f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.08f, t * 4.44f, 22.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.44f, t * 4.68f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.68f, t * 5f, 22.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 5f, t * 5.8f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.8f, t * 6.64f, -70f / j, emergeTime)
            );
            this.body.roll = (AnimationAPI.linear(t * 0f, t * 0.52f, -22.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 0.52f, t * 1.2f, 15f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.2f, t * 1.68f, 17.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.68f, t * 2.88f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.88f, t * 3.76f, -17.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 4.08f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.08f, t * 4.44f, 7.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.44f, t * 4.68f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.68f, t * 5f, 2.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 5f, t * 5.8f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.8f, t * 6.64f, -2.5f / j, emergeTime)
            );
            this.body.pivotY = bodyY + 63 + (AnimationAPI.linear(t * 0f, t * 0.52f, -7f, emergeTime) +
                    AnimationAPI.linear(t * 0.52f, t * 1.2f, -24f, emergeTime) +
                    AnimationAPI.linear(t * 1.2f, t * 2.88f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 2.88f, t * 3.16f, -9f, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.76f, -10f, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 3.92f, -3f, emergeTime) +
                    AnimationAPI.linear(t * 3.92f, t * 4.08f, 3f, emergeTime) +
                    AnimationAPI.linear(t * 4.08f, t * 4.44f, -8f, emergeTime) +
                    AnimationAPI.linear(t * 4.44f, t * 4.56f, -2f, emergeTime) +
                    AnimationAPI.linear(t * 4.56f, t * 4.68f, 2f, emergeTime) +
                    AnimationAPI.linear(t * 4.68f, t * 5f, -3f, emergeTime) +
                    AnimationAPI.linear(t * 5f, t * 5.8f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 5.8f, t * 6.64f, -3f, emergeTime)
            );
            //Head
            this.head.pitch = 95 / j + (AnimationAPI.linear(t * 0f, t * 0.48f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 0.48f, t * 0.96f, -45f / j, emergeTime) +
                    AnimationAPI.linear(t * 0.96f, t * 1.28f, -108f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.28f, t * 1.56f, 33f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.56f, t * 1.8f, -10f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.8f, t * 1.92f, 18.3f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.92f, t * 2.08f, -13.3f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.08f, t * 2.24f, 7.15f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.24f, t * 2.48f, -2.15f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.48f, t * 3.04f, 92.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.04f, t * 3.88f, -15f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.88f, t * 4.8f, -16f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.8f, t * 5.76f, 5f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.76f, t * 6.76f, -41.5f / j, emergeTime)
            );
            this.head.roll = (AnimationAPI.linear(t * 0f, t * 0.48f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 0.48f, t * 0.96f, -20f / j, emergeTime) +
                    AnimationAPI.linear(t * 0.96f, t * 1.24f, 28.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.24f, t * 1.56f, -8.5f / j, emergeTime)
            );

            //Left Arm
            this.left_arm.pitch = (AnimationAPI.linear(t * 0f, t * 0.8f, -167.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 0.8f, t * 1.12f, -22.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.12f, t * 1.2f, 62.4f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.2f, t * 1.28f, 33.6f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.28f, t * 1.36f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.36f, t * 1.68f, 4f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.68f, t * 2.72f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.72f, t * 3.16f, 17.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.76f, 7.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 5.24f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.24f, t * 5.92f, -20f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.92f, t * 6.4f, 85f / j, emergeTime)
            );
            this.left_arm.yaw = (AnimationAPI.linear(t * 0f, t * 0.8f, 2f / j, emergeTime) +
                    AnimationAPI.linear(t * 0.8f, t * 1.12f, -2f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.12f, t * 1.2f, -25f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.2f, t * 1.28f, 5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.28f, t * 1.36f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.36f, t * 1.68f, 10f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.68f, t * 2.72f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.72f, t * 3.16f, -2.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.76f, 30f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 3.84f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.84f, t * 4.64f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.64f, t * 4.84f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.84f, t * 5.92f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.92f, t * 6.4f, -17.5f / j, emergeTime)
            );
            this.left_arm.roll = (AnimationAPI.linear(t * 0f, t * 0.8f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 0.8f, t * 1.12f, 22.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.12f, t * 1.2f, -22.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.2f, t * 1.28f, 12.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.28f, t * 1.36f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.36f, t * 1.68f, -12.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.68f, t * 2.72f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.72f, t * 3.16f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.76f, -55f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 3.84f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.84f, t * 4.64f, 10f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.64f, t * 4.84f, -10f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.84f, t * 5.92f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.92f, t * 6.4f, 55f / j, emergeTime)
            );
            this.left_arm.pivotY = armY + (AnimationAPI.linear(t * 0f, t * 1.12f, -6f, emergeTime) +
                    AnimationAPI.linear(t * 1.12f, t * 1.36f, 2f, emergeTime) +
                    AnimationAPI.linear(t * 1.36f, t * 1.68f, -1f, emergeTime) +
                    AnimationAPI.linear(t * 1.68f, t * 2.56f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 2.56f, t * 3.16f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.6f, -4f, emergeTime) +
                    AnimationAPI.linear(t * 3.6f, t * 3.72f, -3f, emergeTime) +
                    AnimationAPI.linear(t * 3.72f, t * 4.08f, 6f, emergeTime) +
                    AnimationAPI.linear(t * 4.08f, t * 5f, -2.25f, emergeTime) +
                    AnimationAPI.linear(t * 5f, t * 5.28f, 4.35f, emergeTime) +
                    AnimationAPI.linear(t * 5.28f, t * 5.52f, -2.35f, emergeTime) +
                    AnimationAPI.linear(t * 5.52f, t * 6.52f, 6.25f, emergeTime)
            );
            this.left_arm.pivotZ = (AnimationAPI.linear(t * 0f, t * 2.56f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 2.56f, t * 3.16f, 1f, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.6f, 3f, emergeTime) +
                    AnimationAPI.linear(t * 3.6f, t * 3.72f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 3.72f, t * 4.08f, 2f, emergeTime) +
                    AnimationAPI.linear(t * 4.08f, t * 5f, -2f, emergeTime) +
                    AnimationAPI.linear(t * 5f, t * 5.28f, -4f, emergeTime) +
                    AnimationAPI.linear(t * 5.28f, t * 5.52f, 1.9f, emergeTime) +
                    AnimationAPI.linear(t * 5.52f, t * 6.52f, -1.9f, emergeTime)
            );
            //Right Arm
            this.right_arm.pitch = -360 / j + (AnimationAPI.linear(t * 0f, t * 1.36f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.36f, t * 1.44f, 20f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.44f, t * 1.72f, 112.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.72f, t * 1.88f, 137.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.88f, t * 2.72f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.72f, t * 3.16f, 7.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.76f, 22.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 5.24f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.24f, t * 5.92f, -20f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.92f, t * 6.4f, 85f / j, emergeTime)
            );
            this.right_arm.yaw = (AnimationAPI.linear(t * 0f, t * 1.36f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.36f, t * 1.44f, 40f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.44f, t * 1.72f, -60f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.72f, t * 1.88f, 30f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.88f, t * 2.72f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.72f, t * 3.16f, 2.5f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.76f, -30f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 5.92f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.92f, t * 6.4f, 17.5f / j, emergeTime)
            );
            this.right_arm.roll = (AnimationAPI.linear(t * 0f, t * 1.36f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.36f, t * 1.44f, 40f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.44f, t * 1.72f, -55f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.72f, t * 1.88f, 15f / j, emergeTime) +
                    AnimationAPI.linear(t * 1.88f, t * 2.72f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 2.72f, t * 3.16f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.76f, 55f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.76f, t * 3.84f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 3.84f, t * 4.64f, -10f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.64f, t * 4.84f, 10f / j, emergeTime) +
                    AnimationAPI.linear(t * 4.84f, t * 5.92f, 0f / j, emergeTime) +
                    AnimationAPI.linear(t * 5.92f, t * 6.4f, -55f / j, emergeTime)
            );
            this.right_arm.pivotY = armY + (AnimationAPI.linear(t * 0f, t * 1.36f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 1.36f, t * 1.72f, -2f, emergeTime) +
                    AnimationAPI.linear(t * 1.72f, t * 1.88f, -3f, emergeTime) +
                    AnimationAPI.linear(t * 1.88f, t * 2.56f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 2.56f, t * 3.16f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.6f, -4f, emergeTime) +
                    AnimationAPI.linear(t * 3.6f, t * 3.72f, -3f, emergeTime) +
                    AnimationAPI.linear(t * 3.72f, t * 4.08f, 6f, emergeTime) +
                    AnimationAPI.linear(t * 4.08f, t * 5f, -2.25f, emergeTime) +
                    AnimationAPI.linear(t * 5f, t * 5.28f, 4.35f, emergeTime) +
                    AnimationAPI.linear(t * 5.28f, t * 5.52f, -2.35f, emergeTime) +
                    AnimationAPI.linear(t * 5.52f, t * 6.52f, 6.25f, emergeTime)
            );
            this.right_arm.pivotZ = (AnimationAPI.linear(t * 0f, t * 2.56f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 2.56f, t * 3.16f, 1f, emergeTime) +
                    AnimationAPI.linear(t * 3.16f, t * 3.6f, 3f, emergeTime) +
                    AnimationAPI.linear(t * 3.6f, t * 3.72f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 3.72f, t * 4.08f, 2f, emergeTime) +
                    AnimationAPI.linear(t * 4.08f, t * 5f, -2f, emergeTime) +
                    AnimationAPI.linear(t * 5f, t * 5.28f, -4f, emergeTime) +
                    AnimationAPI.linear(t * 5.28f, t * 5.52f, 1.9f, emergeTime) +
                    AnimationAPI.linear(t * 5.52f, t * 6.52f, -1.9f, emergeTime)
            );
            //Left Leg
            this.left_leg.pivotY = legY + 73 + (AnimationAPI.linear(t * 0f, t * 0.64f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 0.64f, t * 1.08f, -42f, emergeTime) +
                    AnimationAPI.linear(t * 1.08f, t * 2.76f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 2.76f, t * 3.88f, -21f, emergeTime) +
                    AnimationAPI.linear(t * 3.88f, t * 4.12f, +1f, emergeTime) +
                    AnimationAPI.linear(t * 4.12f, t * 4.68f, -5f, emergeTime) +
                    AnimationAPI.linear(t * 4.68f, t * 4.92f, +1f, emergeTime) +
                    AnimationAPI.linear(t * 4.92f, t * 5.28f, -7f, emergeTime)
            );
            //Right Leg
            this.right_leg.pivotY = legY + 73 + (AnimationAPI.linear(t * 0f, t * 0.64f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 0.64f, t * 1.08f, -42f, emergeTime) +
                    AnimationAPI.linear(t * 1.08f, t * 2.76f, 0f, emergeTime) +
                    AnimationAPI.linear(t * 2.76f, t * 3.88f, -21f, emergeTime) +
                    AnimationAPI.linear(t * 3.88f, t * 4.12f, +1f, emergeTime) +
                    AnimationAPI.linear(t * 4.12f, t * 4.68f, -5f, emergeTime) +
                    AnimationAPI.linear(t * 4.68f, t * 4.92f, +1f, emergeTime) +
                    AnimationAPI.linear(t * 4.92f, t * 5.28f, -7f, emergeTime)
            );
        }
        /* SNIFFING */
        if (canSniff) {
            /* Stop Syncing Animations */
            this.body.pivotY = bodyY;
            this.body.roll = 0;

            this.head.pivotY = headY;
            this.head.pivotZ = 0;

            this.left_arm.pivotZ = 0;
            this.left_arm.pivotY = -17;

            this.right_arm.pivotZ = 0;
            this.right_arm.pivotY = -17;

            this.left_leg.pivotY = 11;
            this.right_leg.pivotY = 11;

            this.left_leg.pitch = 0;
            this.right_leg.pitch = 0;

            //Body
            this.body.pitch = (AnimationAPI.easeOutSine(0, t * 0.52f, -5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.52f, t * 2.08f, 0f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 2.08f, t * 2.55f, 5f / j, sniffTime)
            );
            this.body.yaw = (AnimationAPI.easeInOutSine(0, t * 0.52f, 27.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.52f, t * 2.08f, -55f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 2.08f, t * 2.55f, 27.5f / j, sniffTime)
            );

            //Head
            this.head.pitch = (AnimationAPI.easeInOutSine(0, t * 0.52f, -5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.52f, t * 0.72f, -15f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.72f, t * 0.92f, 15f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.92f, t * 1.12f, -15f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.12f, t * 1.32f, 15f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.52f, -15f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.52f, t * 1.8f, 15f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.8f, t * 2.64f, 5f / j, sniffTime)
            );
            this.head.yaw = (AnimationAPI.easeInOutSine(0, t * 0.52f, 27.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.52f, t * 0.72f, -15f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.72f, t * 0.92f, -5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.92f, t * 1.12f, -7.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.12f, t * 1.32f, -7.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.52f, -5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.52f, t * 1.8f, -7.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.8f, t * 2.64f, 20f / j, sniffTime)
            );
            this.head.roll = (AnimationAPI.easeInOutSine(0, t * 0.52f, 12.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.52f, t * 0.72f, -7.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.72f, t * 0.92f, -2.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.92f, t * 1.12f, -2.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.12f, t * 1.32f, -2.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.52f, -2.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.52f, t * 1.8f, 0f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 1.8f, t * 2.64f, 5f / j, sniffTime)
            );

            //Left Arm
            this.left_arm.pitch = (AnimationAPI.easeInOutSine(0, t * 0.6f, 10f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.6f, t * 2.04f, -7.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 2.04f, t * 2.8f, -2.5f / j, sniffTime)
            );
            this.left_arm.roll = (AnimationAPI.easeInOutSine(0, t * 0.6f, -7.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.6f, t * 2.04f, 2.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 2.04f, t * 2.8f, 5f / j, sniffTime)
            );

            //Right Arm
            this.right_arm.pitch = (AnimationAPI.easeInOutSine(0, t * 0.6f, 7.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.4f, t * 2.04f, -10f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 2.04f, t * 2.72f, 2.5f / j, sniffTime)
            );
            this.right_arm.roll = (AnimationAPI.easeInOutSine(0, t * 0.6f, 5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 0.6f, t * 2.04f, -2.5f / j, sniffTime) +
                    AnimationAPI.easeInOutSine(t * 2.04f, t * 2.72f, -2.5f / j, sniffTime)
            );
        }
        /*ROARING*/
        if (canRoar) {
            /* Stop Syncing Animations */
            this.body.pivotY = 11;
            this.body.yaw = 0;
            this.body.roll = 0;

            this.head.yaw = 0;
            this.head.roll = 0;

            this.left_arm.pivotZ = 0;
            this.left_arm.pivotY = -17;

            this.right_arm.pivotZ = 0;
            this.right_arm.pivotY = -17;

            this.left_leg.pivotY = 11;
            this.right_leg.pivotY = 11;

            this.left_leg.pitch = 0;
            this.right_leg.pitch = 0;

            //Body
            this.body.pitch = (AnimationAPI.easeInSine(0, t * 1.32f, -25f / j, roarTime) +
                    AnimationAPI.easeOutSine(t * 1.32f, t * 1.72f, 75f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.72f, t * 2.4f, -5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.4f, t * 2.96f, 2f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.96f, t * 3.48f, -47f / j, roarTime)
            );

            //Head
            this.head.pitch = (AnimationAPI.easeInOutSine(0, t * 1.32f, 32f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.64f, -82f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.64f, t * 1.92f, 5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.92f, t * 2.24f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.24f, t * 2.6f, -1f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.6f, t * 3.48f, 46f / j, roarTime)
            );
            this.head.pivotY = -21f + (AnimationAPI.easeInOutSine(0, t * 1.32f, 0f, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.64f, 1f, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.64f, t * 1.92f, -1f, roarTime)
            );
            this.head.pivotZ = (AnimationAPI.easeInOutSine(0, t * 1.32f, 0f, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.64f, -7f, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.64f, t * 2.96f, 0f, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.96f, t * 3.48f, 7f, roarTime)
            );

            //Left Arm
            this.left_arm.pitch = (AnimationAPI.easeInOutSine(0, t * 1.12f, -102.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.12f, t * 1.32f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.72f, 168.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.72f, t * 2.12f, -16f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.12f, t * 2.8f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.8f, t * 3.08f, 2f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 3.08f, t * 3.52f, -52f / j, roarTime)
            );
            this.left_arm.yaw = (AnimationAPI.easeInOutSine(0, t * 1.12f, -7.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.12f, t * 1.32f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.72f, 7.5f / j, roarTime)
            );
            this.left_arm.roll = (AnimationAPI.easeInOutSine(0, t * 1.12f, -27.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.12f, t * 1.32f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.72f, -44.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.72f, t * 2.12f, 12f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.12f, t * 2.8f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.8f, t * 3.08f, -2f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 3.08f, t * 3.52f, 62f / j, roarTime)
            );

            //Right Arm
            this.right_arm.pitch = (AnimationAPI.easeInOutSine(0, t * 0.12f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 0.12f, t * 1.32f, -102.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.72f, 168.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.72f, t * 2.12f, -16f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.12f, t * 2.8f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.8f, t * 3.08f, 2f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 3.08f, t * 3.52f, -52f / j, roarTime)
            );
            this.right_arm.yaw = (AnimationAPI.easeInOutSine(0, t * 0.12f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 0.12f, t * 1.32f, 7.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.72f, -7.5f / j, roarTime)
            );
            this.right_arm.roll = (AnimationAPI.easeInOutSine(0, t * 0.12f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 0.12f, t * 1.32f, 27.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.32f, t * 1.72f, 44.5f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 1.72f, t * 2.12f, -12f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.12f, t * 2.8f, 0f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 2.8f, t * 3.08f, 2f / j, roarTime) +
                    AnimationAPI.easeInOutSine(t * 3.08f, t * 3.52f, -62f / j, roarTime)
            );

        }
        /* DIGGING */
        if (canDig) {
            /* Stop Syncing Animations */
            this.left_arm.pivotY = -17;

            this.right_arm.pivotY = -17;

            //Body
            this.body.pitch = (AnimationAPI.easeInOutSine(0, t * 0.36f, 55f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.36f, t * 0.6f, 0f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.6f, t * 4.16f, 25f / j, digTime)
            );
            this.body.yaw = (AnimationAPI.easeInOutSine(0, t * 0.36f, -15f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.36f, t * 0.72f, 30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.72f, t * 1.08f, -30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.08f, t * 1.44f, 30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.44f, t * 1.8f, -30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.8f, t * 2.16f, 30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 2.16f, t * 2.52f, -30f / j, digTime)
            );
            this.body.roll = (AnimationAPI.easeInOutSine(0, t * 0.36f, -15f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.36f, t * 0.72f, 30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.72f, t * 1.08f, -30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.08f, t * 1.44f, 30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.44f, t * 1.8f, -30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.8f, t * 2.16f, 30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 2.16f, t * 2.52f, -30f / j, digTime)
            );
            this.body.pivotY = bodyY + (AnimationAPI.easeInOutSine(0, t * 0.8f, 0f, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.8f, t * 3.88f, +35f, digTime)
            );

            //Head
            this.head.pitch = (AnimationAPI.easeInOutSine(0, t * 0.44f, 15f / j, digTime)
            );
            this.head.yaw = (AnimationAPI.easeInOutSine(0, t * 0.44f, 15f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.44f, t * 0.8f, -30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.8f, t * 1.16f, 30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.16f, t * 1.52f, -30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.52f, t * 1.88f, 30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.88f, t * 2.28f, -30f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 2.28f, t * 2.64f, 30f / j, digTime)
            );

            //Left Arm
            this.left_arm.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.4f, 0f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.4f, t * 0.6f, -62.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 0.6f, t * 0.72f, -32.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.72f, t * 0.96f, 155f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.96f, t * 1.28f, -122.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.28f, t * 1.4f, -32.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.4f, t * 1.64f, 155f / j, digTime) +
                    AnimationAPI.easeInSine(t * 1.64f, t * 1.96f, -122.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.96f, t * 2.08f, -32.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 2.08f, t * 2.32f, 155f / j, digTime) +
                    AnimationAPI.easeInSine(t * 2.32f, t * 2.64f, -122.5f / j, digTime)
            );
            this.left_arm.yaw = (AnimationAPI.easeInOutSine(t * 0f, t * 0.4f, 0f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.4f, t * 0.6f, 7.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 0.6f, t * 0.72f, 0f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.72f, t * 0.96f, 17.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.96f, t * 1.28f, -17.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.28f, t * 1.4f, 0f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.4f, t * 1.64f, 17.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 1.64f, t * 1.96f, -17.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.96f, t * 2.08f, 0f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 2.08f, t * 2.32f, 17.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 2.32f, t * 2.64f, -17.5f / j, digTime)
            );
            this.left_arm.roll = (AnimationAPI.easeInOutSine(t * 0f, t * 0.4f, 0f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.4f, t * 0.6f, 0f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 0.6f, t * 0.72f, -22.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.72f, t * 0.96f, 2.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.96f, t * 1.28f, 20f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.28f, t * 1.4f, -22.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.4f, t * 1.64f, 2.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 1.64f, t * 1.96f, 20f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.96f, t * 2.08f, -22.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 2.08f, t * 2.32f, 2.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 2.32f, t * 2.64f, 20f / j, digTime)
            );
            this.left_arm.pivotZ = (AnimationAPI.easeInOutSine(t * 0f, t * 0.4f, 0f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.4f, t * 0.6f, 4f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.6f, t * 0.96f, -8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.96f, t * 1.28f, 8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.28f, t * 1.64f, -8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.64f, t * 1.96f, 8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.96f, t * 2.32f, -8f / j, digTime)
            );

            //Right Arm
            this.right_arm.pitch = (AnimationAPI.easeInSine(t * 0f, t * 0.2f, -62.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 0.2f, t * 0.32f, -32.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.32f, t * 0.56f, 155f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.56f, t * 0.88f, -122.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 0.88f, t, -32.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t, t * 1.24f, 155f / j, digTime) +
                    AnimationAPI.easeInSine(t * 1.24f, t * 1.56f, -122.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.56f, t * 1.68f, -32.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.68f, t * 1.92f, 155f / j, digTime) +
                    AnimationAPI.easeInSine(t * 1.92f, t * 2.24f, -122.5f / j, digTime)
            );
            this.right_arm.yaw = (AnimationAPI.easeInSine(t * 0f, t * 0.2f, -7.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 0.2f, t * 0.32f, 0f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.32f, t * 0.56f, -17.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.56f, t * 0.88f, 17.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 0.88f, t, 0f / j, digTime) +
                    AnimationAPI.easeInOutSine(t, t * 1.24f, -17.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 1.24f, t * 1.56f, 17.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.56f, t * 1.68f, 0f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.68f, t * 1.92f, -17.5f / j, digTime) +
                    AnimationAPI.easeInSine(t * 1.92f, t * 2.24f, 17.5f / j, digTime)
            );
            this.right_arm.roll = (AnimationAPI.easeInOutSine(t * 0f, t * 0.2f, 0f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.2f, t * 0.32f, 22.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 0.32f, t * 0.56f, -2.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.56f, t * 0.88f, -20f / j, digTime) +
                    AnimationAPI.easeInSine(t * 0.88f, t, 22.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t, t * 1.24f, -2.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.24f, t * 1.56f, -20f / j, digTime) +
                    AnimationAPI.easeInSine(t * 1.56f, t * 1.68f, 22.5f / j, digTime) +
                    AnimationAPI.easeOutSine(t * 1.68f, t * 1.92f, -2.5f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.92f, t * 2.24f, -20f / j, digTime) +
                    AnimationAPI.easeInSine(t * 2.24f, t * 2.36f, 22.5f / j, digTime)
            );
            this.right_arm.pivotZ = (AnimationAPI.easeInOutSine(t * 0f, t * 0.2f, 4f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.2f, t * 0.56f, -8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.56f, t * 0.88f, 8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.88f, t * 1.24f, -8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.24f, t * 1.56f, 8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.56f, t * 1.92f, -8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 1.92f, t * 2.24f, 8f / j, digTime) +
                    AnimationAPI.easeInOutSine(t * 2.24f, t * 2.6f, -8f / j, digTime)
            );

            //Left Leg
            this.left_leg.pivotY = legY + (AnimationAPI.easeInOutSine(0, t * 0.8f, 0f, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.8f, t * 3.88f, +35f, digTime)
            );

            //Right Leg
            this.right_leg.pivotY = legY + (AnimationAPI.easeInOutSine(0, t * 0.8f, 0f, digTime) +
                    AnimationAPI.easeInOutSine(t * 0.8f, t * 3.88f, +35f, digTime)
            );

        }
        /* WALK, IDLE, & ATTACK ANIMATION */
        if (!canEmerge && !canSniff && !canRoar && !canDig && !canAttack) {
            /* Stop Syncing Animations */
            this.head.pivotY = headY;
            this.head.pivotZ = 0;

            this.body.yaw = 0;
            this.body.pivotY = bodyY;

            this.left_arm.yaw = 0;
            this.left_arm.pivotZ = 0;
            this.left_arm.pivotY = armY;

            this.right_arm.yaw = 0;
            this.right_arm.pivotZ = 0;
            this.right_arm.pivotY = armY;

            this.left_leg.pivotY = legY;
            this.right_leg.pivotY = legY;

            //Head
            if (!canAttack) {
                //Head
                this.head.pitch = headPitch * 0.017453292F;
                this.head.pitch += 0.06F * r;
                this.head.pitch += 1.2F * Math.cos(n + 1.5707964F) * s;
                this.head.yaw = headYaw * 0.017453292F;
                this.head.roll = 0.06F * q;
                this.head.roll += 0.3F * p * l;

                //Body
                this.body.pitch = 1.0F * o * s;
                this.body.pitch += 0.025F * q;
                this.body.roll = 0.1F * p * l;
                this.body.roll += 0.025F * r;

                //Left Arm
                this.left_arm.pitch = -(0.8F * o * l);
                this.left_arm.roll = 0;

                //Right Arm
                this.right_arm.pitch = -(0.8F * p * l);
                this.right_arm.roll = 0;

                //Right Leg
                this.right_leg.pitch = (float) (1.0F * Math.cos(n + 3.1415927F) * l);

                //Left Leg
                this.left_leg.pitch = 1.0F * o * l;
            }
        } else if (canAttack) {
            //Head

            this.head.roll = 0;
            this.head.yaw = 0;

            //Head
            this.head.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.12f, -10f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.12f, t * 0.36f, 27.5f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.36f, t * 0.6f, -20f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.6f, t * 0.72f, 2.5f / j, attackTime)
            );

            //Body
            this.body.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.08f, 10f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.08f, t * 0.32f, -27.5f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.32f, t * 0.56f, 20f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.56f, t * 0.68f, -2.5f / j, attackTime)
            );

            //Left Arm
            this.left_arm.pivotY = armY + (AnimationAPI.easeInOutSine(t * 0f, t * 0.04f, 1f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.04f, t * 0.32f, -4f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.32f, t * 0.44f, 3f, attackTime)
            );
            this.left_arm.pivotZ = (AnimationAPI.easeInOutSine(t * 0f, t * 0.04f, 2f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.04f, t * 0.32f, -6f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.32f, t * 0.44f, 0f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.44f, t * 0.64f, 4f, attackTime)
            );
            this.left_arm.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.08f, 15f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.08f, t * 0.28f, -135f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.28f, t * 0.52f, 135f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.52f, t * 0.64f, -15f / j, attackTime)
            );

            //Right Arm
            this.right_arm.pivotY = armY + (AnimationAPI.easeInOutSine(t * 0f, t * 0.12f, 1f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.12f, t * 0.36f, -4f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.32f, t * 0.44f, 3f, attackTime)
            );
            this.right_arm.pivotZ = (AnimationAPI.easeInOutSine(t * 0f, t * 0.12f, 2f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.12f, t * 0.36f, -6f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.36f, t * 0.44f, 0f, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.44f, t * 0.64f, 4f, attackTime)
            );
            this.right_arm.pitch = (AnimationAPI.easeInOutSine(t * 0f, t * 0.16f, 5f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.16f, t * 0.32f, -125f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.32f, t * 0.56f, 125f / j, attackTime) +
                    AnimationAPI.easeInOutSine(t * 0.56f, t * 0.64f, -5f / j, attackTime)
            );

            //Right Leg
            this.right_leg.pitch = (float) (1.0F * Math.cos(n + 3.1415927F) * l);

            //Left Leg
            this.left_leg.pitch = 1.0F * o * l;
        }
        if (entity.age<=1) {
            this.body.pivotY=100;
            this.right_leg.pivotY=100;
            this.left_leg.pivotY=100;
        }

    }


    @Override
    public void render(MatrixStack matrixStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        body.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        left_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        right_leg.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
