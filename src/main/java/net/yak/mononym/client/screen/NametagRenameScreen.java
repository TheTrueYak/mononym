package net.yak.mononym.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.yak.mononym.Mononym;
import net.yak.mononym.networking.NameTagRenameC2SPayload;

public class NametagRenameScreen extends Screen {

    private static final ButtonTextures APPLY_BUTTON_TEXTURES = new ButtonTextures(Mononym.id("apply_button"), Mononym.id("apply_button_highlighted"));
    private static final ButtonTextures CANCEL_BUTTON_TEXTURES = new ButtonTextures(Mononym.id("cancel_button"), Mononym.id("cancel_button_highlighted"));
    private TextFieldWidget textField;
    private TexturedButtonWidget applyButton;
    private TexturedButtonWidget cancelButton;

    private final ItemStack stack;
    private final int handId;

    protected NametagRenameScreen(ItemStack stack, int handId) {
        super(Text.literal(""));
        this.stack = stack;
        this.handId = handId;
    }

    @Override
    protected void init() {
        int halfWidth = this.width / 2;
        int halfHeight = this.height / 2;
        Text name = this.stack.get(DataComponentTypes.CUSTOM_NAME);

        this.textField = new TextFieldWidget(client.textRenderer, halfWidth - 75, halfHeight - 28, 150, 17, null, stack.getName());
        this.textField.setDrawsBackground(false);
        this.textField.setMaxLength(50);
        this.textField.setText(name != null ? name.getString() : ""); // sets default to current name, otherwise leaves blank
        this.addDrawableChild(this.textField);
        this.setInitialFocus(this.textField);

        this.applyButton = new TexturedButtonWidget(halfWidth + 70, halfHeight - 8, 9, 9, APPLY_BUTTON_TEXTURES, buttonWidget -> {
            ClientPlayNetworking.send(new NameTagRenameC2SPayload(this.client.player.getId(), this.handId, this.textField.getText()));
            this.close();
        });
        this.applyButton.active = this.canApply();
        this.addDrawableChild(applyButton);

        this.cancelButton = new TexturedButtonWidget(halfWidth + 57, halfHeight - 8, 9, 9, CANCEL_BUTTON_TEXTURES, buttonWidget -> {
            this.close();
        });
        this.cancelButton.active = true;
        this.addDrawableChild(cancelButton);

    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawTexture(
                Mononym.id("textures/gui/name_tag_screen.png"),
                context.getScaledWindowWidth() / 2 - 120,
                context.getScaledWindowHeight() / 2 - 60,
                0, 0, 256, 150, 256, 256);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.applyButton.active = this.canApply();
        boolean value = super.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == 257) { // (enter key)
            if (this.applyButton.active) {
                ClientPlayNetworking.send(new NameTagRenameC2SPayload(this.client.player.getId(), this.handId, this.textField.getText()));
                this.close();
                return true;
            }
        }
        this.applyButton.active = this.canApply();
        return value;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.applyButton.active = this.canApply();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private boolean canApply() {
        String currentName = this.stack.contains(DataComponentTypes.CUSTOM_NAME) ? this.stack.get(DataComponentTypes.CUSTOM_NAME).getString() : "";
        String newName = this.textField.getText();
        if (newName.isBlank() && this.stack.contains(DataComponentTypes.CUSTOM_NAME)) {
            return true;
        }
        return !(currentName.equals(newName));
    }

    public static void open(ItemStack stack, int id) {
        MinecraftClient.getInstance().setScreen(new NametagRenameScreen(stack, id));
    }

}
