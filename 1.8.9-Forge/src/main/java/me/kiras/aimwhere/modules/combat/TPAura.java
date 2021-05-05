package me.kiras.aimwhere.modules.combat;

import me.kiras.aimwhere.utils.math.Vec4;
import me.kiras.aimwhere.utils.render.Colors;
import me.kiras.aimwhere.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot;
import net.ccbluex.liquidbounce.features.module.modules.misc.Teams;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.utils.EntityUtils;
import net.ccbluex.liquidbounce.utils.PathUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
@ModuleInfo(
        name = "TPAura",
        description = "Teleport to your target and attack it.",
        category = ModuleCategory.COMBAT
)
public class TPAura extends Module {
    private double dashDistance = 5;
    public static final FloatValue rangeValue = new FloatValue("Range", 30, 1, 100);
    public static final BoolValue playersValue = new BoolValue("Players", true);
    public static final BoolValue animalsValue = new BoolValue("Other", true);
    public static final BoolValue teamsValue = new BoolValue("Teams", true);
    public static final BoolValue invisiblesValue = new BoolValue("Invisibles", true);
    public static final BoolValue ESP = new BoolValue("ESP", true);
    public static final BoolValue pathESPValue = new BoolValue("PathESP", true);
    public static final IntegerValue cpsValue = new IntegerValue("CPS", 7, 1, 20);
    public static final IntegerValue maxTargetsValue = new IntegerValue("MaxTargets", 3, 1, 50);
    private ArrayList<Vec4> path = new ArrayList<>();
    private ArrayList[] test = new ArrayList[50];
    private List<EntityLivingBase> targets = new CopyOnWriteArrayList<>();
    private final TimerUtil cpsTimer = new TimerUtil();
    public static TimerUtil timer = new TimerUtil();
    int maxTargetsValueTargets = maxTargetsValue.get();

