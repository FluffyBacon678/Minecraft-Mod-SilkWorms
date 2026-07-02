package com.fluffybacon.silkworms.registry;

import com.fluffybacon.silkworms.Silkworms;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Registers spawn eggs for every lifecycle stage (all three are handy for
 * testing) and adds them to the vanilla Spawn Eggs creative tab.
 */
public final class ModItems {
	private ModItems() {
	}

	public static Item SILKWORM_SPAWN_EGG;
	public static Item COCOON_SPAWN_EGG;
	public static Item SILK_MOTH_SPAWN_EGG;

	public static void register() {
		SILKWORM_SPAWN_EGG = registerSpawnEgg("silkworm_spawn_egg", ModEntities.SILKWORM);
		COCOON_SPAWN_EGG = registerSpawnEgg("cocoon_spawn_egg", ModEntities.COCOON);
		SILK_MOTH_SPAWN_EGG = registerSpawnEgg("silk_moth_spawn_egg", ModEntities.SILK_MOTH);

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
			entries.add(SILKWORM_SPAWN_EGG);
			entries.add(COCOON_SPAWN_EGG);
			entries.add(SILK_MOTH_SPAWN_EGG);
		});
	}

	private static Item registerSpawnEgg(String name, EntityType<?> type) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Silkworms.MOD_ID, name));
		SpawnEggItem egg = new SpawnEggItem(new Item.Settings().registryKey(key).spawnEgg(type));
		return Registry.register(Registries.ITEM, key, egg);
	}
}
