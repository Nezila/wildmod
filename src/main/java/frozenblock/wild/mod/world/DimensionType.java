package frozenblock.wild.mod.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.nio.file.Path;
import java.util.Optional;
import java.util.OptionalLong;

import static net.minecraft.world.dimension.DimensionType.OVERWORLD_ID;

public record DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int minimumY, int height, int logicalHeight, TagKey<Block> infiniburn, Identifier effects, float ambientLight) {
    public static final int SIZE_BITS_Y;
    public static final int field_33411 = 16;
    public static final int MAX_HEIGHT;
    public static final int MAX_COLUMN_HEIGHT;
    public static final int MIN_HEIGHT;
    public static final int field_35478;
    public static final int field_35479;
    public static final Codec<DimensionType> CODEC;
    private static final int field_31440 = 8;
    public static final float[] MOON_SIZES;
    //public static final Codec<RegistryEntry<DimensionType>> REGISTRY_CODEC;

    public DimensionType(OptionalLong fixedTime, boolean hasSkyLight, boolean hasCeiling, boolean ultrawarm, boolean natural, double coordinateScale, boolean piglinSafe, boolean bedWorks, boolean respawnAnchorWorks, boolean hasRaids, int minimumY, int height, int logicalHeight, TagKey<Block> infiniburn, Identifier effects, float ambientLight) {
        if (height < 16) {
            throw new IllegalStateException("height has to be at least 16");
        } else if (minimumY + height > MAX_COLUMN_HEIGHT + 1) {
            throw new IllegalStateException("min_y + height cannot be higher than: " + (MAX_COLUMN_HEIGHT + 1));
        } else if (logicalHeight > height) {
            throw new IllegalStateException("logical_height cannot be higher than height");
        } else if (height % 16 != 0) {
            throw new IllegalStateException("height has to be multiple of 16");
        } else if (minimumY % 16 != 0) {
            throw new IllegalStateException("min_y has to be a multiple of 16");
        } else {
            this.fixedTime = fixedTime;
            this.hasSkyLight = hasSkyLight;
            this.hasCeiling = hasCeiling;
            this.ultrawarm = ultrawarm;
            this.natural = natural;
            this.coordinateScale = coordinateScale;
            this.piglinSafe = piglinSafe;
            this.bedWorks = piglinSafe;
            this.respawnAnchorWorks = respawnAnchorWorks;
            this.hasRaids = hasRaids;
            this.minimumY = minimumY;
            this.height = height;
            this.logicalHeight = logicalHeight;
            this.infiniburn = infiniburn;
            this.effects = effects;
            this.ambientLight = ambientLight;
        }
    }

    /** @deprecated */
    @Deprecated
    public static DataResult<RegistryKey<World>> worldFromDimensionNbt(Dynamic<?> nbt) {
        Optional<Number> optional = nbt.asNumber().result();
        if (optional.isPresent()) {
            int i = optional.get().intValue();
            if (i == -1) {
                return DataResult.success(World.NETHER);
            }

            if (i == 0) {
                return DataResult.success(World.OVERWORLD);
            }

            if (i == 1) {
                return DataResult.success(World.END);
            }
        }

        return World.CODEC.parse(nbt);
    }

    public static double getCoordinateScaleFactor(DimensionType fromDimension, DimensionType toDimension) {
        double d = fromDimension.coordinateScale();
        double e = toDimension.coordinateScale();
        return d / e;
    }

    public static Path getSaveDirectory(RegistryKey<World> worldRef, Path worldDirectory) {
        if (worldRef == World.OVERWORLD) {
            return worldDirectory;
        } else if (worldRef == World.END) {
            return worldDirectory.resolve("DIM1");
        } else {
            return worldRef == World.NETHER ? worldDirectory.resolve("DIM-1") : worldDirectory.resolve("dimensions").resolve(worldRef.getValue().getNamespace()).resolve(worldRef.getValue().getPath());
        }
    }

    public boolean hasFixedTime() {
        return this.fixedTime.isPresent();
    }

    public float getSkyAngle(long time) {
        double d = MathHelper.fractionalPart((double)this.fixedTime.orElse(time) / 24000.0 - 0.25);
        double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
        return (float)(d * 2.0 + e) / 3.0F;
    }

    public int getMoonPhase(long time) {
        return (int)(time / 24000L % 8L + 8L) % 8;
    }

