package net.yak.mononym.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {

    @Shadow private TextFieldWidget nameField;

    @Shadow @Final private static Identifier TEXT_FIELD_DISABLED_TEXTURE;

    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/AnvilScreen;addSelectableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private Element mononym$removeAnvilText(AnvilScreen instance, Element element, Operation<Element> original, @Cancellable CallbackInfo ci) {
        this.nameField.setDimensions(0, 0);
        this.nameField.setMaxLength(0);
        this.nameField.setFocused(false);
        return null;
    }

    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setEditable(Z)V"))
    private void mononym$removeAnvilTextAccess(TextFieldWidget instance, boolean editable, Operation<Void> original) {
        original.call(instance, false);
    }

    @WrapOperation(method = "renderForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V"))
    private void mononym$removeAnvilText(TextFieldWidget instance, DrawContext context, int mouseX, int mouseY, float delta, Operation<Void> original) {

    }

    @WrapOperation(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    private void mononym$removeTextBackground(DrawContext instance, Identifier texture, int x, int y, int width, int height, Operation<Void> original) {
        original.call(instance, TEXT_FIELD_DISABLED_TEXTURE, x, y, width, height);
    }

}
