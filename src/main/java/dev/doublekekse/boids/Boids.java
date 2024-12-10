package dev.doublekekse.boids;

import dev.doublekekse.boids.config.BoidsConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class Boids implements ModInitializer {
    public static BoidsConfig CONFIG = BoidsConfig.load();

    /**
     * True, when the mod is temporarily enabled for compat reasons
     * TODO: Replace this with a proper solution
     */
    public static boolean disabled = false;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> {
                    dispatcher.register(
                            literal("boids").requires((source) -> source.hasPermission(2)).then(literal("config").then(literal("reload").executes(ctx -> {
                                CONFIG = BoidsConfig.load();

                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.boids.config.reload"), true);

                                return 1;
                            })))
                    );
                }
        );
    }
}
