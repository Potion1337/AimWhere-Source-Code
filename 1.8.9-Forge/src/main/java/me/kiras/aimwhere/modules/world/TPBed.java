package me.kiras.aimwhere.modules.world;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import me.kiras.aimwhere.utils.math.AStarCustomPathFinder;
import me.kiras.aimwhere.utils.math.Vec4;
import me.kiras.aimwhere.utils.other.BlockUtil;
import me.kiras.aimwhere.utils.other.PlayerUtil;
import me.kiras.aimwhere.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.PathUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
@ModuleInfo(
        name = "TPBed",
        description = "Teleport to the bed.",
        category = ModuleCategory.WORLD
)
public class TPBed extends Module {
    public BlockPos playerBed;
    public BlockPos fuckingBed;
    private List<BlockPos> posList;
    private final TimerUtil timer = new TimerUtil();
    public IntegerValue delay = new IntegerValue("Delay", 500, 200, 3000);
    private List<Vec4> path = new ArrayList<>();

    @Override
    public void onEnable() {
        try {
            this.posList = new ArrayList<>(BlockUtil.list);
            this.posList.sort((o1, o2) -> {
                double distance1 = this.getDistanceToBlock(o1);
                double distance2 = this.getDistanceToBlock(o2);
                return (int)(distance1 - distance2);
            });
            if (this.posList.size() < 3)
                this.setState(false);
            ArrayList<BlockPos> posListFor = new ArrayList<>(this.posList);
            int index = 1;
            for (BlockPos kid : posListFor) {
                if (++index % 2 != 1) continue;
                this.posList.remove(kid);
            }
            this.playerBed = this.posList.get(0);
            this.posList.remove(0);
            this.fuckingBed = this.posList.get(0);
        }
        catch (Throwable e) {
            this.setState(false);
        }
        super.onEnable();
    }

    @EventTarget
    public void onRender3D(Render3DEvent e) {
        try {
            for (Vec4 vec3 : this.path) {
                mc.getRenderManager();
                double x = vec3.getX() - mc.getRenderManager().renderPosX;
                mc.getRenderManager();
                double y = vec3.getY() - mc.getRenderManager().renderPosY;
                mc.getRenderManager();
                double z = vec3.getZ() - mc.getRenderManager().renderPosZ;
                double width = mc.thePlayer.getEntityBoundingBox().maxX - mc.thePlayer.getEntityBoundingBox().minX;
                double height = mc.thePlayer.getEntityBoundingBox().maxY - mc.thePlayer.getEntityBoundingBox().minY + 0.25;
                RenderUtils.drawEntityESP(x, y, z, width, height, 0.0f, 1.0f, 0.0f, 0.2f, 0.0f, 0.0f, 0.0f, 1.0f, 2.0f);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @EventTarget
    public void onUpdate(UpdateEvent e) {
        for (BlockPos pos : this.posList) {
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockBed) continue;
            PlayerUtil.tellPlayer("\u00a7b[AimWhere]Destory!" + pos);
            this.posList.remove(pos);
            this.posList.sort((o1, o2) -> {
                double distance1 = this.getDistanceToBlock(o1);
                double distance2 = this.getDistanceToBlock(o2);
                return (int)(distance1 - distance2);
            });
            this.fuckingBed = this.posList.get(0);
        }
        if (mc.thePlayer.getDistance(this.fuckingBed.getX(), this.fuckingBed.getY(), this.fuckingBed.getZ()) < 4.0) {
            PlayerUtil.tellPlayer("\u00a7b[AimWhere]Teleported! :3");
            this.setState(false);
        }

        if (this.timer.hasReached(this.delay.get())) {
            Vec4 topFrom = new Vec4(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            Vec4 to = new Vec4(this.fuckingBed.getX() + 1, this.fuckingBed.getY(), this.fuckingBed.getZ() + 1);
            this.path = PathUtils.computePath(topFrom, to);
            if (mc.thePlayer.getDistance(this.fuckingBed.getX(), this.fuckingBed.getY(), this.fuckingBed.getZ()) > 10.0) {
                for (Vec4 pathElm : this.path)
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), false));
            }
            this.timer.reset();
        }
        if (this.posList.size() == 0) {
            this.setState(false);
        }
    }

    public double getDistanceToBlock(BlockPos pos) {
        return mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ());
    }

    private boolean canPassThrow(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    private ArrayList<Vec4> computePath(Vec4 topFrom, Vec4 to) {
        if (!this.canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0.0, 1.0, 0.0);
        }
        AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();
        int i = 0;
        Vec4 lastLoc = null;
        Vec4 lastDashLoc = null;
        ArrayList<Vec4> path = new ArrayList<>();
        ArrayList<Vec4> pathFinderPath = pathfinder.getPath();
        for (Vec4 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0.0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > 25.0) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    int x = (int)smallX;
                    block1 : while ((double)x <= bigX) {
                        int y = (int)smallY;
                        while ((double)y <= bigY) {
                            int z = (int)smallZ;
                            while ((double)z <= bigZ) {
                                if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break block1;
                                }
                                ++z;
                            }
                            ++y;
                        }
                        ++x;
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            ++i;
        }
        return path;
    }
}