    @Override
    public void onEnable() {
        timer.reset();
        targets.clear();
    }

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        int delayValue = 20 / cpsValue.get() * 50;
        targets = getTargets();
        if (cpsTimer.hasReached(delayValue)) {
            if (targets.size() > 0) {
                test = new ArrayList[50];
                for (int i = 0; i < (Math.min(targets.size(), maxTargetsValueTargets)); i++) {
                    EntityLivingBase T = targets.get(i);
                    Vec4 topFrom = new Vec4(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    Vec4 to = new Vec4(T.posX, T.posY, T.posZ);
                    path = PathUtils.computePath(topFrom, to);
                    test[i] = path;
                    for (Vec4 pathElm : path) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                    }
                    mc.thePlayer.swingItem();
                    mc.playerController.attackEntity(mc.thePlayer, T);
                    Collections.reverse(path);
                    for (Vec4 pathElm : path) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                    }
                }
                cpsTimer.reset();
            }
        }
    }
    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if(packet instanceof S08PacketPlayerPosLook) {
            LiquidBounce.hud.addNotification("TPAura LagBack", Notification.Type.WARNING);
//            ClientUtils.sendClientMessage("TPAura LagBack", Notifications.Type.WARNING);
            this.toggle();
        }
    }

    public void drawESP(Entity entity, int color) {
        double x = entity.lastTickPosX
                + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;

        double y = entity.lastTickPosY
                + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;

        double z = entity.lastTickPosZ
                + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double width = Math.abs(entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX);
        double height = Math.abs(entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY);
        Vec4 vec = new Vec4(x - width / 2, y, z - width / 2);
        Vec4 vec2 = new Vec4(x + width / 2, y + height, z + width / 2);
        RenderUtils.pre3D();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderUtils.glColor(color);
        RenderUtils.drawBoundingBox(new AxisAlignedBB(
                vec.getX() - mc.getRenderManager().renderPosX, vec.getY() - mc.getRenderManager().renderPosY, vec.getZ() - mc.getRenderManager().renderPosZ,
                vec2.getX() - mc.getRenderManager().renderPosX, vec2.getY() - mc.getRenderManager().renderPosY, vec2.getZ() - mc.getRenderManager().renderPosZ));
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        RenderUtils.post3D();
    }

    public void drawPath(Vec4 vec) {
        double x = vec.getX() - mc.getRenderManager().renderPosX;
        double y = vec.getY() - mc.getRenderManager().renderPosY;
        double z = vec.getZ() - mc.getRenderManager().renderPosZ;
        double width = 0.3;
        double height = mc.thePlayer.getEyeHeight();
        RenderUtils.pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        int[] colors = {Colors.getColor(Color.black), Colors.getColor(Color.white)};
        for (int i = 0; i < 2; i++) {
            RenderUtils.glColor(colors[i]);
            GL11.glLineWidth(3 - i * 2);
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glVertex3d(x - width, y, z - width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y, z + width);
            GL11.glVertex3d(x + width, y, z + width);
            GL11.glVertex3d(x + width, y, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x + width, y + height, z + width);
            GL11.glVertex3d(x + width, y + height, z - width);
            GL11.glEnd();
            GL11.glBegin(GL11.GL_LINE_STRIP);
            GL11.glVertex3d(x - width, y + height, z + width);
            GL11.glVertex3d(x - width, y + height, z - width);
            GL11.glEnd();
        }

        RenderUtils.post3D();
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (!targets.isEmpty() && ESP.get()) {
            if (targets.size() > 0) {
                for (int i = 0; i < (Math.min(targets.size(), maxTargetsValueTargets)); i++) {
                    int color = targets.get(i).hurtResistantTime > 15 ? Colors.getColor(new Color(255, 70, 70, 140)) : new Color(0, 192, 255, 100).getRGB();
                    drawESP(targets.get(i), color);
                }

            }
        }
        if (!path.isEmpty() && pathESPValue.get()) {
            for (int i = 0; i < targets.size(); i++) {
                try {
                    if (test != null)
                        for (Object pos : test[i]) {
                            if (pos != null)
                                drawPath((Vec4) pos);
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (cpsTimer.hasReached(1000)) {
                test = new ArrayList[50];
                path.clear();
            }
        }
    }

    private boolean validEntity(EntityLivingBase entity) {
        float range = rangeValue.get();
        boolean players = playersValue.get();
        boolean animals = animalsValue.get();
        Teams Teams = LiquidBounce.moduleManager.getModule(Teams.class);

        if ((mc.thePlayer.isEntityAlive()) && !(entity instanceof EntityPlayerSP)) {
            if (mc.thePlayer.getDistanceToEntity(entity) <= range) {
                if (AntiBot.isBot(entity) && LiquidBounce.moduleManager.getModule(AntiBot.class).getState()) {
                    return false;
                }
                if (entity.isPlayerSleeping()) {
                    return false;
                }
                if (EntityUtils.isFriend(entity)) {
                    return false;
                }

                if (entity instanceof EntityPlayer) {
                    if (players) {

                        EntityPlayer player = (EntityPlayer) entity;
                        if (!player.isEntityAlive() && player.getHealth() == 0.0) {
                            return false;
                        } else if (Teams.isInYourTeam(player) && teamsValue.get()) {
                            return false;
                        } else if (player.isInvisible() && !invisiblesValue.get()) {
                            return false;
                        } else return !EntityUtils.isFriend(entity);
                    }
                } else {
                    if (!entity.isEntityAlive())
                        return false;
                }

                if (entity instanceof EntityMob && animals)
                    return true;
                if ((entity instanceof EntityAnimal || entity instanceof EntityVillager) && animals) {
                    return !entity.getName().equals("Villager");
                }
            }
        }

        return false;
    }

    private List<EntityLivingBase> getTargets() {
        List<EntityLivingBase> targets = new ArrayList<>();

        for (Object o : mc.theWorld.getLoadedEntityList()) {
            if (o instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) o;
                if (validEntity(entity)) {
                    targets.add(entity);
                }
            }
        }
        targets.sort((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) * 1000
                - o2.getDistanceToEntity(mc.thePlayer) * 1000));
        return targets;
    }
}
