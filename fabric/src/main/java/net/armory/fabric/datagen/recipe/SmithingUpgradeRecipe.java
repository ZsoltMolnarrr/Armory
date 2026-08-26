package net.armory.fabric.datagen.recipe;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

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
 *   "template": "minecraft:netherite_upgrade_smithing_template",
 *   "base": "minecraft:diamond_helmet",
 *   "addition": "minecraft:netherite_ingot",
 *   "result": { "id": "minecraft:netherite_helmet", "count": 1 }
 * }
 *
 * Since 1.21.2 ingredients are plain strings (`"ns:item"` / `"#ns:tag"` / an array), not
 * `{"item": ...}` objects - the old form is silently rejected at data load.
 */
public record SmithingUpgradeRecipe(
        @SerializedName("fabric:load_conditions") List<FabricLoadCondition> fabricLoadConditions,
        @SerializedName("neoforge:conditions") List<NeoForgeCondition> neoforgeConditions,
        String type,
        String template,
        String base,
        String addition,
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

    /// Ingredients are plain item-id strings since 1.21.2
    private static String ingredientOf(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).toString();
    }

    /**
     * Represents the result item with count
     */
    public record ItemResult(String id, int count) {
        public static ItemResult of(Item item, int count) {
            return new ItemResult(BuiltInRegistries.ITEM.getKey(item).toString(), count);
        }

        public static ItemResult of(ItemLike item, int count) {
            return of(item.asItem(), count);
        }

        public static ItemResult of(Item item) {
            return of(item, 1);
        }

        public static ItemResult of(ItemLike item) {
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
                ingredientOf(template),
                ingredientOf(base),
                ingredientOf(addition),
                ItemResult.of(result)
        );
    }

    /**
     * Create a smithing upgrade recipe with all parameters as ItemConvertible
     */
    public static SmithingUpgradeRecipe of(ItemLike template, ItemLike base, ItemLike addition, ItemLike result) {
        return of(template.asItem(), base.asItem(), addition.asItem(), result.asItem());
    }

    public static SmithingUpgradeRecipe ofStrings(String templateId, String baseId, String additionId, String resultId) {
        return new SmithingUpgradeRecipe(
                null,
                null,
                TYPE,
                templateId,
                baseId,
                additionId,
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
                templateId,
                baseId,
                additionId,
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
                ingredientOf(template),
                ingredientOf(base),
                ingredientOf(addition),
                ItemResult.of(result)
        );
    }
}
