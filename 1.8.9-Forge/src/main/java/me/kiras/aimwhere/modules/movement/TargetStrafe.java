package me.kiras.aimwhere.modules.movement;
import java.awt.Color;
import java.util.Objects;
import me.kiras.aimwhere.utils.entity.EntityValidator;
import me.kiras.aimwhere.utils.entity.impl.VoidCheck;
import me.kiras.aimwhere.utils.entity.impl.WallCheck;
import me.kiras.aimwhere.utils.render.GLUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
@ModuleInfo(
        name = "TargetStrafe",
        description = "Strafe around your target.",
        category = ModuleCategory.MOVEMENT
)
public final class TargetStrafe
        extends Module {
    private final FloatValue radius = new FloatValue("Radius", 2.0F, 0.1F, 4.0F);
    private final BoolValue render = new BoolValue("Render", true);
    private final BoolValue space = new BoolValue("HoldSpace", false);
    private final EntityValidator targetValidator;
    private KillAura aura;
    private int direction = -1;
    public TargetStrafe() {
        this.targetValidator = new EntityValidator();
        this.targetValidator.add(new VoidCheck());
        this.targetValidator.add(new WallCheck());
    }

    @Override
    public void onEnable() {
        if(aura == null)
            aura = LiquidBounce.moduleManager.getModule(KillAura.class);
    }

    @EventTarget
    public final void onMotion(MotionEvent event) {
        if (event.getEventState() == EventState.PRE) {
            if (mc.thePlayer.isCollidedHorizontally) {
                this.switchDirection();
            }
            if (mc.gameSettings.keyBindLeft.isKeyDown()) {
                this.direction = 1;
            }
            if (mc.gameSettings.keyBindRight.isKeyDown()) {
                this.direction = -1;
            }
        }
    }

    private void switchDirection() {
        this.direction = this.direction == 1 ? -1 : 1;
    }
    @EventTarget
    public void strafe(MoveEvent event) {
        EntityLivingBase target = this.aura.getTarget();
        if(target != null)
            MovementUtils.setSpeed(event, MovementUtils.getSpeed(), RotationUtils.getRotationsEntity(target).getYaw(), this.direction, mc.thePlayer.getDistanceToEntity(target) <= this.radius.get() ? 0.0 : 1.0);
    }
    public void strafe(MoveEvent event, double speed) {
        EntityLivingBase target = this.aura.getTarget();
        if(target != null)
            MovementUtils.setSpeed(event, speed, RotationUtils.getRotationsEntity(target).getYaw(), this.direction, mc.thePlayer.getDistanceToEntity(target) <= this.radius.get() ? 0.0 : 1.0);
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        if (this.canStrafe() && this.render.get()) {
            this.drawCircle(Objects.requireNonNull(this.aura.getTarget()), event.getPartialTicks(), this.radius.get());
        }
    }

    private void drawCircle(EntityLivingBase entity, float partialTicks, double rad) { GL11.glPushMatrix();
        GL11.glDisable(3553);
        GLUtils.startSmooth();
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(1.0F);
        GL11.glBegin(3);
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.getRenderManager().viewerPosZ;
        for (int i = 0; i <= 360; i++) {
            Color rainbow = new Color(Color.HSBtoRGB((float)(mc.thePlayer.ticksExisted / 70.0D + Math.sin(i / 50.0D * 1.75D)) % 1.0F, 0.7F, 1.0F));
            GL11.glColor3f(rainbow.getRed() / 255.0F, rainbow.getGreen() / 255.0F, rainbow.getBlue() / 255.0F);
            GL11.glVertex3d(x + rad * Math.cos(i * 6.283185307179586D / 45.0D), y, z + rad * Math.sin(i * 6.283185307179586D / 45.0D));
        }
        GL11.glEnd();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GLUtils.endSmooth();
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }

    public boolean canStrafe() {
        if(aura == null)
            aura = LiquidBounce.moduleManager.getModule(KillAura.class);
        return this.aura.getState() && this.aura.getTarget() != null && this.getState() && this.targetValidator.validate(this.aura.getTarget()) && (!this.space.get() || mc.thePlayer.movementInput.jump);
    }
}

