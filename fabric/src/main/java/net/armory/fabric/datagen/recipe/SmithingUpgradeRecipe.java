package net.armory.fabric.datagen.recipe;

import com.google.gson.annotations.SerializedName;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.List;

/**
 * Simple data structure for smithing transform recipes that can serialize to JSON.
 * Example output:
 * {
 *   "fabric:load_conditions": [
 *     {
 *       "condition": "fabric:all_mods_loaded",
 *       "values": ["betternether"]
 *     }
 *   ],
 *   "conditions": [
 *     {
 *       "type": "forge:mod_loaded",
 *       "modid": "betternether"
 *     }
 *   ],
 *   "type": "minecraft:smithing_transform",
 *   "template": { "item": "minecraft:netherite_upgrade_smithing_template" },
 *   "base": { "item": "minecraft:diamond_helmet" },
 *   "addition": { "item": "minecraft:netherite_ingot" },
 *   "result": { "item": "minecraft:netherite_helmet", "count": 1 }
 * }
 *
 * 1.20.1 notes: Forge 47 reads a plain top-level `conditions` array (there is no `forge:conditions` key),
 * and the recipe result is an item stack (`item`), not the 1.21 `id`.
 */
public record SmithingUpgradeRecipe(
        @SerializedName("fabric:load_conditions") List<FabricLoadCondition> fabricLoadConditions,
        @SerializedName("conditions") List<ForgeCondition> forgeConditions,
        String type,
        ItemIngredient template,
        ItemIngredient base,
        ItemIngredient addition,
        ItemResult result
) {
    public static final String TYPE = "minecraft:smithing_transform";

    /**
     * Fabric load condition for mod dependencies
     */
    public record FabricLoadCondition(
            String condition,
            List<String> values
    ) {
        public static FabricLoadCondition allModsLoaded(String... modIds) {
            return new FabricLoadCondition("fabric:all_mods_loaded", List.of(modIds));
        }

        public static FabricLoadCondition allModsLoaded(List<String> modIds) {
            return new FabricLoadCondition("fabric:all_mods_loaded", modIds);
        }
    }

    /**
     * Forge load condition for mod dependencies. Forge 47 reads a plain top-level `conditions` array.
     */
    public record ForgeCondition(
            String type,
            String modid
    ) {
        public static ForgeCondition modLoaded(String modId) {
            return new ForgeCondition("forge:mod_loaded", modId);
        }
    }

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
     * Represents the result item with count. 1.20.1 recipe results are item stacks keyed by `item`.
     */
    public record ItemResult(String item, int count) {
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
                null,
                null,
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
                null,
                null,
                TYPE,
                new ItemIngredient(templateId),
                new ItemIngredient(baseId),
                new ItemIngredient(additionId),
                new ItemResult(resultId, 1)
        );
    }

    /**
     * Create a smithing upgrade recipe with load conditions for specific mods
     */
    public static SmithingUpgradeRecipe ofStringsWithConditions(
            String templateId,
            String baseId,
            String additionId,
            String resultId,
            String... requiredModIds) {
        List<FabricLoadCondition> fabricConditions = null;
        List<ForgeCondition> forgeConditions = null;

        if (requiredModIds != null && requiredModIds.length > 0) {
            fabricConditions = List.of(FabricLoadCondition.allModsLoaded(requiredModIds));
            forgeConditions = List.of(requiredModIds).stream()
                    .map(ForgeCondition::modLoaded)
                    .toList();
        }

        return new SmithingUpgradeRecipe(
                fabricConditions,
                forgeConditions,
                TYPE,
                new ItemIngredient(templateId),
                new ItemIngredient(baseId),
                new ItemIngredient(additionId),
                new ItemResult(resultId, 1)
        );
    }

    /**
     * Create a smithing upgrade recipe with custom load conditions
     */
    public static SmithingUpgradeRecipe withConditions(
            Item template,
            Item base,
            Item addition,
            Item result,
            List<FabricLoadCondition> fabricConditions,
            List<ForgeCondition> forgeConditions) {
        return new SmithingUpgradeRecipe(
                fabricConditions,
                forgeConditions,
                TYPE,
                ItemIngredient.of(template),
                ItemIngredient.of(base),
                ItemIngredient.of(addition),
                ItemResult.of(result)
        );
    }
}
