package net.frozenblock.wildmod.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public abstract class AbstractLichenBlock extends net.minecraft.block.AbstractLichenBlock {
    private static final float field_31194 = 1.0F;
    private static final VoxelShape UP_SHAPE = Block.createCuboidShape(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, BooleanProperty> FACING_PROPERTIES = ConnectingBlock.FACING_PROPERTIES;
    private static final Map<Direction, VoxelShape> SHAPES_FOR_DIRECTIONS = Util.make(Maps.newEnumMap(Direction.class), shapes -> {
        shapes.put(Direction.NORTH, SOUTH_SHAPE);
        shapes.put(Direction.EAST, WEST_SHAPE);
        shapes.put(Direction.SOUTH, NORTH_SHAPE);
        shapes.put(Direction.WEST, EAST_SHAPE);
        shapes.put(Direction.UP, UP_SHAPE);
        shapes.put(Direction.DOWN, DOWN_SHAPE);
    });
    protected static final Direction[] DIRECTIONS = Direction.values();
    private final ImmutableMap<BlockState, VoxelShape> SHAPES;
    private final boolean hasAllHorizontalDirections;
    private final boolean canMirrorX;
    private final boolean canMirrorZ;

    public AbstractLichenBlock(Settings settings) {
        super(settings);
        this.setDefaultState(withAllDirections(this.stateManager));
        this.SHAPES = this.getShapesForStates(AbstractLichenBlock::getShapeForState);
        this.hasAllHorizontalDirections = Direction.Type.HORIZONTAL.stream().allMatch(this::canHaveDirection);
        this.canMirrorX = Direction.Type.HORIZONTAL.stream().filter(Direction.Axis.X).filter(this::canHaveDirection).count() % 2L == 0L;
        this.canMirrorZ = Direction.Type.HORIZONTAL.stream().filter(Direction.Axis.Z).filter(this::canHaveDirection).count() % 2L == 0L;
    }

    public static Set<Direction> collectDirections(BlockState state) {
        if (!(state.getBlock() instanceof AbstractLichenBlock)) {
            return Set.of();
        } else {
            Set<Direction> set = EnumSet.noneOf(Direction.class);

            for(Direction direction : Direction.values()) {
                if (hasDirection(state, direction)) {
                    set.add(direction);
                }
            }

            return set;
        }
    }

    public static Set<Direction> flagToDirections(byte flag) {
        Set<Direction> set = EnumSet.noneOf(Direction.class);

        for(Direction direction : Direction.values()) {
            if ((flag & (byte)(1 << direction.ordinal())) > 0) {
                set.add(direction);
            }
        }

        return set;
    }

    public static byte directionsToFlag(Collection<Direction> directions) {
        byte b = 0;

        for(Direction direction : directions) {
            b = (byte)(b | 1 << direction.ordinal());
        }

        return b;
    }

    protected boolean canHaveDirection(Direction direction) {
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        for(Direction direction : DIRECTIONS) {
            if (this.canHaveDirection(direction)) {
                builder.add(new Property[]{getProperty(direction)});
            }
        }

    }

    public BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if (!hasAnyDirection(state)) {
            return Blocks.AIR.getDefaultState();
        } else {
            return hasDirection(state, direction) && !canGrowOn(world, direction, neighborPos, neighborState)
                    ? disableDirection(state, getProperty(direction))
                    : state;
        }
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return (VoxelShape)this.SHAPES.get(state);
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        boolean bl = false;

        for(Direction direction : DIRECTIONS) {
            if (hasDirection(state, direction)) {
                BlockPos blockPos = pos.offset(direction);
                if (!canGrowOn(world, direction, blockPos, world.getBlockState(blockPos))) {
                    return false;
                }

                bl = true;
            }
        }

        return bl;
    }

    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return isNotFullBlock(state);
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        return (BlockState)Arrays.stream(ctx.getPlacementDirections())
                .map(direction -> this.withDirection(blockState, world, blockPos, direction))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public boolean canGrowWithDirection(BlockView world, BlockState state, BlockPos pos, Direction direction) {
        if (this.canHaveDirection(direction) && (!state.isOf(this) || !hasDirection(state, direction))) {
            BlockPos blockPos = pos.offset(direction);
            return canGrowOn(world, direction, blockPos, world.getBlockState(blockPos));
        } else {
            return false;
        }
    }

    @Nullable
    public BlockState withDirection(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!this.canGrowWithDirection(world, state, pos, direction)) {
            return null;
        } else {
            BlockState blockState;
            if (state.isOf(this)) {
                blockState = state;
            } else if (this.isWaterlogged() && state.getFluidState().isEqualAndStill(Fluids.WATER)) {
                blockState = (BlockState)this.getDefaultState().with(Properties.WATERLOGGED, true);
            } else {
                blockState = this.getDefaultState();
            }

            return (BlockState)blockState.with(getProperty(direction), true);
        }
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return !this.hasAllHorizontalDirections ? state : this.mirror(state, rotation::rotate);
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        if (mirror == BlockMirror.FRONT_BACK && !this.canMirrorX) {
            return state;
        } else {
            return mirror == BlockMirror.LEFT_RIGHT && !this.canMirrorZ ? state : this.mirror(state, mirror::apply);
        }
    }

    private BlockState mirror(BlockState state, Function<Direction, Direction> mirror) {
        BlockState blockState = state;

        for(Direction direction : DIRECTIONS) {
            if (this.canHaveDirection(direction)) {
                blockState = (BlockState)blockState.with(getProperty((Direction)mirror.apply(direction)), (Boolean)state.get(getProperty(direction)));
            }
        }

        return blockState;
    }

    public static boolean hasDirection(BlockState state, Direction direction) {
        BooleanProperty booleanProperty = getProperty(direction);
        return state.contains(booleanProperty) && state.get(booleanProperty);
    }

    public static boolean canGrowOn(BlockView world, Direction direction, BlockPos pos, BlockState state) {
        return Block.isFaceFullSquare(state.getSidesShape(world, pos), direction.getOpposite())
                || Block.isFaceFullSquare(state.getCollisionShape(world, pos), direction.getOpposite());
    }

    private boolean isWaterlogged() {
        return this.stateManager.getProperties().contains(Properties.WATERLOGGED);
    }

    private static BlockState disableDirection(BlockState state, BooleanProperty direction) {
        BlockState blockState = (BlockState)state.with(direction, false);
        return hasAnyDirection(blockState) ? blockState : Blocks.AIR.getDefaultState();
    }

    public static BooleanProperty getProperty(Direction direction) {
        return (BooleanProperty)FACING_PROPERTIES.get(direction);
    }

    private static BlockState withAllDirections(StateManager<Block, BlockState> stateManager) {
        BlockState blockState = (BlockState)stateManager.getDefaultState();

        for(BooleanProperty booleanProperty : FACING_PROPERTIES.values()) {
            if (blockState.contains(booleanProperty)) {
                blockState = (BlockState)blockState.with(booleanProperty, false);
            }
        }

        return blockState;
    }

    private static VoxelShape getShapeForState(BlockState state) {
        VoxelShape voxelShape = VoxelShapes.empty();

        for(Direction direction : DIRECTIONS) {
            if (hasDirection(state, direction)) {
                voxelShape = VoxelShapes.union(voxelShape, (VoxelShape)SHAPES_FOR_DIRECTIONS.get(direction));
            }
        }

        return voxelShape.isEmpty() ? VoxelShapes.fullCube() : voxelShape;
    }

    protected static boolean hasAnyDirection(BlockState state) {
        return Arrays.stream(DIRECTIONS).anyMatch(direction -> hasDirection(state, direction));
    }

    private static boolean isNotFullBlock(BlockState state) {
        return Arrays.stream(DIRECTIONS).anyMatch(direction -> !hasDirection(state, direction));
    }

    public abstract LichenGrower getGrower();
}
