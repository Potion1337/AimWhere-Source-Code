/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.JumpEvent;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.block.BlockPane;
import net.minecraft.util.BlockPos;

@ModuleInfo(
        name = "HighJump",
        description = "Allows you to jump higher.",
        category = ModuleCategory.MOVEMENT
)
public class HighJump extends Module {
    private final FloatValue heightValue = new FloatValue("Height", 2F, 1.1F, 5F);
    private final ListValue modeValue = new ListValue("Mode", new String[] {"Vanilla", "Damage", "AACv3", "DAC", "Mineplex","RedeSky"}, "Vanilla");
    private final BoolValue glassValue = new BoolValue("OnlyGlassPane", false);
    private final FloatValue airSpeed = new FloatValue("RedeSky-AirSpeed",0.1F,0.05F,0.25F);
    private final FloatValue asMin = new FloatValue("RedeSky-MinAirSpeed",0.04F,0.05F,0.25F);
    private final FloatValue asRe = new FloatValue("RedeSky-ReduceAirSpeed",0.02F,0.05F,0.25F);
    private final FloatValue ymotV = new FloatValue("RedeSky-YMotion",0.08F,0.05F,0.25F);
    private final FloatValue ymotM = new FloatValue("RedeSky-MinYMotion",0.04F,0.05F,0.25F);
    private final FloatValue ymotR = new FloatValue("RedeSky-ReduceYMotion",0.04F,0.05F,0.25F);
    private final BoolValue ymot = new BoolValue("RedeSky-YMotionReducer", false);
    private final BoolValue asr = new BoolValue("RedeSky-AirSpeedReducer", true);
    private int airTicks = 0;

    @Override
    public void onDisable() {
        airTicks = 0;
        mc.thePlayer.speedInAir = 0.02F;
    }

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        switch(modeValue.get().toLowerCase()) {
            case "redesky":
                if (asr.get()) {
                    float as = airSpeed.get() - (airTicks * (asRe.get() / 100));
                    mc.thePlayer.speedInAir = as < asMin.get() ? asMin.get() : as;
                } else {
                    mc.thePlayer.speedInAir = airSpeed.get();
                }
                if (mc.thePlayer.onGround) {
                    airTicks = 0;
                    mc.thePlayer.jump();
                } else {
                    if (ymot.get()) {
                        float motY = ymotV.get() - (airTicks * (ymotR.get() / 100));
                        airTicks++;
                        mc.thePlayer.motionY += motY < ymotM.get() ? ymotM.get() : motY;
                    } else {
                        mc.thePlayer.motionY += 0.08;
                    }
                }
                break;
            case "damage":
                if(mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround)
                    mc.thePlayer.motionY += 0.42F * heightValue.get();
                break;
            case "aacv3":
                if(!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.059D;
                break;
            case "dac":
                if(!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.049999;
                break;
            case "mineplex":
                if(!mc.thePlayer.onGround) MovementUtils.strafe(0.35F);
                break;
        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        if(glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        if(!mc.thePlayer.onGround) {
            if ("mineplex".equals(modeValue.get().toLowerCase())) {
                mc.thePlayer.motionY += mc.thePlayer.fallDistance == 0 ? 0.0499D : 0.05D;
            }
        }
    }

    @EventTarget
    public void onJump(final JumpEvent event) {
        if(glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        switch(modeValue.get().toLowerCase()) {
            case "vanilla":
                event.setMotion(event.getMotion() * heightValue.get());
                break;
            case "mineplex":
                event.setMotion(0.47F);
                break;
        }
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}
