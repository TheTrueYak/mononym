package net.yak.mononym;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.yak.mononym.networking.NameTagRenameC2SPayload;
import net.yak.mononym.recipe.RenameItemRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mononym implements ModInitializer {
	public static final String MOD_ID = "mononym";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static RecipeSerializer<RenameItemRecipe> RENAME_ITEM = registerRecipe("crafting_special_renameitem", new SpecialRecipeSerializer<>(RenameItemRecipe::new));

	@Override
	public void onInitialize() {

		LOGGER.info("those who name");

		PayloadTypeRegistry.playC2S().register(NameTagRenameC2SPayload.ID, NameTagRenameC2SPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(NameTagRenameC2SPayload.ID, new NameTagRenameC2SPayload.Receiver());






		FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer -> ResourceManagerHelper.registerBuiltinResourcePack(id("name_tag_recipe"), modContainer, ResourcePackActivationType.DEFAULT_ENABLED));

	}

	private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerRecipe(String id, S serializer) {
		return Registry.register(Registries.RECIPE_SERIALIZER, id(id), serializer);
	}


	public static Identifier id(String id) {
		return Identifier.of(MOD_ID, id);
	}
}