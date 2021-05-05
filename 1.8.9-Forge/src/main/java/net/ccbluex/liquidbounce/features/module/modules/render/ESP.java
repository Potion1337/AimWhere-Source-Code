/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import me.kiras.aimwhere.utils.render.Colors;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.misc.Teams;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.EntityUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.WorldToScreen;
import net.ccbluex.liquidbounce.utils.render.shader.FramebufferShader;
import net.ccbluex.liquidbounce.utils.render.shader.shaders.GlowShader;
import net.ccbluex.liquidbounce.utils.render.shader.shaders.OutlineShader;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Timer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import java.awt.*;
import java.util.*;
import static net.ccbluex.liquidbounce.utils.render.WorldToScreen.getMatrix;
import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "ESP", description = "Allows you to see targets through walls.", category = ModuleCategory.RENDER)
public class ESP extends Module {

    public static boolean renderNameTags = true;
    private Map<EntityLivingBase, double[]> entityConvertedPointsMap = new HashMap<>();
    private final FontRenderer fr = mc.fontRendererObj;
    public final ListValue modeValue = new ListValue("Mode", new String[]{"CSGO","Box", "OtherBox", "WireFrame", "2D", "Real2D", "Outline", "ShaderOutline", "ShaderGlow","Corner"}, "Box");
    public final FloatValue outlineWidth = new FloatValue("Outline-Width", 3F, 0.5F, 5F);
    public final FloatValue wireframeWidth = new FloatValue("WireFrame-Width", 2F, 0.5F, 5F);
    private final FloatValue shaderOutlineRadius = new FloatValue("ShaderOutline-Radius", 1.35F, 1F, 2F);
    private final FloatValue shaderGlowRadius = new FloatValue("ShaderGlow-Radius", 2.3F, 2F, 3F);
    private final ColorValue colorValue = new ColorValue("Color", 0x6FD8FF);
    private final BoolValue colorRainbow = new BoolValue("Rainbow", false);
    private final BoolValue colorTeam = new BoolValue("Team", false);
    private final BoolValue healthValue = new BoolValue("Health" , true);

