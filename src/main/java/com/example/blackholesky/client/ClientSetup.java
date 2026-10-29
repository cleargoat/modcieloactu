package com.example.blackholesky.client;

import com.example.blackholesky.BlackHoleSky;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = BlackHoleSky.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static ShaderInstance groundShader;

    @SubscribeEvent
    public static void onRegisterEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(new ResourceLocation(BlackHoleSky.MODID, "event_horizon"), new EventHorizonEffects());
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
                new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(BlackHoleSky.MODID, "bh_ground"),
                        DefaultVertexFormat.POSITION),
                shader -> groundShader = shader);
    }
}
