package dev.vortres.lobby.instance;

import de.articdive.jnoise.generators.noisegen.opensimplex.FastSimplexNoiseGenerator;
import de.articdive.jnoise.generators.noisegen.perlin.PerlinNoiseGenerator;
import de.articdive.jnoise.modules.octavation.OctavationModule;
import de.articdive.jnoise.pipeline.JNoise;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.MathUtils;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public class VanillaInstance {

    public static final Instance INSTANCE;

    static {
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();

        DimensionType custom_dimension1 = DimensionType.builder(NamespaceID.from("vortres:custom_dimension1"))
                .ambientLight(1.0f)
                .build();

        MinecraftServer.getDimensionTypeManager().addDimension(custom_dimension1);

        InstanceContainer instance = instanceManager.createInstanceContainer(custom_dimension1, new AnvilLoader("worlds/world_normal"));

        // Noise used for the terrain with 3D noise
        JNoise noise = JNoise.newBuilder()
                .fastSimplex(FastSimplexNoiseGenerator.newBuilder().build())
                .scale(0.03289) //0.0025 (very smooth)
                .octavation(OctavationModule.newBuilder().setOctaves(4).setNoiseSource(PerlinNoiseGenerator.newBuilder().build()).build())
                .build();

        instance.setGenerator(unit -> {
            final Point start = unit.absoluteStart();
            for (int x = 0; x < unit.size().x(); x++) {
                for (int z = 0; z < unit.size().z(); z++) {
                    Point bottom = start.add(x, 0, z);

                    final double modifier = MathUtils.clamp((bottom.distance(Pos.ZERO.withY(bottom.y())) - 73) / 50, 0, 2);
                    double y = noise.evaluateNoise(bottom.x(), bottom.z()) * modifier;
                    y = (y > 0 ? y * 4 : y * 7) + 64;
                    unit.modifier().fill(bottom, bottom.add(1, 0, 1).withY(y), Block.STONE);
                }
            }
        });


        instance.enableAutoChunkLoad(true);
        instanceManager.registerInstance(instance);

        INSTANCE = instance;
    }
}
