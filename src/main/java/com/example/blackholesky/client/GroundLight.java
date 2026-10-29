package com.example.blackholesky.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix4f;
import org.joml.Vector4f;

/** Ilumina el suelo y las paredes con la luz naranja del agujero negro (de noche). */
public final class GroundLight {
    private static float skyFactor = 1f;

    private GroundLight() {
    }

    public static void render(ClientLevel level, PoseStack poseStack, Matrix4f proj, float partialTick, BlockPos camPos) {
        ShaderInstance sh = ClientSetup.groundShader;
        if (sh == null) return;

        float night = 1f - EventHorizonEffects.dayFactor(level, partialTick);
        // Si estas bajo techo o en una cueva, la luz del agujero negro no te llega
        float target = level.getBrightness(LightLayer.SKY, camPos) / 15f;
        skyFactor += (target - skyFactor) * 0.05f;
        float strength = 0.65f * night * skyFactor;
        if (strength <= 0.01f) return;

        RenderTarget rt = Minecraft.getInstance().getMainRenderTarget();

        // Direccion del agujero negro en espacio de camara
        Vector4f l = new Vector4f(EventHorizonEffects.BH.x, EventHorizonEffects.BH.y, EventHorizonEffects.BH.z, 0f);
        poseStack.last().pose().transform(l);

        sh.setSampler("DepthSampler", rt.getDepthTextureId());
        sh.safeGetUniform("InvProj").set(new Matrix4f(proj).invert());
        sh.safeGetUniform("Proj").set(proj);
        sh.safeGetUniform("LightDir").set(l.x, l.y, l.z);
        sh.safeGetUniform("LightColor").set(1.0f, 0.5f, 0.15f, strength);

        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(() -> sh);

        BufferBuilder b = Tesselator.getInstance().getBuilder();
        b.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        b.vertex(-1, -1, 0).endVertex();
        b.vertex(1, -1, 0).endVertex();
        b.vertex(1, 1, 0).endVertex();
        b.vertex(-1, 1, 0).endVertex();
        BufferUploader.drawWithShader(b.end());

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
    }
}
