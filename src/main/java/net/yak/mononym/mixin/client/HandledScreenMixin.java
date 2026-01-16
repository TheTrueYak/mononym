package net.yak.mononym.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.yak.mononym.Mononym;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Unique private static final Identifier SELECTION_OVERLAY = Mononym.id("selection_overlay");

    @WrapOperation(method = "drawItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"))
    private void mononym$selectionOverlay(DrawContext instance, TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, Operation<Void> original) {
        original.call(instance, textRenderer, stack, x, y, countOverride);
        MatrixStack matrixStack = instance.getMatrices();
        matrixStack.push();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof HandledScreen<? extends ScreenHandler> screenHandler) {
            ItemStack cursorStack = screenHandler.getScreenHandler().getCursorStack();
            if (cursorStack.isOf(Items.NAME_TAG) && cursorStack.contains(DataComponentTypes.CUSTOM_NAME) && !stack.isOf(Items.NAME_TAG)) {
                float sin = (MathHelper.sin(client.player.age) + client.getRenderTickCounter().getTickDelta(false)) / 60f;
                matrixStack.scale(1 + sin, 1 + sin, 0);
                //matrixStack.translate((x * (float) 1 / sin) - x, (y * (float) 1 / sin) - y, 0);
                matrixStack.translate(sin, sin, 0);
                instance.drawGuiTexture(SELECTION_OVERLAY, x, y, 16, 16);
            }
        }
        matrixStack.pop();
    }



}
