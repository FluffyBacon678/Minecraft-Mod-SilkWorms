package com.fluffybacon.silkworms.registry;

import com.fluffybacon.silkworms.Silkworms;
import com.fluffybacon.silkworms.SilkwormsBalance;
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
				.create(SilkwormEntity::new, SpawnGroup.CREATURE)
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

		// Modest spawns in forest biomes ("grassy or forest-like areas").
		BiomeModifications.addSpawn(
				BiomeSelectors.tag(BiomeTags.IS_FOREST),
				SpawnGroup.CREATURE, SILKWORM,
				SilkwormsBalance.SILKWORM_SPAWN_WEIGHT,
				SilkwormsBalance.SILKWORM_MIN_GROUP_SIZE,
				SilkwormsBalance.SILKWORM_MAX_GROUP_SIZE);
	}

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE,
				Identifier.of(Silkworms.MOD_ID, name));
		return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
	}
}
