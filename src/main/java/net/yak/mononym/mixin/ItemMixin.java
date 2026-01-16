package net.yak.mononym.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.yak.mononym.client.screen.NametagRenameScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemMixin {

	@WrapMethod(method = "use")
	private TypedActionResult<ItemStack> mononym$nameTagScreen(World world, PlayerEntity user, Hand hand, Operation<TypedActionResult<ItemStack>> original) {
		ItemStack stack = user.getStackInHand(hand);
		if (stack.isOf(Items.NAME_TAG)) {
			user.setCurrentHand(hand);
			if (world.isClient) {
				NametagRenameScreen.open(stack, hand == Hand.MAIN_HAND ? 0 : 1);
				user.swingHand(hand);
				return TypedActionResult.success(stack);
			}
			return TypedActionResult.pass(user.getStackInHand(hand));
		}
		return original.call(world, user, hand);
	}

	@WrapMethod(method = "onStackClicked")
	private boolean mononym$renameWithClick(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, Operation<Boolean> original) {
		if (clickType == ClickType.RIGHT && !stack.isEmpty() && stack.isOf(Items.NAME_TAG)) {
			ItemStack slotStack = slot.getStack();
			if (!slotStack.isEmpty()) {
				if (stack.isOf(Items.NAME_TAG) && stack.contains(DataComponentTypes.CUSTOM_NAME)) {
					if (!stack.getName().equals(slotStack.getName())) {
						slotStack.set(DataComponentTypes.CUSTOM_NAME, stack.getName());
						stack.decrement(1);
						slot.markDirty();
						if (player.getWorld().isClient()) {
							player.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0f, 1.0f);
						}
					}
					return true;
				}
			}
		}
		return original.call(stack, slot, clickType, player);
	}



}