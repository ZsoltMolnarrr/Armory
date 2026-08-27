package net.armory.fabric.datagen.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating smithing upgrade recipes.
 * Uses the SmithingUpgradeRecipe data structure to generate JSON files.
 */
public abstract class SmithingRecipeProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final CompletableFuture<HolderLookup.Provider> registryLookup;
    protected final FabricPackOutput dataOutput;
    private final PackOutput.PathProvider recipePathResolver;

    public SmithingRecipeProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        this.dataOutput = dataOutput;
        this.registryLookup = registryLookup;
        this.recipePathResolver = dataOutput.createRegistryElementsPathProvider(Registries.RECIPE);
    }

    /**
     * Builder for collecting smithing recipes
     */
    public static class Builder {
        public final List<Entry> entries = new ArrayList<>();

        /**
         * Add a smithing recipe with an ID
         */
        public Builder add(Identifier recipeId, SmithingUpgradeRecipe recipe) {
            entries.add(new Entry(recipeId, recipe));
            return this;
        }

        /**
         * Add a smithing recipe with namespace and path
         */
        public Builder add(String namespace, String path, SmithingUpgradeRecipe recipe) {
            return add(Identifier.fromNamespaceAndPath(namespace, path), recipe);
        }
    }

    public record Entry(Identifier id, SmithingUpgradeRecipe recipe) {}

    /**
     * Implement this method to generate your smithing recipes
     */
    public abstract void generateRecipes(Builder builder);

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        var builder = new Builder();
        generateRecipes(builder);
        var entries = builder.entries;

        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (var entry : entries) {
            var json = GSON.toJsonTree(entry.recipe);
            var path = recipePathResolver.json(entry.id);
            writes.add(DataProvider.saveStable(writer, json, path));
        }

        return CompletableFuture.allOf(writes.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return "Smithing Recipe Generator";
    }
}
