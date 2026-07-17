package com.fluffybacon.silkworms.registry;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.SilkwormsBalance;
import com.fluffybacon.silkworms.SilkwormsConfig;
import com.fluffybacon.silkworms.entity.CocoonEntity;
import com.fluffybacon.silkworms.entity.SilkMothEntity;
import com.fluffybacon.silkworms.entity.SilkwormEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.Heightmap;

/**
 * Registers the three lifecycle entity types, their attributes, the silkworm's
 * natural spawn placement rule and a modest biome spawn entry.
 */
public final class ModEntities {
	private ModEntities() {
	}

	public static EntityType<SilkwormEntity> SILKWORM;
	public static EntityType<CocoonEntity> COCOON;
	public static EntityType<SilkMothEntity> SILK_MOTH;

	public static void register() {
		SILKWORM = register("silkworm", EntityType.Builder
				// AMBIENT (the bat group): its spawn cycle runs continuously
				// underground and its small cap self-limits — the established
				// pattern for cave critters. CREATURE placement is surface-only
				// at worldgen and its cap is saturated by farm animals, so
				// lush-cave worms would never spawn from it.
				.create(SilkwormEntity::new, SpawnGroup.AMBIENT)
				.dimensions(SilkwormsBalance.SILKWORM_WIDTH, SilkwormsBalance.SILKWORM_HEIGHT)
				.maxTrackingRange(8));

		COCOON = register("cocoon", EntityType.Builder
				.create(CocoonEntity::new, SpawnGroup.MISC)
				.dimensions(SilkwormsBalance.COCOON_WIDTH, SilkwormsBalance.COCOON_HEIGHT)
				.maxTrackingRange(8));

		SILK_MOTH = register("silk_moth", EntityType.Builder
				.create(SilkMothEntity::new, SpawnGroup.CREATURE)
				.dimensions(SilkwormsBalance.SILK_MOTH_WIDTH, SilkwormsBalance.SILK_MOTH_HEIGHT)
				.maxTrackingRange(10));

		FabricDefaultAttributeRegistry.register(SILKWORM, SilkwormEntity.createSilkwormAttributes());
		FabricDefaultAttributeRegistry.register(COCOON, CocoonEntity.createCocoonAttributes());
		FabricDefaultAttributeRegistry.register(SILK_MOTH, SilkMothEntity.createSilkMothAttributes());

		// Only the silkworm spawns naturally; cocoon/moth arrive via the life cycle.
		SpawnRestriction.register(SILKWORM, SpawnLocationTypes.ON_GROUND,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SilkwormEntity::canSpawn);

		// Natural spawns (AMBIENT pool matches the worm's spawn group).
		// Registered once at startup, so the config toggle needs a restart.
		if (SilkwormsConfig.get().naturalSilkwormSpawning) {
			// Modest surface spawns in forest biomes.
			BiomeModifications.addSpawn(
					BiomeSelectors.tag(BiomeTags.IS_FOREST),
					SpawnGroup.AMBIENT, SILKWORM,
					SilkwormsBalance.SILKWORM_SPAWN_WEIGHT,
					SilkwormsBalance.SILKWORM_MIN_GROUP_SIZE,
					SilkwormsBalance.SILKWORM_MAX_GROUP_SIZE);
			// Discoverable little colonies in lush caves (moss/azalea/vines).
			BiomeModifications.addSpawn(
					BiomeSelectors.includeByKey(BiomeKeys.LUSH_CAVES),
					SpawnGroup.AMBIENT, SILKWORM,
					SilkwormsBalance.SILKWORM_LUSH_SPAWN_WEIGHT,
					SilkwormsBalance.SILKWORM_LUSH_MIN_GROUP,
					SilkwormsBalance.SILKWORM_LUSH_MAX_GROUP);
		}
	}

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE,
				Identifier.of(Silkworms.MOD_ID, name));
		return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
	}
}
