package net.armory_rpgs.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.armory_rpgs.ArmoryMod;

public class ArmoryItemTags {
    public static final TagKey<Item> ALL = TagKey.of(RegistryKeys.ITEM, Identifier.of(ArmoryMod.NAMESPACE, "all"));
}