    private void doCornerESP(EntityLivingBase entity) {
        if (!entity.isInvisible()) {
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            float partialTicks = this.mc.timer.renderPartialTicks;
            this.mc.getRenderManager();
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks
                    - mc.getRenderManager().renderPosX;
            this.mc.getRenderManager();
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks
                    - mc.getRenderManager().renderPosY;
            this.mc.getRenderManager();
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks
                    - mc.getRenderManager().renderPosZ;
            float DISTANCE = this.mc.thePlayer.getDistanceToEntity(entity);
            float DISTANCE_SCALE = Math.min(DISTANCE * 0.15F, 2.5F);
            float SCALE = 0.035F;
            SCALE /= 2.0F;
            GlStateManager.translate((float) x,
                    (float) y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float) z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-SCALE, -SCALE, -SCALE);
            Tessellator tesselator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tesselator.getWorldRenderer();
            Color color =new Color(colorValue.get());
            if (entity.hurtTime > 0) {
                color = new Color(255, 0, 0);
            }
            float HEALTH = entity.getHealth();
            int COLOR = -1;
            if (HEALTH > 20.0) {
                COLOR = -65292;
            }
            else if (HEALTH >= 10.0) {
                COLOR = -16711936;
            }
            else if (HEALTH >= 3.0) {
                COLOR = -23296;
            }
            else {
                COLOR = -65536;
            }
            double thickness = (double) (1.0F);
            double xLeft = -30.0D;
            double xRight = 30.0D;
            double yUp = (entity.isSneaking()) ? 28 : 18.0D;
            double yDown = 140.0D;
            double size = 10.0D;
            double size2 = 40.0D;
            drawVerticalLine(xLeft + size / 2.0D, yUp-0.5D, size / 2.0D, thickness, color);
            this.drawHorizontalLine(xLeft, yUp + size2 - 5, size2-4, thickness, color);
            drawVerticalLine(xRight - size / 2.0D, yUp-0.5D, size / 2.0D, thickness, color);
            this.drawHorizontalLine(xRight, yUp + size2 - 5, size2-4, thickness, color);
            drawVerticalLine(xLeft + size / 2.0D, yDown+0.5D, size / 2.0D, thickness, color);
            this.drawHorizontalLine(xLeft, yDown - size2 + 5, size2-4, thickness, color);
            drawVerticalLine(xRight - size / 2.0D, yDown+0.5D, size / 2.0D, thickness, color);
            this.drawHorizontalLine(xRight, yDown - size2 + 5, size2-4, thickness, color);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glNormal3f(1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

    private void drawVerticalLine(double xPos, double yPos, double xSize, double thickness, Color color) {
        Tessellator tesselator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tesselator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(xPos - xSize, yPos - thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
                .endVertex();
        worldRenderer.pos(xPos - xSize, yPos + thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
                .endVertex();
        worldRenderer.pos(xPos + xSize, yPos + thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
                .endVertex();
        worldRenderer.pos(xPos + xSize, yPos - thickness / 2.0D, 0.0D).color((float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
                .endVertex();
        tesselator.draw();
    }

    private void drawHorizontalLine(double xPos, double yPos, double ySize, double thickness, Color color) {
        Tessellator tesselator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tesselator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(xPos - thickness / 2.0D, yPos - ySize, 0.0D).color((float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
                .endVertex();
        worldRenderer.pos(xPos - thickness / 2.0D, yPos + ySize, 0.0D).color((float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
                .endVertex();
        worldRenderer.pos(xPos + thickness / 2.0D, yPos + ySize, 0.0D).color((float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
                .endVertex();
        worldRenderer.pos(xPos + thickness / 2.0D, yPos - ySize, 0.0D).color((float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) color.getAlpha() / 255.0F)
                .endVertex();
        tesselator.draw();
    }

    public static double getIncremental(final double val, final double inc) {
        final double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }


    private void updatePositions() {
        entityConvertedPointsMap.clear();
        final float pTicks = mc.timer.renderPartialTicks;
        for (final Entity e2 : mc.theWorld.getLoadedEntityList()) {
            if (e2 instanceof EntityPlayer) {
                final EntityPlayer ent;
                if ((ent = (EntityPlayer) e2) == mc.thePlayer) {
                    continue;
                }
                double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks
                        - mc.getRenderManager().viewerPosX + 0.36;
                double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks
                        - mc.getRenderManager().viewerPosY;
                double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks
                        - mc.getRenderManager().viewerPosZ + 0.36;
                final double topY;
                y = (topY = y + (ent.height + 0.15));
                final double[] convertedPoints = RenderUtils.convertTo2D(x, y, z);
                final double[] convertedPoints2 = RenderUtils.convertTo2D(x - 0.36, y, z - 0.36);
                final double xd = 0.0;
                assert convertedPoints2 != null;
                if (convertedPoints2[2] < 0.0) {
                    continue;
                }
                if (convertedPoints2[2] >= 1.0) {
                    continue;
                }
                x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX
                        - 0.36;
                z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ
                        - 0.36;
                final double[] convertedPointsBottom = RenderUtils.convertTo2D(x, y, z);
                y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * pTicks - mc.getRenderManager().viewerPosY
                        - 0.05;
                final double[] convertedPointsx = RenderUtils.convertTo2D(x, y, z);
                x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX
                        - 0.36;
                z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ
                        + 0.36;
                final double[] convertedPointsTop1 = RenderUtils.convertTo2D(x, topY, z);
                final double[] convertedPointsx2 = RenderUtils.convertTo2D(x, y, z);
                x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX
                        + 0.36;
                z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ
                        + 0.36;
                final double[] convertedPointsz = RenderUtils.convertTo2D(x, y, z);
                x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * pTicks - mc.getRenderManager().viewerPosX
                        + 0.36;
                z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * pTicks - mc.getRenderManager().viewerPosZ
                        - 0.36;
                final double[] convertedPointsTop2 = RenderUtils.convertTo2D(x, topY, z);
                final double[] convertedPointsz2 = RenderUtils.convertTo2D(x, y, z);
                assert convertedPoints != null;
                assert convertedPointsx != null;
                assert convertedPointsTop1 != null;
                assert convertedPointsTop2 != null;
                assert convertedPointsz2 != null;
                assert convertedPointsz != null;
                assert convertedPointsx2 != null;
                assert convertedPointsBottom != null;
                entityConvertedPointsMap.put(ent,
                        new double[] { convertedPoints[0], convertedPoints[1], xd, convertedPoints[2],
                                convertedPointsBottom[0], convertedPointsBottom[1], convertedPointsBottom[2],
                                convertedPointsx[0], convertedPointsx[1], convertedPointsx[2], convertedPointsx2[0],
                                convertedPointsx2[1], convertedPointsx2[2], convertedPointsz[0], convertedPointsz[1],
                                convertedPointsz[2], convertedPointsz2[0], convertedPointsz2[1], convertedPointsz2[2],
                                convertedPointsTop1[0], convertedPointsTop1[1], convertedPointsTop1[2],
                                convertedPointsTop2[0], convertedPointsTop2[1], convertedPointsTop2[2] });
            }
        }
    }

    public static void rectangle(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            double var5 = top;
            top = bottom;
            bottom = var5;
        }
        float var11 = (color >> 24 & 0xFF) / 255.0F;
        float var6 = (color >> 16 & 0xFF) / 255.0F;
        float var7 = (color >> 8 & 0xFF) / 255.0F;
        float var8 = (color & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION);
        worldRenderer.pos(left, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, bottom, 0.0D).endVertex();
        worldRenderer.pos(right, top, 0.0D).endVertex();
        worldRenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void drawBorderedRect(float x, float y, float x2, float y2, float l1, int col1, int col2) {
        drawRect(x, y, x2, y2, col2);
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawRect(float g, float h, float i, float j, int col1) {
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(7);
        GL11.glVertex2d(i, h);
        GL11.glVertex2d(g, h);
        GL11.glVertex2d(g, j);
        GL11.glVertex2d(i, j);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    private boolean isValid(EntityLivingBase entity) {
        return EntityUtils.isSelected(entity, true);
    }



    @EventTarget
    public void onRender3D(Render3DEvent event) {
        final String mode = modeValue.get();
        if(mode.equalsIgnoreCase("CSGO")) {
            try {
                updatePositions();
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
        Matrix4f mvMatrix = getMatrix(GL11.GL_MODELVIEW_MATRIX);
        Matrix4f projectionMatrix = getMatrix(GL11.GL_PROJECTION_MATRIX);

        boolean real2d = mode.equalsIgnoreCase("real2d");

        //<editor-fold desc="Real2D-Setup">
        if (real2d) {
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0, mc.displayWidth, mc.displayHeight, 0, -1.0f, 1.0);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();

            glDisable(GL_DEPTH_TEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableTexture2D();
            GlStateManager.depthMask(true);

            GL11.glLineWidth(1.0f);
        }
        //</editor-fold>

        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != null && entity != mc.thePlayer && EntityUtils.isSelected(entity, false)) {
                final EntityLivingBase entityLiving = (EntityLivingBase) entity;

                Color color = getColor(entityLiving);

                switch (mode.toLowerCase()) {
                    case "corner":
                        this.doCornerESP(entityLiving);
                        break;
                    case "box":
                    case "otherbox":
                        RenderUtils.drawEntityBox(entity, color, !mode.equalsIgnoreCase("otherbox"));
                        break;
                    case "2d": {
                        final RenderManager renderManager = mc.getRenderManager();
                        final Timer timer = mc.timer;

                        final double posX = entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX;
                        final double posY = entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY;
                        final double posZ = entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ;

                        RenderUtils.draw2D(entityLiving, posX, posY, posZ, color.getRGB(), Color.BLACK.getRGB());
                        break;
                    }
                    case "real2d": {
                        final RenderManager renderManager = mc.getRenderManager();
                        final Timer timer = mc.timer;

                        AxisAlignedBB bb = entityLiving.getEntityBoundingBox()
                                .offset(-entityLiving.posX, -entityLiving.posY, -entityLiving.posZ)
                                .offset(entityLiving.lastTickPosX + (entityLiving.posX - entityLiving.lastTickPosX) * timer.renderPartialTicks,
                                        entityLiving.lastTickPosY + (entityLiving.posY - entityLiving.lastTickPosY) * timer.renderPartialTicks,
                                        entityLiving.lastTickPosZ + (entityLiving.posZ - entityLiving.lastTickPosZ) * timer.renderPartialTicks)
                                .offset(-renderManager.renderPosX, -renderManager.renderPosY, -renderManager.renderPosZ);

                        double[][] boxVertices = {
                                {bb.minX, bb.minY, bb.minZ},
                                {bb.minX, bb.maxY, bb.minZ},
                                {bb.maxX, bb.maxY, bb.minZ},
                                {bb.maxX, bb.minY, bb.minZ},
                                {bb.minX, bb.minY, bb.maxZ},
                                {bb.minX, bb.maxY, bb.maxZ},
                                {bb.maxX, bb.maxY, bb.maxZ},
                                {bb.maxX, bb.minY, bb.maxZ},
                        };

                        float minX = Float.MAX_VALUE;
                        float minY = Float.MAX_VALUE;

                        float maxX = -1;
                        float maxY = -1;

                        for (double[] boxVertex : boxVertices) {
                            Vector2f screenPos = WorldToScreen.worldToScreen(new Vector3f((float) boxVertex[0], (float) boxVertex[1], (float) boxVertex[2]), mvMatrix, projectionMatrix, mc.displayWidth, mc.displayHeight);

                            if (screenPos == null) {
                                continue;
                            }

                            minX = Math.min(screenPos.x, minX);
                            minY = Math.min(screenPos.y, minY);

                            maxX = Math.max(screenPos.x, maxX);
                            maxY = Math.max(screenPos.y, maxY);
                        }

                        if (minX > 0 || minY > 0 || maxX <= mc.displayWidth || maxY <= mc.displayWidth) {
                            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);

                            GL11.glBegin(GL11.GL_LINE_LOOP);

                            GL11.glVertex2f(minX, minY);
                            GL11.glVertex2f(minX, maxY);
                            GL11.glVertex2f(maxX, maxY);
                            GL11.glVertex2f(maxX, minY);

                            GL11.glEnd();
                        }

                        break;
                    }
                }
            }
        }

        if (real2d) {
            glEnable(GL_DEPTH_TEST);

            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();

            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();

            GL11.glPopAttrib();
        }
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        final String mode = modeValue.get().toLowerCase();
        if (mode.equalsIgnoreCase("CSGO")) {
            GlStateManager.pushMatrix();
            for (final Entity entity : entityConvertedPointsMap.keySet()) {
                final EntityPlayer ent = (EntityPlayer) entity;
                final double[] renderPositions = entityConvertedPointsMap.get(ent);
                final double[] renderPositionsBottom = {renderPositions[4], renderPositions[5], renderPositions[6]};
                final double[] renderPositionsX = {renderPositions[7], renderPositions[8], renderPositions[9]};
                final double[] renderPositionsX2 = {renderPositions[10], renderPositions[11], renderPositions[12]};
                final double[] renderPositionsZ = {renderPositions[13], renderPositions[14], renderPositions[15]};
                final double[] renderPositionsZ2 = {renderPositions[16], renderPositions[17], renderPositions[18]};
                final double[] renderPositionsTop1 = {renderPositions[19], renderPositions[20], renderPositions[21]};
                final double[] renderPositionsTop2 = {renderPositions[22], renderPositions[23], renderPositions[24]};
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5, 0.5, 0.5);
                if (EntityUtils.isSelected(entity, true)) {
                    try {
                        final double[] xValues = {renderPositions[0], renderPositionsBottom[0], renderPositionsX[0],
                                renderPositionsX2[0], renderPositionsZ[0], renderPositionsZ2[0], renderPositionsTop1[0],
                                renderPositionsTop2[0]};
                        final double[] yValues = {renderPositions[1], renderPositionsBottom[1], renderPositionsX[1],
                                renderPositionsX2[1], renderPositionsZ[1], renderPositionsZ2[1], renderPositionsTop1[1],
                                renderPositionsTop2[1]};
                        double x = renderPositions[0];
                        double y = renderPositions[1];
                        double endx = renderPositionsBottom[0];
                        double endy = renderPositionsBottom[1];
                        double[] array;
                        for (int length = (array = xValues).length, j = 0; j < length; ++j) {
                            final double bdubs = array[j];
                            if (bdubs < x) {
                                x = bdubs;
                            }
                        }
                        double[] array2;
                        for (int length2 = (array2 = xValues).length, k = 0; k < length2; ++k) {
                            final double bdubs = array2[k];
                            if (bdubs > endx) {
                                endx = bdubs;
                            }
                        }
                        double[] array3;
                        for (int length3 = (array3 = yValues).length, l = 0; l < length3; ++l) {
                            final double bdubs = array3[l];
                            if (bdubs < y) {
                                y = bdubs;
                            }
                        }
                        double[] array4;
                        for (int length4 = (array4 = yValues).length, n = 0; n < length4; ++n) {
                            final double bdubs = array4[n];
                            if (bdubs > endy) {
                                endy = bdubs;
                            }
                        }
                        RenderUtils.rectangleBordered(x + 0.5, y + 0.5, endx - 0.5, endy - 0.5, 1.0,
                                Colors.getColor(0, 0, 0, 0), new Color(255,255,255).getRGB());
                        RenderUtils.rectangleBordered(x - 0.5, y - 0.5, endx + 0.5, endy + 0.5, 1.0,
                                Colors.getColor(0, 0), Colors.getColor(0, 150));
                        RenderUtils.rectangleBordered(x + 1.5, y + 1.5, endx - 1.5, endy - 1.5, 1.0,
                                Colors.getColor(0, 0), Colors.getColor(0, 150));

                        if (healthValue.get()) {
                            float health = ent.getHealth();
                            float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
                            Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                            float progress = health / ent.getMaxHealth();
                            Color customColor = health >= 0.0f ? Colors.blendColors(fractions, colors, progress).brighter()
                                    : Color.RED;
                            double difference = y - endy + 0.5;
                            double healthLocation = endy + difference * progress;
                            RenderUtils.rectangleBordered((x - 6.5), (y - 0.5), (x - 2.5),
                                    endy, 1.0, Colors.getColor(0, 100),
                                    Colors.getColor(0, 150));
                            RenderUtils.rectangle((x - 5.5), (endy - 1.0), (x - 3.5),
                                    healthLocation, customColor.getRGB());
                            if (-difference > 50.0) {
                                for (int i = 1; i < 10; ++i) {
                                    double dThing = difference / 10.0 * i;
                                    RenderUtils.rectangle((x - 6.5), (endy - 0.5 + dThing),
                                            (x - 2.5), (endy - 0.5 + dThing - 1.0),
                                            Colors.getColor(0));
                                }
                            }
                            if ((int) getIncremental(progress * 100.0f, 1.0) <= 40) {
                                GlStateManager.pushMatrix();
                                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                                GlStateManager.popMatrix();
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
                GlStateManager.popMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        } else {
            final FramebufferShader shader = mode.equalsIgnoreCase("shaderoutline")
                    ? OutlineShader.OUTLINE_SHADER : mode.equalsIgnoreCase("shaderglow")
                    ? GlowShader.GLOW_SHADER : null;

            if (shader == null) return;

            shader.startDraw(event.getPartialTicks());

            renderNameTags = false;

            try {
                for (final Entity entity : mc.theWorld.loadedEntityList) {
                    if (!EntityUtils.isSelected(entity, false))
                        continue;

                    mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, true);
                }
            } catch (final Exception ex) {
                ClientUtils.getLogger().error("An error occurred while rendering all entities for shader esp", ex);
            }

            renderNameTags = true;

            final float radius = mode.equalsIgnoreCase("shaderoutline")
                    ? shaderOutlineRadius.get() : mode.equalsIgnoreCase("shaderglow")
                    ? shaderGlowRadius.get() : 1F;

            shader.stopDraw(getColor(null), radius, 1F);
        }
    }
    @Override
    public String getTag() {
        return modeValue.get();
    }

    public final Color getColor(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

            if (entityLivingBase.hurtTime > 0)
                return Color.RED;

            if (EntityUtils.isFriend(entityLivingBase))
                return Color.BLUE;

            if (colorTeam.get()) {
                final char[] chars = entityLivingBase.getDisplayName().getFormattedText().toCharArray();
                int color = Integer.MAX_VALUE;

                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] != '§' || i + 1 >= chars.length)
                        continue;

                    final int index = GameFontRenderer.getColorIndex(chars[i + 1]);

                    if (index < 0 || index > 15)
                        continue;

                    color = ColorUtils.hexColors[index];
                    break;
                }

                return new Color(color);
            }
        }

        return colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorValue.get());
    }
}
