package net.armory_rpgs.datagen.recipe;

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
 *   "neoforge:conditions": [
 *     {
 *       "type": "neoforge:mod_loaded",
 *       "modid": "betternether"
 *     }
 *   ],
 *   "type": "minecraft:smithing_transform",
 *   "template": { "item": "minecraft:netherite_upgrade_smithing_template" },
 *   "base": { "item": "minecraft:diamond_helmet" },
 *   "addition": { "item": "minecraft:netherite_ingot" },
 *   "result": { "id": "minecraft:netherite_helmet", "count": 1 }
 * }
 */
public record SmithingUpgradeRecipe(
        @SerializedName("fabric:load_conditions") List<FabricLoadCondition> fabricLoadConditions,
        @SerializedName("neoforge:conditions") List<NeoForgeCondition> neoforgeConditions,
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
     * NeoForge load condition for mod dependencies
     */
    public record NeoForgeCondition(
            String type,
            String modid
    ) {
        public static NeoForgeCondition modLoaded(String modId) {
            return new NeoForgeCondition("neoforge:mod_loaded", modId);
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
        List<NeoForgeCondition> neoforgeConditions = null;

        if (requiredModIds != null && requiredModIds.length > 0) {
            fabricConditions = List.of(FabricLoadCondition.allModsLoaded(requiredModIds));
            neoforgeConditions = List.of(requiredModIds).stream()
                    .map(NeoForgeCondition::modLoaded)
                    .toList();
        }

        return new SmithingUpgradeRecipe(
                fabricConditions,
                neoforgeConditions,
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
            List<NeoForgeCondition> neoforgeConditions) {
        return new SmithingUpgradeRecipe(
                fabricConditions,
                neoforgeConditions,
                TYPE,
                ItemIngredient.of(template),
                ItemIngredient.of(base),
                ItemIngredient.of(addition),
                ItemResult.of(result)
        );
    }
}
