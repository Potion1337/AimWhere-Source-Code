package me.kiras.aimwhere.modules.world;
import me.kiras.aimwhere.utils.render.Notifications;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventState;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot;
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.block.Block;
import net.minecraft.util.*;
import net.minecraft.world.World;
import java.util.ArrayList;

@ModuleInfo(
        name = "HackerDetect",
        description = "Auto Find the hacker and call you.",
        category = ModuleCategory.WORLD
)
public class HackerDetect extends Module {
    private static final ArrayList<EntityPlayer> hackers = new ArrayList<>();
    @EventTarget
    public final void onMotion(MotionEvent event) {
        if(event.getEventState() != EventState.PRE)
            return;
        if (mc.thePlayer.ticksExisted <= 105) {
            hackers.clear();
            return;
        }
        for (final EntityPlayer player : mc.theWorld.playerEntities) {
            if (player != mc.thePlayer && player.ticksExisted >= 105
                    && !hackers.contains(player) && !AntiBot.isBot(player)
                    && !player.capabilities.isFlying) {
                if (player.capabilities.isCreativeMode) {
                    continue;
                }
                final double playerSpeed = getBPS(player);
                if ((player.isUsingItem() || player.isBlocking()) && player.onGround && playerSpeed >= 6.5) {
                    LiquidBounce.hud.addNotification(player.getName() + " might be Hacker (NoSlowDown)", Notification.Type.INFO);
                    hackers.add(player);
                }
                if (player.isSprinting()
                        && (player.moveForward < 0.0f || (player.moveForward == 0.0f && player.moveStrafing != 0.0f))) {
                    LiquidBounce.hud.addNotification(player.getName() + " might be Hacker (Speed)", Notification.Type.INFO);
                    hackers.add(player);
                }
                if (!mc.theWorld
                        .getCollidingBoundingBoxes(player,
                                mc.thePlayer.getEntityBoundingBox().offset(0.0, player.motionY, 0.0))
                        .isEmpty() && player.motionY > 0.0 && playerSpeed > 10.0) {
                    LiquidBounce.hud.addNotification(player.getName() + " might be Hacker (Flight/LongJump)", Notification.Type.INFO);
                    hackers.add(player);
                }
                final double y = Math.abs((int) player.posY);
                final double lastY = Math.abs((int) player.lastTickPosY);
                final double yDiff = (y > lastY) ? (y - lastY) : (lastY - y);
                if (yDiff > 0.0 && mc.thePlayer.onGround && player.motionY == -0.0784000015258789) {
                    LiquidBounce.hud.addNotification(player.getName() + " might be Hacker (Step)", Notification.Type.INFO);
                    hackers.add(player);
                }
                if (player.hurtTime >= 5 && player.hurtTime <= 8 && mc.thePlayer.onGround
                        && player.motionY == -0.0784000015258789 && player.motionX == 0.0 && player.motionZ == 0.0) {
                    LiquidBounce.hud.addNotification(player.getName() + " might be Hacker (Velocity)", Notification.Type.INFO);
                    hackers.add(player);
                }
                if (player.fallDistance != 0.0f || player.motionY >= -0.08 || InsideBlock(player) || player.onGround) {
                    continue;
                }
                LiquidBounce.hud.addNotification(player.getName() + " might be Hacker (NoFall)", Notification.Type.INFO);
                hackers.add(player);
            }
        }
    }

    public static int getBPS(final EntityLivingBase entityIn) {
        final double bps = getLastDist(entityIn) * 10.0;
        return (int)bps;
    }

    public static double getLastDist(final EntityLivingBase entIn) {
        final double xDist = entIn.posX - entIn.prevPosX;
        final double zDist = entIn.posZ - entIn.prevPosZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    public static boolean InsideBlock(final EntityPlayer player) {
        for (int x = MathHelper.floor_double(player.getEntityBoundingBox().minX); x < MathHelper
                .floor_double(player.getEntityBoundingBox().maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(player.getEntityBoundingBox().minY); y < MathHelper
                    .floor_double(player.getEntityBoundingBox().maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(player.getEntityBoundingBox().minZ); z < MathHelper
                        .floor_double(player.getEntityBoundingBox().maxZ) + 1; ++z) {
                    final Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block != null && !(block instanceof BlockAir)) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox((World) mc.theWorld,
                                new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if (block instanceof BlockHopper) {
                            boundingBox = new AxisAlignedBB(x,y,z,x + 1,y + 1,z + 1);
                        }
                        if (boundingBox != null && player.getEntityBoundingBox().intersectsWith(boundingBox)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}