    public OptionalLong fixedTime() {
        return this.fixedTime;
    }

    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }

    public boolean hasCeiling() {
        return this.hasCeiling;
    }

    public boolean ultrawarm() {
        return this.ultrawarm;
    }

    public boolean natural() {
        return this.natural;
    }

    public double coordinateScale() {
        return this.coordinateScale;
    }

    public boolean piglinSafe() {
        return this.piglinSafe;
    }

    public boolean bedWorks() {
        return this.bedWorks;
    }

    public boolean respawnAnchorWorks() {
        return this.respawnAnchorWorks;
    }

    public boolean hasRaids() {
        return this.hasRaids;
    }

    public int minimumY() {
        return this.minimumY;
    }

    public int height() {
        return this.height;
    }

    public int logicalHeight() {
        return this.logicalHeight;
    }

    public TagKey<Block> infiniburn() {
        return this.infiniburn;
    }

    public Identifier effects() {
        return this.effects;
    }

    public float ambientLight() {
        return this.ambientLight;
    }

    static {
        SIZE_BITS_Y = BlockPos.SIZE_BITS_Y;
        MAX_HEIGHT = (1 << SIZE_BITS_Y) - 32;
        MAX_COLUMN_HEIGHT = (MAX_HEIGHT >> 1) - 1;
        MIN_HEIGHT = MAX_COLUMN_HEIGHT - MAX_HEIGHT + 1;
        field_35478 = MAX_COLUMN_HEIGHT << 4;
        field_35479 = MIN_HEIGHT << 4;
        CODEC = exceptionCatching(RecordCodecBuilder.create((instance) -> {
            return instance.group(Codec.LONG.optionalFieldOf("fixed_time").xmap((optional) -> {
                return (OptionalLong)optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
            }, (optionalLong) -> {
                return optionalLong.isPresent() ? Optional.of(optionalLong.getAsLong()) : Optional.empty();
            }).forGetter((dimensionType) -> {
                return dimensionType.fixedTime;
            }), Codec.BOOL.fieldOf("has_skylight").forGetter(DimensionType::hasSkyLight), Codec.BOOL.fieldOf("has_ceiling").forGetter(DimensionType::hasCeiling), Codec.BOOL.fieldOf("ultrawarm").forGetter(DimensionType::ultrawarm), Codec.BOOL.fieldOf("natural").forGetter(DimensionType::natural), Codec.doubleRange(9.999999747378752E-6, 3.0E7).fieldOf("coordinate_scale").forGetter(DimensionType::coordinateScale), Codec.BOOL.fieldOf("piglin_safe").forGetter(DimensionType::piglinSafe), Codec.BOOL.fieldOf("bed_works").forGetter(DimensionType::bedWorks), Codec.BOOL.fieldOf("respawn_anchor_works").forGetter(DimensionType::respawnAnchorWorks), Codec.BOOL.fieldOf("has_raids").forGetter(DimensionType::hasRaids), Codec.intRange(MIN_HEIGHT, MAX_COLUMN_HEIGHT).fieldOf("min_y").forGetter(DimensionType::minimumY), Codec.intRange(16, MAX_HEIGHT).fieldOf("height").forGetter(DimensionType::height), Codec.intRange(0, MAX_HEIGHT).fieldOf("logical_height").forGetter(DimensionType::logicalHeight), TagKey.stringCodec(Registry.BLOCK_KEY).fieldOf("infiniburn").forGetter((dimensionType) -> {
                return dimensionType.infiniburn;
            }), Identifier.CODEC.fieldOf("effects").orElse(OVERWORLD_ID).forGetter((dimensionType) -> {
                return dimensionType.effects;
            }), Codec.FLOAT.fieldOf("ambient_light").forGetter((dimensionType) -> {
                return dimensionType.ambientLight;
            })).apply(instance, DimensionType::new);
        }));
        MOON_SIZES = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
        //REGISTRY_CODEC = RegistryElementCodec.of(Registry.DIMENSION_TYPE_KEY, CODEC);
    }

    public static <A> Codec<A> exceptionCatching(final Codec<A> codec) {
        return Codec.of(codec, new Decoder<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
                try {
                    return codec.decode(ops, input);
                } catch (Exception var4) {
                    return DataResult.error("Cauch exception decoding " + input + ": " + var4.getMessage());
                }
            }
        });
    }
}
