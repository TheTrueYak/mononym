package net.yak.mononym;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MononymClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
            if (stack.isOf(Items.NAME_TAG)) {
                list.add(Text.literal("[").formatted(Formatting.GRAY).append(Text.translatable("tooltip.mononym.use").formatted(Formatting.GOLD).append(Text.literal("]").append(Text.translatable("tooltip.mononym.rename")).formatted(Formatting.GRAY))));
            }
        });
    }
}
