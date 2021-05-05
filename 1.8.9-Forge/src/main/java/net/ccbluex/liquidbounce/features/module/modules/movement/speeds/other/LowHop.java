package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other;

import me.kiras.aimwhere.modules.movement.TargetStrafe;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode;
import net.ccbluex.liquidbounce.utils.MovementUtils;

public class LowHop extends SpeedMode {
    int stage = 0;
    float speed = 0;
    double less = 0.0;
    boolean lessSlow = false;
    double stair = 0.0;
    public LowHop() {
        super("LowHop");
    }

    @Override
    public void onEnable() {
        lessSlow = false;
        stair = 0;
        less = 0;
        speed = 0;
        stage = 0;
    }

    @Override
    public void onMotion() {

    }

    @Override
    public void onUpdate() {
        if(!(mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown())){
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        }
    }

    @Override
    public void onMove(MoveEvent event) {
        TargetStrafe ts = LiquidBounce.moduleManager.getModule(TargetStrafe.class);
        if (!MovementUtils.isMoving() || mc.thePlayer.isInWater()) {
            lessSlow = false;
            stair = 0;
            less = 0;
            speed = 0;
            stage = 0;
            return;
        }
        if (mc.thePlayer.isCollidedHorizontally) {
            stage = -1;
        }
        if (stair > 0.0) {
            stair -= 0.25;
        }
        less -= ((less > 1.0) ? 0.12 : 0.11);
        if (less < 0.0) {
            less = 0.0;
        }
        if (!mc.thePlayer.isInWater() && MovementUtils.isOnGround(0.01)) {
            if (stage >= 0 || mc.thePlayer.isCollidedHorizontally) {
                stage = 0;
                double y = 0.4001999986886975 + MovementUtils.getJumpEffect() * 0.099;
                if (stair == 0.0) {
                    if (mc.gameSettings.keyBindJump.isKeyDown() || mc.thePlayer.isCollidedHorizontally){
                        mc.thePlayer.jump();
                        mc.thePlayer.motionY = y;
                    }
                    event.setY(y);
                }
                less++;
                lessSlow = (less > 1.0 && !lessSlow);
                if (less > 1.12) {
                    less = 1.12;
                }
            }
        }
        speed = (float) (getHypixelSpeed(stage) - MovementUtils.getSpeedEffect() * 0.03F);
        speed *= 0.9;
        if (stair > 0.0) {
            speed *= 0.7087 - MovementUtils.getSpeedEffect() * 0.1;
        }
        if (stage < 0) {
            speed = (float) MovementUtils.getBaseMoveSpeed();
        }
        if (lessSlow) {
            speed *= 0.94;
        }
        if(ts.canStrafe())
            ts.strafe(event,speed);
        else
            MovementUtils.setMotion(speed);
        stage++;
    }
    double getHypixelSpeed(int stage) {
        double value = MovementUtils.getBaseMoveSpeed() + 0.028 * MovementUtils.getSpeedEffect() + MovementUtils.getSpeedEffect() / 15.0;
        double firstvalue = 0.4145 + MovementUtils.getSpeedEffect() / 12.5;
        double decr = stage / 500.0 * 2.0;
        if (stage == 0) {
            value = 0.64 + (MovementUtils.getSpeedEffect() + 0.028 *MovementUtils. getSpeedEffect()) * 0.134;
        } else if (stage == 1) {
            value = firstvalue;
        } else if (stage >= 2) {
            value = firstvalue - decr;
        }
        if (mc.thePlayer.isCollidedHorizontally) {
            value = 0.2;
            if (stage == 0) {
                value = 0.0;
            }
        }
        return Math.max(value, MovementUtils.getBaseMoveSpeed() + 0.028 * MovementUtils.getSpeedEffect());
    }
}
