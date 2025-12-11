package dev.happyaccidents.modules;

import dev.happyaccidents.HappyAccidents;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class BlockTracers extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgColors = settings.createGroup("Colors");

    private final Setting<Double> maxDistance = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-distance")
        .description("Maximum distance to render tracers.")
        .defaultValue(256.0)
        .min(1.0)
        .max(512.0)
        .sliderMin(16.0)
        .sliderMax(512.0)
        .build()
    );


    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("Blocks to render tracers to.")
        .defaultValue(
            Blocks.CHEST,
            Blocks.TRAPPED_CHEST,
            Blocks.ENDER_CHEST,
            Blocks.BARREL,
            Blocks.SHULKER_BOX,
            Blocks.RED_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX,
            Blocks.BROWN_SHULKER_BOX,
            Blocks.GREEN_SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX,
            Blocks.WHITE_SHULKER_BOX,
            Blocks.ORANGE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX,
            Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.LIGHT_GRAY_SHULKER_BOX,
            Blocks.PINK_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX,
            Blocks.MAGENTA_SHULKER_BOX,
            Blocks.CYAN_SHULKER_BOX,
            Blocks.HOPPER,
            Blocks.CRAFTER
        ).build()
    );

    private final Setting<SettingColor> tracerColor = sgColors.add(new ColorSetting.Builder()
        .name("tracer-color")
        .description("Color of block tracers.")
        .defaultValue(new SettingColor(173, 216, 230, 255))
        .build()
    );

    public BlockTracers() {
        super(HappyAccidents.CATEGORY, "block-tracers", "Draws tracers to selected blocks.");
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (mc.world == null) return;

        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        List<Block> selectedBlocks = blocks.get();
        if (selectedBlocks.isEmpty()) return;

        double maxDistSq = maxDistance.get() * maxDistance.get();


        Vec3d start = getTracerStart();

        for (BlockEntity be : Utils.blockEntities()) {
            BlockPos pos = be.getPos();
            Vec3d center = Vec3d.ofCenter(pos);

            double distSq = player.squaredDistanceTo(center.x, center.y, center.z);
            if (distSq > maxDistSq) continue;

            Block block = mc.world.getBlockState(pos).getBlock();
            if (!selectedBlocks.contains(block)) continue;

            renderTracer(event, start, center, tracerColor.get());
        }
    }



    private Vec3d getTracerStart() {
        Vec3d camPos = mc.gameRenderer.getCamera().getPos();
        float pitch = mc.gameRenderer.getCamera().getPitch();
        float yaw = mc.gameRenderer.getCamera().getYaw();


        double dist = 75.0;

        Vec3d offset = new Vec3d(0.0, 0.0, dist)
            .rotateX((float) -Math.toRadians(pitch))
            .rotateY((float) -Math.toRadians(yaw));

        return camPos.add(offset);
    }


    private void renderTracer(Render3DEvent event, Vec3d start, Vec3d target, SettingColor color) {
        Color c = new Color(color);
        event.renderer.line(
            start.x, start.y, start.z,
            target.x, target.y, target.z,
            c
        );
    }
}
