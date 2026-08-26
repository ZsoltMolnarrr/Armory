package net.armory_rpgs.item;

import net.armory_rpgs.ArmoryMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ArmoryItemTags {
    public static final TagKey<Item> ALL = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(ArmoryMod.NAMESPACE, "all"));
}
