package frozenblock.wild.mod.registry;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class RegisterTags {
    public static final Tag<Block> CONVERTABLE_TO_MUD = TagFactory.BLOCK.create(new Identifier("twm", "convertable_to_mud"));
    public static final Tag<Block> FROG_PREFER_JUMP_TO = TagFactory.BLOCK.create(new Identifier("twm", "frog_prefer_jump_to"));
}
