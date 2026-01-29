package net.yak.mononym.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.yak.mononym.Mononym;

public record NameTagRenameC2SPayload(int entityId, int handId, String name) implements CustomPayload {
    public static final Identifier PAYLOAD_ID = Mononym.id("name_tag_rename");
    public static final Id<NameTagRenameC2SPayload> ID = new Id<>(PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, NameTagRenameC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, NameTagRenameC2SPayload::entityId, PacketCodecs.INTEGER, NameTagRenameC2SPayload::handId, PacketCodecs.STRING, NameTagRenameC2SPayload::name, NameTagRenameC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static class Receiver implements ServerPlayNetworking.PlayPayloadHandler<NameTagRenameC2SPayload> {

        @Override
        public void receive(NameTagRenameC2SPayload payload, ServerPlayNetworking.Context context) {
            Entity entity = context.player().getWorld().getEntityById(payload.entityId());
            if (entity instanceof PlayerEntity playerEntity) {
                ItemStack stack = playerEntity.getStackInHand(payload.handId() == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND);
                if (stack.isOf(Items.NAME_TAG)) {
                    if (payload.name.isBlank() && stack.contains(DataComponentTypes.CUSTOM_NAME)) {
                        stack.remove(DataComponentTypes.CUSTOM_NAME);
                    }
                    else {
                        stack.set(DataComponentTypes.CUSTOM_NAME, Text.literal(payload.name()));
                    }
                }
            }
        }
    }
}
