package dev.doublekekse.boids;

import dev.doublekekse.boids.config.BoidsConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.*;

import static net.minecraft.commands.Commands.literal;

public class Boids implements ModInitializer {
    public static BoidsConfig CONFIG;
    public static BoidsSimulation SETTINGS;
    public static Set<EntityType<?>> AFFECTED_ENTITIES = Collections.emptySet();

    public static void loadConfig() {
        CONFIG = BoidsConfig.load();
        CONFIG.save();

        SETTINGS = new BoidsSimulation(
            CONFIG.separationInfluence,
            CONFIG.separationRange,
            BoidsSimulation.degrees(CONFIG.separationAngle),

            CONFIG.alignmentInfluence,
            BoidsSimulation.degrees(CONFIG.alignmentAngle),

            CONFIG.cohesionInfluence,
            BoidsSimulation.degrees(CONFIG.cohesionAngle),

            CONFIG.randomness
        );

        AFFECTED_ENTITIES = affectedEntities(CONFIG);
    }

    private static Set<EntityType<?>> affectedEntities(BoidsConfig config) {
        var entities = new ArrayList<>(config.defaultEntities.types);

        entities.addAll(getEntities(config.includedEntities));
        entities.removeAll(getEntities(config.excludeEntities));

        return Set.copyOf(entities);
    }

    private static Collection<? extends EntityType<?>> getEntities(List<String> ids) {
        return ids.stream()
            .map(ResourceLocation::tryParse)
            .filter(Objects::nonNull)
            .map(BuiltInRegistries.ENTITY_TYPE::get)
            .toList();
    }

    public static boolean isAffected(Entity entity) {
        return AFFECTED_ENTITIES.contains(entity.getType());
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register((minecraftServer) -> loadConfig());

        CommandRegistrationCallback.EVENT.register(
            (dispatcher, registryAccess, environment) -> {
                dispatcher.register(
                    literal("boids").requires((source) -> source.hasPermission(2)).then(literal("config").then(literal("reload").executes(ctx -> {
                        loadConfig();

                        ctx.getSource().sendSuccess(() -> Component.translatable("commands.boids.config.reload"), true);

                        return 1;
                    })))
                );
            }
        );
    }
}
