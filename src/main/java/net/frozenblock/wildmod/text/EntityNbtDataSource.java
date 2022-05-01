/*package net.frozenblock.wildmod.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record EntityNbtDataSource(String rawSelector, @Nullable EntitySelector selector) implements NbtDataSource {
    public EntityNbtDataSource(String rawPath) {
        this(rawPath, parseSelector(rawPath));
    }

    @Nullable
    private static EntitySelector parseSelector(String rawSelector) {
        try {
            EntitySelectorReader entitySelectorReader = new EntitySelectorReader(new StringReader(rawSelector));
            return entitySelectorReader.read();
        } catch (CommandSyntaxException var2) {
            return null;
        }
    }

    public Stream<NbtCompound> get(ServerCommandSource source) throws CommandSyntaxException {
        if (this.selector != null) {
            List<? extends Entity> list = this.selector.getEntities(source);
            return list.stream().map(NbtPredicate::entityToNbt);
        } else {
            return Stream.empty();
        }
    }

    public String toString() {
        return "entity=" + this.rawSelector;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else {
            if (object instanceof EntityNbtDataSource entityNbtDataSource && this.rawSelector.equals(entityNbtDataSource.rawSelector)) {
                return true;
            }

            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.rawSelector});
    }
}
*/