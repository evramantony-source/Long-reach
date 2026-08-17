package com.wafis.longreach;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server-side Long Reach mod.
 *
 * The server changes the player's vanilla interaction-range attributes, so
 * the increased reach is enforced by the server rather than being a
 * client-side-only visual/raycast change. Install this mod on the Fabric
 * server (and on clients when the server requires it).
 */
public final class LongReach implements ModInitializer {
    public static final String MOD_ID = "long-reach";
    public static final double REACH = 8.0D;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                applyReach(handler.getPlayer()));

        // A respawn creates a new ServerPlayer instance, so apply the
        // attributes again after death/respawn.
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                applyReach(newPlayer));

        LOGGER.info("Long Reach loaded in server-compatible mode: {} blocks block/entity interaction range.", REACH);
    }

    private static void applyReach(net.minecraft.server.level.ServerPlayer player) {
        setBase(player, Attributes.BLOCK_INTERACTION_RANGE, REACH);
        setBase(player, Attributes.ENTITY_INTERACTION_RANGE, REACH);
    }

    private static void setBase(
            net.minecraft.server.level.ServerPlayer player,
            net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
            double value) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
}
