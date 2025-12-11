package dev.happyaccidents.modules;

import dev.happyaccidents.HappyAccidents;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Set;

public class EntityTracers extends Module {
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


    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Non-item entities to render tracers to.")
        .defaultValue(
            EntityType.PLAYER
        )
        .build()
    );


    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Dropped items to render tracers to.")
        .defaultValue(
            Items.TURTLE_HELMET,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.GOLDEN_APPLE,
            Items.BUNDLE,
            Items.WHITE_BUNDLE,
            Items.ORANGE_BUNDLE,
            Items.MAGENTA_BUNDLE,
            Items.LIGHT_BLUE_BUNDLE,
            Items.YELLOW_BUNDLE,
            Items.LIME_BUNDLE,
            Items.PINK_BUNDLE,
            Items.GRAY_BUNDLE,
            Items.LIGHT_GRAY_BUNDLE,
            Items.CYAN_BUNDLE,
            Items.PURPLE_BUNDLE,
            Items.BLUE_BUNDLE,
            Items.BROWN_BUNDLE,
            Items.GREEN_BUNDLE,
            Items.RED_BUNDLE,
            Items.BLACK_BUNDLE,
            Items.FISHING_ROD,
            Items.MACE,
            Items.BREEZE_ROD,
            Items.WIND_CHARGE,
            Items.END_CRYSTAL,
            Items.CHORUS_FRUIT,
            Items.SHULKER_SHELL,
            Items.NETHERITE_AXE,
            Items.NETHERITE_HOE,
            Items.NETHERITE_SWORD,
            Items.NETHERITE_BOOTS,
            Items.NETHERITE_SHOVEL,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_BLOCK,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_PICKAXE,
            Items.ELYTRA,
            Items.ENDER_CHEST,
            Items.ENDER_PEARL,
            Items.FIREWORK_ROCKET,
            Items.EXPERIENCE_BOTTLE,
            Items.HEAVY_CORE,
            Items.SHULKER_BOX,
            Items.WHITE_SHULKER_BOX,
            Items.ORANGE_SHULKER_BOX,
            Items.MAGENTA_SHULKER_BOX,
            Items.LIGHT_BLUE_SHULKER_BOX,
            Items.YELLOW_SHULKER_BOX,
            Items.LIME_SHULKER_BOX,
            Items.PINK_SHULKER_BOX,
            Items.GRAY_SHULKER_BOX,
            Items.LIGHT_GRAY_SHULKER_BOX,
            Items.CYAN_SHULKER_BOX,
            Items.PURPLE_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX,
            Items.BROWN_SHULKER_BOX,
            Items.GREEN_SHULKER_BOX,
            Items.RED_SHULKER_BOX,
            Items.BLACK_SHULKER_BOX,
            Items.CONDUIT,
            Items.TNT
        )
        .build()
    );

    private final Setting<SettingColor> tracerColor = sgColors.add(new ColorSetting.Builder()
        .name("tracer-color")
        .description("Color of entity tracers.")
        .defaultValue(new SettingColor(255, 0, 0, 255))
        .build()
    );

    public EntityTracers() {
        super(HappyAccidents.CATEGORY, "entity-tracers", "Draws tracers to selected entities and items.");
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (mc.world == null) return;

        ClientPlayerEntity player = mc.player;
        if (player == null) return;

        Set<EntityType<?>> selectedEntities = entities.get();
        List<Item> selectedItems = items.get();


        if (selectedEntities.isEmpty() && selectedItems.isEmpty()) return;

        double maxDistSq = maxDistance.get() * maxDistance.get();
        Box searchBox = player.getBoundingBox().expand(maxDistance.get());


        Vec3d start = getTracerStart();


        for (Entity e : mc.world.getOtherEntities(player, searchBox)) {
            if (!e.isAlive()) continue;


            if (e instanceof ItemEntity itemEntity) {
                if (selectedItems.isEmpty()) continue;

                ItemStack stack = itemEntity.getStack();
                if (stack == null || stack.isEmpty()) continue;

                if (!selectedItems.contains(stack.getItem())) continue;
            } else {

                if (!selectedEntities.contains(e.getType())) continue;
            }

            double distSq = e.squaredDistanceTo(player);
            if (distSq > maxDistSq) continue;

            Vec3d target = e.getBoundingBox().getCenter();
            renderTracer(event, start, target, tracerColor.get());
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
