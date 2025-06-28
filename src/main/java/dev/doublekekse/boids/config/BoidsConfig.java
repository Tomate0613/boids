package dev.doublekekse.boids.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonGrammar;
import blue.endless.jankson.api.SyntaxError;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.entity.EntityType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BoidsConfig {
    private static final Jankson jankson = new Jankson.Builder().build();
    private static final Path filePath = FabricLoader.getInstance().getConfigDir().resolve("boids.json5");

    @Comment("Separation controls. All angles are in degrees")
    public float separationInfluence = 0.6f;
    public float separationRange = 2.5f;
    public float separationAngle = 70;

    @Comment("Alignment controls")
    public float alignmentInfluence = 0.4f;
    public float alignmentAngle = 100;

    @Comment("Cohesion controls")
    public float cohesionInfluence = 0.4f;
    public float cohesionAngle = 70;

    @Comment("Speed limits")
    public float minSpeed = 0.2f;
    public float maxSpeed = 0.3f;

    @Comment("Random influence")
    public float randomness = 0.005f;

    public enum DefaultEntities {
        DEFAULT(List.of(EntityType.SALMON, EntityType.COD, EntityType.TROPICAL_FISH)),
        NONE(Collections.emptyList());

        public final Collection<EntityType<?>> types;

        DefaultEntities(Collection<EntityType<?>> types) {
            this.types = types;
        }
    }

    @Comment("Which entities to be included by default. Can be one of 'DEFAULT' or 'NONE'")
    public DefaultEntities defaultEntities = DefaultEntities.DEFAULT;

    @Comment("Lists of additional entities to include or exclude by resource ID, e.g., 'minecraft:salmon'")
    public List<String> includedEntities = new ArrayList<>();
    public List<String> excludeEntities = new ArrayList<>();


    private static BoidsConfig loadFromJSON(String json) throws SyntaxError {
        return jankson.fromJson(json, BoidsConfig.class);
    }

    public static BoidsConfig load() {
        if (!Files.exists(filePath)) {
            return new BoidsConfig();
        }

        try {
            var json = Files.readString(filePath, StandardCharsets.UTF_8);
            return loadFromJSON(json);
        } catch (IOException | SyntaxError e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            //noinspection ResultOfMethodCallIgnored
            filePath.getParent().toFile().mkdirs();
            Files.writeString(filePath, toJSON(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJSON() {
        return jankson.toJson(this).toJson(JsonGrammar.JANKSON);
    }
}
