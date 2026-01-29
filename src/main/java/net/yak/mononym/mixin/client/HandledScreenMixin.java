package net.yak.mononym.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.yak.mononym.Mononym;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    @Shadow @Final protected T handler;
    @Shadow @Nullable protected Slot focusedSlot;
    @Unique private static final Identifier SELECTION_OVERLAY = Mononym.id("selection_overlay");

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlotHighlight(Lnet/minecraft/client/gui/DrawContext;III)V"))
    private void mononym$selectionOverlay(DrawContext context, int x, int y, int z, Operation<Void> original, DrawContext drawContext, int mouseX, int mouseY, float delta) {
        original.call(context, x, y, z);
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack cursorStack = this.handler.getCursorStack();
        if (client.currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen) {
            if (!creativeInventoryScreen.isInventoryTabSelected()) {
                return;
            }
        }
        if (this.focusedSlot != null) {
            ItemStack hoveredStack = this.focusedSlot.getStack().copy();
            if (cursorStack.isOf(Items.NAME_TAG) && cursorStack.contains(DataComponentTypes.CUSTOM_NAME) && hoveredStack != null && !hoveredStack.isEmpty() && !hoveredStack.isOf(Items.NAME_TAG) && !cursorStack.getName().equals(hoveredStack.getName())) {
                context.drawGuiTexture(SELECTION_OVERLAY, x, y, 16, 16);
                hoveredStack.set(DataComponentTypes.CUSTOM_NAME, hoveredStack.getName().copy().append(Text.literal(" -> ")).append(cursorStack.getName().copy()));
                context.drawTooltip(this.textRenderer, getTooltipFromItem(client, hoveredStack), hoveredStack.getTooltipData(), x, y);
            }
        }
    }

}
