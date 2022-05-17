//
// Source code recreated from a .class file by Quiltflower
//

package net.frozenblock.wildmod.world.gen;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.Structure;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.foliage.FoliagePlacer.TreeNode;

public class WildTreeFeature extends Feature<WildTreeFeatureConfig> {
    private static final int FORCE_STATE_AND_NOTIFY_ALL = 19;

    public WildTreeFeature(Codec<WildTreeFeatureConfig> codec) {
        super(codec);
    }

    public static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
        return canReplace(world, pos) || world.testBlockState(pos, state -> state.isIn(BlockTags.LOGS));
    }

    private static boolean isVine(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.VINE));
    }

    private static boolean isWater(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isOf(Blocks.WATER));
    }

    public static boolean isAirOrLeaves(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES));
    }

    private static boolean isReplaceablePlant(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> {
            Material material = state.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    private static void setBlockStateWithoutUpdatingNeighbors(ModifiableWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 19);
    }

    public static boolean canReplace(TestableWorld world, BlockPos pos) {
        return isAirOrLeaves(world, pos) || isReplaceablePlant(world, pos) || isWater(world, pos);
    }

    private boolean generate(
            StructureWorldAccess world,
            Random random,
            BlockPos pos,
            BiConsumer<BlockPos, BlockState> trunkReplacer,
            BiConsumer<BlockPos, BlockState> foliageReplacer,
            WildTreeFeatureConfig config
    ) {
        int i = config.trunkPlacer.getHeight(random);
        int j = config.foliagePlacer.getRandomHeight(random, i, config);
        int k = i - j;
        int l = config.foliagePlacer.getRandomRadius(random, k);
        if (pos.getY() >= world.getBottomY() + 1 && pos.getY() + i + 1 <= world.getTopY()) {
            OptionalInt optionalInt = config.minimumSize.getMinClippedHeight();
            int m = this.getTopPosition(world, i, pos, config);
            if (m >= i || optionalInt.isPresent() && m >= optionalInt.getAsInt()) {
                List<TreeNode> list = config.trunkPlacer.generate(world, trunkReplacer, random, m, pos, config);
                list.forEach(node -> config.foliagePlacer.generate(world, foliageReplacer, random, config, m, node, j, l));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private int getTopPosition(TestableWorld world, int height, BlockPos pos, WildTreeFeatureConfig config) {
        Mutable mutable = new Mutable();

        for(int i = 0; i <= height + 1; ++i) {
            int j = config.minimumSize.getRadius(height, i);

            for(int k = -j; k <= j; ++k) {
                for(int l = -j; l <= j; ++l) {
                    mutable.set(pos, k, i, l);
                    if (!canTreeReplace(world, mutable) || !config.ignoreVines && isVine(world, mutable)) {
                        return i - 2;
                    }
                }
            }
        }

        return height;
    }

    protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
        setBlockStateWithoutUpdatingNeighbors(world, pos, state);
    }

    public final boolean generate(FeatureContext<WildTreeFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();
        WildTreeFeatureConfig treeFeatureConfig = context.getConfig();
        Set<BlockPos> set = Sets.newHashSet();
        Set<BlockPos> set2 = Sets.newHashSet();
        Set<BlockPos> set3 = Sets.newHashSet();
        BiConsumer<BlockPos, BlockState> biConsumer = (pos, state) -> {
            set.add(pos.toImmutable());
            structureWorldAccess.setBlockState(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> biConsumer2 = (pos, state) -> {
            set2.add(pos.toImmutable());
            structureWorldAccess.setBlockState(pos, state, 19);
        };
        BiConsumer<BlockPos, BlockState> biConsumer3 = (pos, state) -> {
            set3.add(pos.toImmutable());
            structureWorldAccess.setBlockState(pos, state, 19);
        };
        boolean bl = this.generate(structureWorldAccess, random, blockPos, biConsumer, biConsumer2, treeFeatureConfig);
        if (bl && (!set.isEmpty() || !set2.isEmpty())) {
            if (!treeFeatureConfig.decorators.isEmpty()) {
                List<BlockPos> list = Lists.newArrayList(set);
                List<BlockPos> list2 = Lists.newArrayList(set2);
                list.sort(Comparator.comparingInt(Vec3i::getY));
                list2.sort(Comparator.comparingInt(Vec3i::getY));
                treeFeatureConfig.decorators.forEach(decorator -> decorator.generate(structureWorldAccess, biConsumer3, random, list, list2));
            }

            return BlockBox.encompassPositions(Iterables.concat(set, set2, set3)).map(box -> {
                VoxelSet voxelSet = placeLogsAndLeaves(structureWorldAccess, box, set, set3);
                Structure.updateCorner(structureWorldAccess, 3, voxelSet, box.getMinX(), box.getMinY(), box.getMinZ());
                return true;
            }).orElse(false);
        } else {
            return false;
        }
    }

    private static VoxelSet placeLogsAndLeaves(WorldAccess world, BlockBox box, Set<BlockPos> trunkPositions, Set<BlockPos> decorationPositions) {
        List<Set<BlockPos>> list = Lists.newArrayList();
        VoxelSet voxelSet = new BitSetVoxelSet(box.getBlockCountX(), box.getBlockCountY(), box.getBlockCountZ());
        int i = 6;

        for(int j = 0; j < 6; ++j) {
            list.add(Sets.newHashSet());
        }

        Mutable mutable = new Mutable();

        for(BlockPos blockPos : Lists.newArrayList(decorationPositions)) {
            if (box.contains(blockPos)) {
                voxelSet.set(blockPos.getX() - box.getMinX(), blockPos.getY() - box.getMinY(), blockPos.getZ() - box.getMinZ());
            }
        }

        for(BlockPos blockPos : Lists.newArrayList(trunkPositions)) {
            if (box.contains(blockPos)) {
                voxelSet.set(blockPos.getX() - box.getMinX(), blockPos.getY() - box.getMinY(), blockPos.getZ() - box.getMinZ());
            }

            for(Direction direction : Direction.values()) {
                mutable.set(blockPos, direction);
                if (!trunkPositions.contains(mutable)) {
                    BlockState blockState = world.getBlockState(mutable);
                    if (blockState.contains(Properties.DISTANCE_1_7)) {
                        ((Set)list.get(0)).add(mutable.toImmutable());
                        setBlockStateWithoutUpdatingNeighbors(world, mutable, (BlockState)blockState.with(Properties.DISTANCE_1_7, 1));
                        if (box.contains(mutable)) {
                            voxelSet.set(mutable.getX() - box.getMinX(), mutable.getY() - box.getMinY(), mutable.getZ() - box.getMinZ());
                        }
                    }
                }
            }
        }

        for(int k = 1; k < 6; ++k) {
            Set<BlockPos> set = (Set)list.get(k - 1);
            Set<BlockPos> set2 = (Set)list.get(k);

            for(BlockPos blockPos2 : set) {
                if (box.contains(blockPos2)) {
                    voxelSet.set(blockPos2.getX() - box.getMinX(), blockPos2.getY() - box.getMinY(), blockPos2.getZ() - box.getMinZ());
                }

                for(Direction direction2 : Direction.values()) {
                    mutable.set(blockPos2, direction2);
                    if (!set.contains(mutable) && !set2.contains(mutable)) {
                        BlockState blockState2 = world.getBlockState(mutable);
                        if (blockState2.contains(Properties.DISTANCE_1_7)) {
                            int l = blockState2.get(Properties.DISTANCE_1_7);
                            if (l > k + 1) {
                                BlockState blockState3 = (BlockState)blockState2.with(Properties.DISTANCE_1_7, k + 1);
                                setBlockStateWithoutUpdatingNeighbors(world, mutable, blockState3);
                                if (box.contains(mutable)) {
                                    voxelSet.set(mutable.getX() - box.getMinX(), mutable.getY() - box.getMinY(), mutable.getZ() - box.getMinZ());
                                }

                                set2.add(mutable.toImmutable());
                            }
                        }
                    }
                }
            }
        }

        return voxelSet;
    }
}
