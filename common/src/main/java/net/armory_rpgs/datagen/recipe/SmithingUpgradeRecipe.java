package net.armory_rpgs.datagen.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

/**
 * Simple data structure for smithing transform recipes that can serialize to JSON.
 * Example output:
 * {
 *   "type": "minecraft:smithing_transform",
 *   "template": { "item": "minecraft:netherite_upgrade_smithing_template" },
 *   "base": { "item": "minecraft:diamond_helmet" },
 *   "addition": { "item": "minecraft:netherite_ingot" },
 *   "result": { "id": "minecraft:netherite_helmet", "count": 1 }
 * }
 */
public record SmithingUpgradeRecipe(
        String type,
        ItemIngredient template,
        ItemIngredient base,
        ItemIngredient addition,
        ItemResult result
) {
    public static final String TYPE = "minecraft:smithing_transform";

    /**
     * Represents an item ingredient in the recipe (template, base, or addition)
     */
    public record ItemIngredient(String item) {
        public static ItemIngredient of(Item item) {
            return new ItemIngredient(Registries.ITEM.getId(item).toString());
        }

        public static ItemIngredient of(ItemConvertible item) {
            return of(item.asItem());
        }

        public static ItemIngredient of(Identifier id) {
            return new ItemIngredient(id.toString());
        }
    }

    /**
     * Represents the result item with count
     */
    public record ItemResult(String id, int count) {
        public static ItemResult of(Item item, int count) {
            return new ItemResult(Registries.ITEM.getId(item).toString(), count);
        }

        public static ItemResult of(ItemConvertible item, int count) {
            return of(item.asItem(), count);
        }

        public static ItemResult of(Item item) {
            return of(item, 1);
        }

        public static ItemResult of(ItemConvertible item) {
            return of(item.asItem(), 1);
        }
    }

    /**
     * Create a smithing upgrade recipe with all parameters
     */
    public static SmithingUpgradeRecipe of(Item template, Item base, Item addition, Item result) {
        return new SmithingUpgradeRecipe(
                TYPE,
                ItemIngredient.of(template),
                ItemIngredient.of(base),
                ItemIngredient.of(addition),
                ItemResult.of(result)
        );
    }

    /**
     * Create a smithing upgrade recipe with all parameters as ItemConvertible
     */
    public static SmithingUpgradeRecipe of(ItemConvertible template, ItemConvertible base, ItemConvertible addition, ItemConvertible result) {
        return of(template.asItem(), base.asItem(), addition.asItem(), result.asItem());
    }

    public static SmithingUpgradeRecipe ofStrings(String templateId, String baseId, String additionId, String resultId) {
        return new SmithingUpgradeRecipe(
                TYPE,
                new ItemIngredient(templateId),
                new ItemIngredient(baseId),
                new ItemIngredient(additionId),
                new ItemResult(resultId, 1)
        );
    }
}
