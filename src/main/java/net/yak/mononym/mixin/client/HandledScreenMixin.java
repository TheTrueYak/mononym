package net.yak.mononym.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.yak.mononym.Mononym;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

    @Shadow @Final protected T handler;
    @Shadow @Nullable protected Slot focusedSlot;

    @Unique private static final Identifier SELECTION_OVERLAY = Mononym.id("selection_overlay");

    protected HandledScreenMixin(Text title) {
        super(title);
    }

    @WrapMethod(method = "renderCursorStack")
    private void mononym$selectionOverlayTooltip(DrawContext context, int mouseX, int mouseY, Operation<Void> original) {
        original.call(context, mouseX, mouseY);
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
                context.drawTooltip(this.textRenderer, getTooltipFromItem(client, hoveredStack), hoveredStack.getTooltipData(), mouseX, mouseY, hoveredStack.get(DataComponentTypes.TOOLTIP_STYLE));
                OrderedText cursorStackText = cursorStack.getName().asOrderedText();
                int offset = TooltipComponent.of(cursorStackText).getWidth(textRenderer);
                int i = textRenderer.getWidth(cursorStackText);
                int j = mouseX + offset / 2;
                TooltipComponent tooltipComponent = TooltipComponent.of(cursorStackText);
                context.drawTooltipImmediately(textRenderer, List.of(tooltipComponent), j - i / 2, mouseY - 15, HoveredTooltipPositioner.INSTANCE, cursorStack.get(DataComponentTypes.TOOLTIP_STYLE));
            }
        }
    }

    @WrapMethod(method = "drawSlotHighlightFront")
    private void mononym$selectionOverlay(DrawContext context, Operation<Void> original) {
        original.call(context);
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
                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, SELECTION_OVERLAY, this.focusedSlot.x - 1, this.focusedSlot.y - 1, 18, 18);
            }
        }
    }

}
