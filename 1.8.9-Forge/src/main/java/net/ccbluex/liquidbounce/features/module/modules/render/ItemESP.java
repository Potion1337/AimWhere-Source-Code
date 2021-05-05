/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;
import me.kiras.aimwhere.utils.render.Colors;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static me.kiras.aimwhere.utils.render.Colors.blendColors;

@ModuleInfo(
        name = "ItemESP",
        description = "Allows you to see items through walls.",
        category = ModuleCategory.RENDER
)
public class ItemESP extends Module {
    private Map<EntityItem, double[]> entityConvertedPointsMap = new HashMap<>();
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Normal","Exhibition"}, "Normal");
    private final FontRenderer fr = mc.fontRendererObj;
    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if(modeValue.get().equalsIgnoreCase("Exhibition"))
        {
            GlStateManager.pushMatrix();
            for (final EntityItem ent : entityConvertedPointsMap.keySet()) {
                final double[] renderPositions = entityConvertedPointsMap.get(ent);
                final double[] renderPositionsBottom = { renderPositions[4], renderPositions[5], renderPositions[6] };
                final double[] renderPositionsX = { renderPositions[7], renderPositions[8], renderPositions[9] };
                final double[] renderPositionsX2 = { renderPositions[10], renderPositions[11], renderPositions[12] };
                final double[] renderPositionsZ = { renderPositions[13], renderPositions[14], renderPositions[15] };
                final double[] renderPositionsZ2 = { renderPositions[16], renderPositions[17], renderPositions[18] };
                final double[] renderPositionsTop1 = { renderPositions[19], renderPositions[20], renderPositions[21] };
                final double[] renderPositionsTop2 = { renderPositions[22], renderPositions[23], renderPositions[24] };
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5, 0.5, 0.5);
                if (mc.theWorld.loadedEntityList.contains(ent)) {
                    try {
                        final double[] xValues = { renderPositions[0], renderPositionsBottom[0], renderPositionsX[0],
                                renderPositionsX2[0], renderPositionsZ[0], renderPositionsZ2[0], renderPositionsTop1[0],
                                renderPositionsTop2[0] };
                        final double[] yValues = { renderPositions[1], renderPositionsBottom[1], renderPositionsX[1],
                                renderPositionsX2[1], renderPositionsZ[1], renderPositionsZ2[1], renderPositionsTop1[1],
                                renderPositionsTop2[1] };
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
                        RenderUtils.rectangleBordered(x + 0.5, y + 0.5, endx - 0.5, endy - 0.5, 1.0, Colors.getColor(0, 0, 0, 0), new Color(255,255,255).getRGB());
                        RenderUtils.rectangleBordered(x - 0.5, y - 0.5, endx + 0.5, endy + 0.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0, 150));
                        RenderUtils.rectangleBordered(x + 1.5, y + 1.5, endx - 1.5, endy - 1.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0, 150));
                        float health = 20F;
                        float[] fractions = new float[] { 0.0f, 0.5f, 1.0f };
                        Color[] colors = new Color[] { Color.RED, Color.YELLOW, Color.GREEN };
                        float progress = health / 20F;
                            Color customColor = health >= 0.0f ? blendColors(fractions, colors, progress).brighter()
                                    : Color.RED;
                            double difference = y - endy + 0.5;
                            double healthLocation = endy + difference * progress;
                            RenderUtils.rectangleBordered((x - 6.5), (y - 0.5), (x - 2.5),
                                    endy, 1.0,  new Color(30,255,30).getRGB(),
                                    Colors.getColor( 0,  150));
                            //RenderUtils.rectangle((x - 5.5), (endy - 1.0), (x - 3.5),
                           //         healthLocation,  customColor.getRGB());
                            RenderUtils.rectangle(x-5.5,endy-1.0,x-3.5,endy+difference, new Color(30,255,30).getRGB());
                            if (-difference > 50.0) {
                                for (int i = 1; i < 10; ++i) {
                                    double dThing = difference / 10.0 * i;
                                    RenderUtils.rectangle((x - 6.5), (endy - 0.5 + dThing),
                                            (x - 2.5), (endy - 0.5 + dThing - 1.0),
                                            Colors.getColor( 0));
                                }
                            }
                            if ((int) getIncremental(progress * 100.0f, 1.0) <= 40) {
                                GlStateManager.pushMatrix();
                                GlStateManager.scale( 2.0f,  2.0f,  2.0f);
                                GlStateManager.popMatrix();
                            }
                    } catch (Exception ignored) {
                    }
                }
                GlStateManager.popMatrix();
                GL11.glColor4f( 1.0f,  1.0f,  1.0f,  1.0f);
            }
            GL11.glScalef( 1.0f,  1.0f,  1.0f);
            GL11.glColor4f( 1.0f,  1.0f,  1.0f,  1.0f);
            GlStateManager.popMatrix();
            RenderUtils.rectangle(0.0, 0.0, 0.0, 0.0, -1);
        }
    }
    public static double getIncremental(final double val, final double inc) {
        final double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }
    @Override
    public String getTag() {
        return modeValue.get();
    }
    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (modeValue.get().equalsIgnoreCase("Normal")) {
            for (Entity o : mc.theWorld.loadedEntityList) {
                if (!(o instanceof EntityItem)) continue;
                EntityItem item = (EntityItem) o;
                double x = item.posX - mc.getRenderManager().renderPosX;
                double y = item.posY + 0.5 - mc.getRenderManager().renderPosY;
                double z = item.posZ - mc.getRenderManager().renderPosZ;
                GL11.glEnable(3042);
                GL11.glLineWidth(2.0F);
                GL11.glColor4f(1, 1, 1, .75F);
                GL11.glDisable(3553);
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                RenderUtils.drawOutlinedBoundingBox(new AxisAlignedBB(x - .2D, y - 0.3d, z - .2D, x + .2D, y - 0.4d, z + .2D));
                GL11.glColor4f(1, 1, 1, 0.15f);
                RenderUtils.drawBoundingBox(new AxisAlignedBB(x - .2D, y - 0.3d, z - .2D, x + .2D, y - 0.4d, z + .2D));
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                GL11.glDisable(3042);
            }
        }
        if(modeValue.get().equalsIgnoreCase("Exhibition")) {
            entityConvertedPointsMap.clear();
            final float pTicks = mc.timer.renderPartialTicks;
            for (final Entity e2 : mc.theWorld.getLoadedEntityList()) {
                if (e2 instanceof EntityItem) {
                    final EntityItem ent = (EntityItem) e2;
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
    }
    private double[] convertTo2D(double x, double y, double z, Entity ent) {
        return convertTo2D(x, y, z);
    }


    private double[] convertTo2D(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
        FloatBuffer projection = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        boolean result = GLU.gluProject((float) x, (float) y, (float) z, modelView, projection, viewport, screenCoords);
        if (result) {
            return new double[]{screenCoords.get(0), Display.getHeight() - screenCoords.get(1), screenCoords.get(2)};
        }
        return null;
    }
}
