package dev.doublekekse.boids.asm;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.objectweb.asm.Type;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;

public class BoidsMixinPlugin implements IMixinConfigPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            var annotationNodes = MixinService.getService()
                .getBytecodeProvider()
                .getClassNode(mixinClassName)
                .visibleAnnotations;

            var node = Annotations.get(annotationNodes, Type.getDescriptor(IfMod.class));
            if (node == null) return true;

            var requiredModId = Annotations.<String>getValue(node, "value");
            var requiredModVersion = Annotations.getValue(node, "version", "");

            return FabricLoader.getInstance().getModContainer(requiredModId).map(modContainer -> {
                if (requiredModVersion.isEmpty()) {
                    return true;
                }

                var version = modContainer.getMetadata().getVersion();

                try {
                    var predicate = VersionPredicate.parse(requiredModVersion);
                    return predicate.test(version);
                } catch (VersionParsingException e) {
                    throw new RuntimeException(e);
                }
            }).orElse(false);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
