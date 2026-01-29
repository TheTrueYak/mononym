package net.yak.mononym.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    /*@WrapOperation(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;formatted(Lnet/minecraft/util/Formatting;)Lnet/minecraft/text/MutableText;", ordinal = 1))
    private MutableText hi(MutableText instance, Formatting formatting, Operation<MutableText> original, Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type) {
        ItemStack cursorStack = this.handler.getCursorStack();
        if (client.currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen) {
            if (!creativeInventoryScreen.isInventoryTabSelected()) {
                return;
            }
        }
        if (this.focusedSlot != null) {
            ItemStack hoveredStack = this.focusedSlot.getStack().copy();
            if (cursorStack.isOf(Items.NAME_TAG) && cursorStack.contains(DataComponentTypes.CUSTOM_NAME) && hoveredStack != null && !hoveredStack.isEmpty() && !hoveredStack.isOf(Items.NAME_TAG) && !cursorStack.getName().equals(hoveredStack.getName())) {

            }
        }
        return original.call(instance, formatting);
    }*/

}
