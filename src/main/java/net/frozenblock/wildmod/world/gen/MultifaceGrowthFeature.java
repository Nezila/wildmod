package net.frozenblock.wildmod.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.minecraft.world.gen.random.AbstractRandom;

import java.util.List;

public class MultifaceGrowthFeature extends Feature<MultifaceGrowthFeatureConfig> {
    public MultifaceGrowthFeature(Codec<MultifaceGrowthFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(FeatureContext<MultifaceGrowthFeatureConfig> context) {
        StructureWorldAccess structureWorldAccess = context.getWorld();
        BlockPos blockPos = context.getOrigin();
        AbstractRandom abstractRandom = (AbstractRandom) context.getRandom();
        MultifaceGrowthFeatureConfig multifaceGrowthFeatureConfig = context.getConfig();
        if (!isAirOrWater(structureWorldAccess.getBlockState(blockPos))) {
            return false;
        } else {
            List<Direction> list = multifaceGrowthFeatureConfig.shuffleDirections(abstractRandom);
            if (generate(structureWorldAccess, blockPos, structureWorldAccess.getBlockState(blockPos), multifaceGrowthFeatureConfig, abstractRandom, list)) {
                return true;
            } else {
                BlockPos.Mutable mutable = blockPos.mutableCopy();

                for(Direction direction : list) {
                    mutable.set(blockPos);
                    List<Direction> list2 = multifaceGrowthFeatureConfig.shuffleDirections(abstractRandom, direction.getOpposite());

                    for(int i = 0; i < multifaceGrowthFeatureConfig.searchRange; ++i) {
                        mutable.set(blockPos, direction);
                        BlockState blockState = structureWorldAccess.getBlockState(mutable);
                        if (!isAirOrWater(blockState) && !blockState.isOf(multifaceGrowthFeatureConfig.lichen)) {
                            break;
                        }

                        if (generate(structureWorldAccess, mutable, blockState, multifaceGrowthFeatureConfig, abstractRandom, list2)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        }
    }

    public static boolean generate(
            StructureWorldAccess world, BlockPos pos, BlockState state, MultifaceGrowthFeatureConfig config, AbstractRandom random, List<Direction> directions
    ) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        for(Direction direction : directions) {
            BlockState blockState = world.getBlockState(mutable.set(pos, direction));
            if (blockState.isIn(config.canPlaceOn)) {
                BlockState blockState2 = config.lichen.withDirection(state, world, pos, direction);
                if (blockState2 == null) {
                    return false;
                }

                world.setBlockState(pos, blockState2, 3);
                world.getChunk(pos).markBlockForPostProcessing(pos);
                if (random.nextFloat() < config.spreadChance) {
                    //config.lichen.getGrower().grow(blockState2, world, pos, direction, random, true);
                }

                return true;
            }
        }

        return false;
    }

    private static boolean isAirOrWater(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER);
    }
}
