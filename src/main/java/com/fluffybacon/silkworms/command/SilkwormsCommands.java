package com.fluffybacon.silkworms.command;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.entity.CocoonEntity;
import com.fluffybacon.silkworms.entity.SilkMothEntity;
import com.fluffybacon.silkworms.entity.SilkwormEntity;
import com.fluffybacon.silkworms.item.SilkwormBucketItem;
import com.fluffybacon.silkworms.registry.ModItems;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin commands for the v0.5.2 safety release. The mod's honest removal
 * story is "cleanup-friendly removal while the mod is still installed":
 * back up, run {@code /silkworms prepare_removal confirm}, save, stop, then
 * remove the jar. Only loaded areas and player inventories can be processed —
 * the commands say so out loud.
 */
public final class SilkwormsCommands {
	private SilkwormsCommands() {
	}

	private static final String TAG_CONVERTED = "converted_from_silk_moth";
	private static final String TAG_CLEANUP = "silkworms_cleanup_v052";

	public static void register() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("silkworms")
						.requires(CommandManager.requirePermissionLevel(CommandManager.GAMEMASTERS_CHECK))
						.then(CommandManager.literal("safety_audit")
								.executes(ctx -> safetyAudit(ctx.getSource())))
						.then(CommandManager.literal("removal_check")
								.executes(ctx -> removalCheck(ctx.getSource())))
						.then(CommandManager.literal("prepare_removal")
								.executes(ctx -> prepareRemovalWarning(ctx.getSource()))
								.then(CommandManager.literal("confirm")
										.executes(ctx -> prepareRemovalConfirm(ctx.getSource()))))));
	}

	private static void say(ServerCommandSource source, String message) {
		source.sendFeedback(() -> Text.literal(message), false);
	}

	private static void warn(ServerCommandSource source, String message) {
		source.sendFeedback(() -> Text.literal(message).formatted(Formatting.YELLOW), false);
	}

	// ------------------------------------------------------------------
	private static int safetyAudit(ServerCommandSource source) {
		String version = FabricLoader.getInstance().getModContainer(Silkworms.MOD_ID)
				.map(container -> container.getMetadata().getVersion().getFriendlyString())
				.orElse("unknown");
		say(source, "=== Silkworms Safety Audit ===");
		say(source, "Mod version: " + version + " | Minecraft: " + source.getServer().getVersion());
		say(source, "Registered content: 3 entities (silkworm, cocoon, silk_moth), "
				+ "4 items (3 spawn eggs, silkworm_bucket), 2 item data components.");
		say(source, "No custom blocks, block entities, dimensions, mixins or networking.");
		say(source, "Cleanup commands available: /silkworms removal_check, /silkworms prepare_removal.");
		warn(source, "Removal should only ever be done after a full world backup.");
		say(source, "Recommended removal path: backup -> /silkworms prepare_removal confirm "
				+ "-> save & stop -> remove the jar.");
		return 1;
	}

	// ------------------------------------------------------------------
	private static int removalCheck(ServerCommandSource source) {
		MinecraftServer server = source.getServer();
		int worms = 0;
		int cocoons = 0;
		int tamedMoths = 0;
		int wildMoths = 0;
		int droppedModItems = 0;
		for (ServerWorld world : server.getWorlds()) {
			for (Entity entity : world.iterateEntities()) {
				if (entity instanceof SilkwormEntity) {
					worms++;
				} else if (entity instanceof CocoonEntity) {
					cocoons++;
				} else if (entity instanceof SilkMothEntity moth) {
					if (moth.isTamed()) {
						tamedMoths++;
					} else {
						wildMoths++;
					}
				} else if (entity instanceof ItemEntity item && isModItem(item.getStack())) {
					droppedModItems++;
				}
			}
		}
		int buckets = 0;
		int otherItems = 0;
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			int[] counts = countInventoryItems(player);
			buckets += counts[0];
			otherItems += counts[1];
		}
		say(source, "=== Silkworms Removal Check (dry run, nothing changed) ===");
		say(source, "Silkworms: " + worms + " | Cocoons: " + cocoons
				+ " | Tamed moths: " + tamedMoths + " | Wild moths: " + wildMoths);
		say(source, "Silkworm buckets in player inventories/ender chests: " + buckets);
		say(source, "Other mod items (spawn eggs) in player inventories: " + otherItems);
		say(source, "Dropped mod items in the world: " + droppedModItems);
		warn(source, "Only LOADED chunks and ONLINE players' inventories/ender chests are counted.");
		warn(source, "Items inside placed containers (chests etc.) are NOT scanned - move them "
				+ "to a player inventory before cleanup.");
		warn(source, "Load any important areas (bases, moth homes) and run this check again.");
		return 1;
	}

	// ------------------------------------------------------------------
	private static int prepareRemovalWarning(ServerCommandSource source) {
		warn(source, "This will convert/remove Silkworms mod content in currently loaded areas.");
		warn(source, "Tamed silk moths become vanilla Allays (a friendly stand-in - they keep "
				+ "position and name, NOT riding/harness/size/variant behavior).");
		warn(source, "Wild moths, silkworms and cocoons are removed. Silkworm buckets become "
				+ "vanilla buckets (+1 string per stored worm).");
		warn(source, "BACK UP YOUR WORLD FIRST.");
		say(source, "Run '/silkworms prepare_removal confirm' to continue.");
		return 1;
	}

	private static int prepareRemovalConfirm(ServerCommandSource source) {
		MinecraftServer server = source.getServer();
		int converted = 0;
		int failed = 0;
		int removedWild = 0;
		int removedWorms = 0;
		int removedCocoons = 0;
		int droppedConverted = 0;
		for (ServerWorld world : server.getWorlds()) {
			// Snapshot first: we must not mutate while iterating.
			List<Entity> snapshot = new ArrayList<>();
			for (Entity entity : world.iterateEntities()) {
				snapshot.add(entity);
			}
			for (Entity entity : snapshot) {
				if (entity.isRemoved()) {
					continue;
				}
				if (entity instanceof SilkMothEntity moth) {
					if (moth.isTamed()) {
						if (convertToAllay(world, moth)) {
							converted++;
						} else {
							failed++; // replacement failed -> moth kept, reported
						}
					} else {
						moth.discard();
						removedWild++;
					}
				} else if (entity instanceof SilkwormEntity worm) {
					worm.discard();
					removedWorms++;
				} else if (entity instanceof CocoonEntity cocoon) {
					cocoon.discard();
					removedCocoons++;
				} else if (entity instanceof ItemEntity item && isModItem(item.getStack())) {
					convertDroppedItem(world, item);
					droppedConverted++;
				}
			}
		}
		int bucketsConverted = 0;
		int stringGiven = 0;
		int itemsRemoved = 0;
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			int[] result = cleanInventory(player, player.getInventory());
			int[] ender = cleanInventory(player, player.getEnderChestInventory());
			bucketsConverted += result[0] + ender[0];
			stringGiven += result[1] + ender[1];
			itemsRemoved += result[2] + ender[2];
		}
		say(source, "=== Silkworms Cleanup Summary ===");
		say(source, "Tamed moths converted to Allays: " + converted
				+ (failed > 0 ? " (FAILED to convert " + failed + " - they were kept, try again)" : ""));
		say(source, "Removed: " + removedWild + " wild moths, " + removedWorms
				+ " silkworms, " + removedCocoons + " cocoons.");
		say(source, "Buckets converted to vanilla buckets: " + bucketsConverted
				+ " (+" + stringGiven + " string). Other mod items removed: "
				+ itemsRemoved + " (+ " + droppedConverted + " dropped items handled).");
		warn(source, "Only loaded areas were processed. Load remaining areas and run again, "
				+ "then save, stop, and remove the jar.");
		return 1;
	}

	/** Spawns the replacement Allay first; the moth is only removed on success. */
	private static boolean convertToAllay(ServerWorld world, SilkMothEntity moth) {
		AllayEntity allay = EntityType.ALLAY.create(world, SpawnReason.CONVERSION);
		if (allay == null) {
			return false;
		}
		moth.removeAllPassengers();
		allay.refreshPositionAndAngles(moth.getX(), moth.getY(), moth.getZ(), moth.getYaw(), 0.0F);
		if (moth.getCustomName() != null) {
			allay.setCustomName(moth.getCustomName());
		}
		allay.setPersistent();
		allay.addCommandTag(TAG_CONVERTED);
		allay.addCommandTag(TAG_CLEANUP);
		if (!world.spawnEntity(allay)) {
			return false;
		}
		moth.discard();
		return true;
	}

	/** Dropped bucket -> dropped vanilla bucket + string; dropped egg -> gone. */
	private static void convertDroppedItem(ServerWorld world, ItemEntity item) {
		ItemStack stack = item.getStack();
		if (stack.isOf(ModItems.SILKWORM_BUCKET)) {
			int worms = SilkwormBucketItem.getCount(stack);
			item.setStack(new ItemStack(Items.BUCKET));
			ItemEntity string = new ItemEntity(world, item.getX(), item.getY(), item.getZ(),
					new ItemStack(Items.STRING, worms));
			world.spawnEntity(string);
		} else {
			item.discard();
		}
	}

	/** Returns {bucketsConverted, stringGiven, otherItemsRemoved}. */
	private static int[] cleanInventory(ServerPlayerEntity player, Inventory inventory) {
		int buckets = 0;
		int string = 0;
		int removed = 0;
		for (int slot = 0; slot < inventory.size(); slot++) {
			ItemStack stack = inventory.getStack(slot);
			if (stack.isEmpty() || !isModItem(stack)) {
				continue;
			}
			if (stack.isOf(ModItems.SILKWORM_BUCKET)) {
				int worms = SilkwormBucketItem.getCount(stack); // clamped 1..5, legacy-safe
				inventory.setStack(slot, new ItemStack(Items.BUCKET));
				player.getInventory().offerOrDrop(new ItemStack(Items.STRING, worms));
				buckets++;
				string += worms;
			} else {
				inventory.setStack(slot, ItemStack.EMPTY);
				removed += stack.getCount();
			}
		}
		return new int[]{buckets, string, removed};
	}

	/** Returns {buckets, otherModItems} without changing anything. */
	private static int[] countInventoryItems(ServerPlayerEntity player) {
		int buckets = 0;
		int other = 0;
		Inventory[] inventories = {player.getInventory(), player.getEnderChestInventory()};
		for (Inventory inventory : inventories) {
			for (int slot = 0; slot < inventory.size(); slot++) {
				ItemStack stack = inventory.getStack(slot);
				if (stack.isEmpty() || !isModItem(stack)) {
					continue;
				}
				if (stack.isOf(ModItems.SILKWORM_BUCKET)) {
					buckets++;
				} else {
					other += stack.getCount();
				}
			}
		}
		return new int[]{buckets, other};
	}

	private static boolean isModItem(ItemStack stack) {
		return stack.isOf(ModItems.SILKWORM_BUCKET)
				|| stack.isOf(ModItems.SILKWORM_SPAWN_EGG)
				|| stack.isOf(ModItems.COCOON_SPAWN_EGG)
				|| stack.isOf(ModItems.SILK_MOTH_SPAWN_EGG);
	}
}
