/*package net.frozenblock.wildmod.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NbtTextContent implements TextContent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final boolean interpret;
    private final Optional<Text> separator;
    private final String rawPath;
    private final NbtDataSource dataSource;
    @Nullable
    protected final NbtPathArgumentType.NbtPath path;

    public NbtTextContent(String rawPath, boolean interpret, Optional<Text> separator, NbtDataSource nbtDataSource) {
        this(rawPath, parsePath(rawPath), interpret, separator, nbtDataSource);
    }

    private NbtTextContent(String rawPath, @Nullable NbtPathArgumentType.NbtPath path, boolean interpret, Optional<Text> separator, NbtDataSource dataSource) {
        this.rawPath = rawPath;
        this.path = path;
        this.interpret = interpret;
        this.separator = separator;
        this.dataSource = dataSource;
    }

    @Nullable
    private static NbtPathArgumentType.NbtPath parsePath(String rawPath) {
        try {
            return new NbtPathArgumentType().parse(new StringReader(rawPath));
        } catch (CommandSyntaxException var2) {
            return null;
        }
    }

    public String getPath() {
        return this.rawPath;
    }

    public boolean shouldInterpret() {
        return this.interpret;
    }

    public Optional<Text> getSeparator() {
        return this.separator;
    }

    public NbtDataSource getDataSource() {
        return this.dataSource;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            if (object instanceof NbtTextContent nbtTextContent
                    && this.dataSource.equals(nbtTextContent.dataSource)
                    && this.separator.equals(nbtTextContent.separator)
                    && this.interpret == nbtTextContent.interpret
                    && this.rawPath.equals(nbtTextContent.rawPath)) {
                return true;
            }

            return false;
        }
    }

    public int hashCode() {
        int i = super.hashCode();
        i = 31 * i + (this.interpret ? 1 : 0);
        i = 31 * i + this.separator.hashCode();
        i = 31 * i + this.rawPath.hashCode();
        return 31 * i + this.dataSource.hashCode();
    }

    public String toString() {
        return "nbt{" + this.dataSource + ", interpreting=" + this.interpret + ", separator=" + this.separator + "}";
    }

    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source != null && this.path != null) {
            Stream<String> stream = this.dataSource.get(source).flatMap(nbt -> {
                try {
                    return this.path.get(nbt).stream();
                } catch (CommandSyntaxException var3x) {
                    return Stream.empty();
                }
            }).map(NbtElement::asString);
            if (this.interpret) {
                Text text = (Text) DataFixUtils.orElse(Texts.parse(source, this.separator, sender, depth), Texts.DEFAULT_SEPARATOR_TEXT);
                return (MutableText)stream.flatMap(textx -> {
                    try {
                        MutableText mutableText = Text.Serializer.fromJson(textx);
                        return Stream.of(Texts.parse(source, mutableText, sender, depth));
                    } catch (Exception var5x) {
                        LOGGER.warn("Failed to parse component: {}", textx, var5x);
                        return Stream.of();
                    }
                }).reduce((accumulator, current) -> accumulator.append(text).append(current)).orElseGet(Text::empty);
            } else {
                return Texts.parse(source, this.separator, sender, depth)
                        .map(
                                textx -> (MutableText)stream.map(Text::literal)
                                        .reduce((accumulator, current) -> accumulator.append(textx).append(current))
                                        .orElseGet(Text::empty)
                        )
                        .orElseGet(() -> Text.literal((String)stream.collect(Collectors.joining(", "))));
            }
        } else {
            return Text.empty();
        }
    }
}
*/