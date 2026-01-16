package net.yak.mononym.recipe;

import com.mojang.datafixers.util.Pair;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.yak.mononym.Mononym;
import org.jetbrains.annotations.Nullable;

public class RenameItemRecipe extends SpecialCraftingRecipe {

    public RenameItemRecipe(CraftingRecipeCategory craftingRecipeCategory) {
        super(craftingRecipeCategory);
    }

    @Nullable
    private Pair<ItemStack, ItemStack> findPair(CraftingRecipeInput input) {
        ItemStack itemStack = null;
        ItemStack itemStack2 = null;

        for(int i = 0; i < input.getSize(); ++i) {
            ItemStack itemStack3 = input.getStackInSlot(i);
            if (!itemStack3.isEmpty()) {
                if (itemStack == null) {
                    itemStack = itemStack3;
                }
                else {
                    if (itemStack2 != null) {
                        return null;
                    }

                    itemStack2 = itemStack3;
                }
            }
        }

        if (itemStack != null && itemStack2 != null && canCombineStacks(itemStack, itemStack2)) {
            return Pair.of(itemStack, itemStack2);
        } else {
            return null;
        }
    }

    private static boolean canCombineStacks(ItemStack first, ItemStack second) {
        return ((first.isOf(Items.NAME_TAG) && first.contains(DataComponentTypes.CUSTOM_NAME)) || (second.isOf(Items.NAME_TAG) && second.contains(DataComponentTypes.CUSTOM_NAME))) && !(first.isOf(Items.NAME_TAG) && second.isOf(Items.NAME_TAG));
    }

    @Override
    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        return this.findPair(craftingRecipeInput) != null;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        Pair<ItemStack, ItemStack> pair = this.findPair(craftingRecipeInput);
        if (pair == null) {
            return ItemStack.EMPTY;
        } else {
            ItemStack targetStack = pair.getFirst(); // item
            ItemStack nameTagStack = pair.getSecond(); // name tag
            if (targetStack.isOf(Items.NAME_TAG) && targetStack.contains(DataComponentTypes.CUSTOM_NAME)) {
                targetStack = pair.getSecond(); // defaults to first and second, swaps if second meets conditions for first
                nameTagStack = pair.getFirst();
            }
            ItemStack resultStack = targetStack.copyWithCount(1);
            resultStack.set(DataComponentTypes.CUSTOM_NAME, nameTagStack.getName());
            return resultStack;
        }
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput input) {
        return DefaultedList.ofSize(input.getSize(), ItemStack.EMPTY);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Mononym.RENAME_ITEM;
    }

}
