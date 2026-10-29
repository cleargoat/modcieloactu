package com.example.blackholesky.client;

import com.example.blackholesky.BlackHoleSky;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlackHoleSky.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static final ResourceLocation CUSTOM_DIM = new ResourceLocation(BlackHoleSky.MODID, "event_horizon");

    /** /shader cielo on  |  /shader cielo off */
    @SubscribeEvent
    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("shader")
                .then(Commands.literal("cielo")
                        .then(Commands.literal("on").executes(ctx -> set(true)))
                        .then(Commands.literal("off").executes(ctx -> set(false)))));
    }

    private static int set(boolean on) {
        SkyToggle.setEnabled(on);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal(
                    on ? "Shader de cielo: ACTIVADO" : "Shader de cielo: DESACTIVADO"));
        }
        return 1;
    }

    @SubscribeEvent
    public static void onRenderStage(RenderLevelStageEvent event) {
        if (!SkyToggle.isEnabled()) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        boolean overworld = level.dimension() == Level.OVERWORLD;
        boolean custom = level.dimension().location().equals(CUSTOM_DIM);
        if (!overworld && !custom) return;

        RenderLevelStageEvent.Stage stage = event.getStage();
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY && overworld) {
            // En el Overworld dibujamos nuestro cielo encima del cielo vanilla
            EventHorizonEffects.draw(level, event.getRenderTick(), event.getPartialTick(), event.getPoseStack());
        } else if (stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            // Luz del agujero negro sobre el terreno (ya dibujado)
            GroundLight.render(level, event.getPoseStack(), event.getProjectionMatrix(),
                    event.getPartialTick(), event.getCamera().getBlockPosition());
        }
    }
}
