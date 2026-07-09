package com.fluffybacon.silkworms.item;

import com.fluffybacon.silkworms.SilkwormsBalance;
import com.fluffybacon.silkworms.entity.SilkwormEntity;
import com.fluffybacon.silkworms.entity.SilkwormVariant;
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A bucket carrying up to {@link SilkwormsBalance#SILKWORM_BUCKET_CAPACITY}
 * silkworms. The worms' colour variants are remembered in an ordered list
 * component so released worms come back out the same colour they went in.
 * Buckets saved by v0.4.0 (count only, no list) still work: their released
 * worms get a random variant. Picking worms up is handled by
 * {@link SilkwormEntity#interactMob}.
 */
public class SilkwormBucketItem extends Item {
	public SilkwormBucketItem(Settings settings) {
		super(settings);
	}

	/** Stored variant ids, or an empty list for a legacy count-only bucket.
	 * Defensively clamped to capacity so corrupted/edited item data can never
	 * over-release worms (invalid ids are clamped later by byId). */
	public static List<Integer> getVariantIds(ItemStack stack) {
		List<Integer> ids = stack.get(ModComponents.SILKWORM_VARIANTS);
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		return ids.size() <= SilkwormsBalance.SILKWORM_BUCKET_CAPACITY
				? ids
				: List.copyOf(ids.subList(0, SilkwormsBalance.SILKWORM_BUCKET_CAPACITY));
	}

	public static int getCount(ItemStack stack) {
		List<Integer> ids = getVariantIds(stack);
		if (!ids.isEmpty()) {
			return ids.size();
		}
		return Math.clamp(stack.getOrDefault(ModComponents.SILKWORM_COUNT, 1),
				1, SilkwormsBalance.SILKWORM_BUCKET_CAPACITY);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		if (!(world instanceof ServerWorld serverWorld)) {
			return ActionResult.SUCCESS;
		}
		ItemStack stack = context.getStack();
		List<Integer> ids = new ArrayList<>(getVariantIds(stack));

		// Decide which variant leaves first: the front of the stored list, or a
		// random one for a legacy bucket that never recorded its variants.
		SilkwormVariant released;
		if (!ids.isEmpty()) {
			released = SilkwormVariant.byId(ids.remove(0));
		} else {
			released = SilkwormVariant.pickWeighted(world.getRandom());
		}

		BlockPos releasePos = context.getBlockPos().offset(context.getSide());
		SilkwormEntity worm = new SilkwormEntity(ModEntities.SILKWORM, serverWorld);
		worm.refreshPositionAndAngles(releasePos.getX() + 0.5, releasePos.getY(),
				releasePos.getZ() + 0.5, world.getRandom().nextFloat() * 360.0F, 0.0F);
		worm.setVariant(released);
		serverWorld.spawnEntity(worm);
		world.playSound(null, releasePos, SoundEvents.ITEM_BUCKET_EMPTY_FISH, SoundCategory.NEUTRAL, 1.0F, 1.0F);

		PlayerEntity player = context.getPlayer();
		if (player == null || !player.isCreative()) {
			int remaining = getCount(stack) - 1;
			if (remaining <= 0) {
				setStack(player, context, new ItemStack(Items.BUCKET));
			} else {
				ItemStack updated = stack.copy();
				updated.set(ModComponents.SILKWORM_COUNT, remaining);
				if (!getVariantIds(stack).isEmpty()) {
					updated.set(ModComponents.SILKWORM_VARIANTS, List.copyOf(ids));
				}
				setStack(player, context, updated);
			}
		}
		return ActionResult.SUCCESS_SERVER;
	}

	private static void setStack(PlayerEntity player, ItemUsageContext context, ItemStack newStack) {
		if (player != null) {
			player.setStackInHand(context.getHand(), newStack);
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context,
			TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		super.appendTooltip(stack, context, displayComponent, textConsumer, type);
		textConsumer.accept(Text.translatable("item.silkworms.silkworm_bucket.count",
				getCount(stack), SilkwormsBalance.SILKWORM_BUCKET_CAPACITY).formatted(Formatting.GRAY));

		List<Integer> ids = getVariantIds(stack);
		if (!ids.isEmpty()) {
			Set<String> names = new LinkedHashSet<>();
			for (int id : ids) {
				names.add(Text.translatable("silkworms.variant." + SilkwormVariant.byId(id).getName()).getString());
			}
			textConsumer.accept(Text.translatable("item.silkworms.silkworm_bucket.variants",
					String.join(", ", names)).formatted(Formatting.DARK_GRAY));
		}
	}
}
