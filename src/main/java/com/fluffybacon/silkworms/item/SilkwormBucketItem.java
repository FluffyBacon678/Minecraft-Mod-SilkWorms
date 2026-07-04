package com.fluffybacon.silkworms.item;

import com.fluffybacon.silkworms.SilkwormsBalance;
import com.fluffybacon.silkworms.entity.SilkwormEntity;
import com.fluffybacon.silkworms.registry.ModComponents;
import com.fluffybacon.silkworms.registry.ModEntities;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Consumer;

/**
 * A bucket carrying up to {@link SilkwormsBalance#SILKWORM_BUCKET_CAPACITY}
 * silkworms (count stored in a data component). Using it on a block releases
 * one worm per click; at zero it turns back into an empty bucket. Picking
 * worms up is handled by {@link SilkwormEntity#interactMob}.
 */
public class SilkwormBucketItem extends Item {
	public SilkwormBucketItem(Settings settings) {
		super(settings);
	}

	public static int getCount(ItemStack stack) {
		return stack.getOrDefault(ModComponents.SILKWORM_COUNT, 1);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!(world instanceof ServerWorld serverWorld)) {
			return ActionResult.SUCCESS;
		}
		BlockPos releasePos = context.getBlockPos().offset(context.getSide());
		SilkwormEntity worm = new SilkwormEntity(ModEntities.SILKWORM, serverWorld);
		worm.refreshPositionAndAngles(releasePos.getX() + 0.5, releasePos.getY(),
				releasePos.getZ() + 0.5, world.getRandom().nextFloat() * 360.0F, 0.0F);
		serverWorld.spawnEntity(worm);
		world.playSound(null, releasePos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);

		PlayerEntity player = context.getPlayer();
		if (player == null || !player.isCreative()) {
			ItemStack stack = context.getStack();
			int count = getCount(stack);
			if (count <= 1 && player != null) {
				player.setStackInHand(context.getHand(), new ItemStack(Items.BUCKET));
			} else {
				stack.set(ModComponents.SILKWORM_COUNT, count - 1);
			}
		}
		return ActionResult.SUCCESS_SERVER;
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context,
			TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		super.appendTooltip(stack, context, displayComponent, textConsumer, type);
		textConsumer.accept(Text.translatable("item.silkworms.silkworm_bucket.count",
				getCount(stack), SilkwormsBalance.SILKWORM_BUCKET_CAPACITY).formatted(Formatting.GRAY));
	}
}
