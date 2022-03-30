package frozenblock.wild.mod.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;

public class SwiftSneakEnchantment extends Enchantment {
    public SwiftSneakEnchantment(Rarity rarity, EquipmentSlot... equipmentSlots) {
        super(rarity, EnchantmentTarget.ARMOR_LEGS, equipmentSlots);
    }

    public int getMinPower(int i) {
        return i * 25;
    }

    public int getMaxPower(int i) {
        return this.getMinPower(i) + 50;
    }

    public boolean isTreasure() {
        return true;
    }

    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    public boolean isAvailableForRandomSelection() {
        return false;
    }

    public int getMaxLevel() {
        return 3;
    }
}
