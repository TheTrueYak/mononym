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
    private static final ButtonTextures CANCEL_BUTTON_TEXTURES = new ButtonTextures(Mononym.id("cancel_button"), Mononym.id("apply_button_highlighted"));
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

        this.textField = new TextFieldWidget(client.textRenderer, halfWidth - 75, halfHeight - 30, 150, 17, null, stack.getName());
        this.textField.setDrawsBackground(false);
        this.textField.setMaxLength(50);
        this.textField.setText("");
        this.addDrawableChild(this.textField);
        this.setInitialFocus(this.textField);

        this.applyButton = new TexturedButtonWidget(halfHeight + 200, halfHeight + 14, 10, 10, APPLY_BUTTON_TEXTURES, pressAction -> {
            if (!this.textField.getText().isEmpty()) {
                ClientPlayNetworking.send(new NameTagRenameC2SPayload(this.client.player.getId(), this.handId, this.textField.getText()));
                this.close();
            }
        });
        this.applyButton.active = canApply();
        this.addDrawableChild(applyButton);

        this.cancelButton = new TexturedButtonWidget(halfHeight + 187, halfHeight + 14, 10, 10, CANCEL_BUTTON_TEXTURES, pressAction -> {
            this.close();
        });
        this.cancelButton.active = true;
        this.addDrawableChild(cancelButton);

        if (this.stack.contains(DataComponentTypes.CUSTOM_NAME)) { // sets default to current name
            this.textField.setText(this.stack.get(DataComponentTypes.CUSTOM_NAME).getString());
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
        context.drawTexture(
                Mononym.id("textures/gui/name_tag_screen.png"),
                context.getScaledWindowWidth() / 2 - 120,
                context.getScaledWindowHeight() / 2 - 60,
                0,
                0,
                256,
                150,
                256,
                256
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257) { // (enter key)
            if (this.applyButton.active) {
                if (!this.textField.getText().isEmpty()) {
                    ClientPlayNetworking.send(new NameTagRenameC2SPayload(this.client.player.getId(), this.handId, this.textField.getText()));
                    this.close();
                }
                return true;
            }
        }
        boolean value = super.keyPressed(keyCode, scanCode, modifiers);
        this.applyButton.active = canApply();
        return value;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private boolean canApply() {
        String currentName = this.stack.contains(DataComponentTypes.CUSTOM_NAME) ? this.stack.get(DataComponentTypes.CUSTOM_NAME).getString() : "";
        String newName = this.textField.getText();
        return !(currentName.equals(newName) || newName.isBlank());
    }

    public static void open(ItemStack stack, int id) {
        MinecraftClient.getInstance().setScreen(new NametagRenameScreen(stack, id));
    }

}
