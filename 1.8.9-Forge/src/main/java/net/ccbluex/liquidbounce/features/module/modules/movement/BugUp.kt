/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import me.kiras.aimwhere.utils.other.PlayerUtil
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

@ModuleInfo(
        name = "AntiVoid",
        description = "Automatically setbacks you after falling a certain distance.",
        category = ModuleCategory.MOVEMENT
)
class BugUp : Module() {
    private val modeValue = ListValue("Mode", arrayOf("RedeSky","Hypixel", "FlyFlag", "SpoofGround", "MotionTeleport-Flag"), "Hypixel")
    private val onlyVoidValue = BoolValue("OnlyVoid", true)
    private val maxFallDistance = IntegerValue("MaxFallDistance", 10, 2, 255)
    private val maxDistanceWithoutGround = FloatValue("MaxDistanceToSetback", 2.5F, 1f, 30f)
    private val indicator = BoolValue("Indicator", true)
    private val startFallDistValue=FloatValue("StartFallDistance",2F,0F,5F)
    private val maxFallDistValue=IntegerValue("MaxFallDistance",10,5,20)
    private val resetMotion=BoolValue("ResetMotion",false)
    private val autoScaffold=BoolValue("AutoScaffold",true)
    private val onlyOnce=BoolValue("OnlyOnce",true)

    private val packetCache=ArrayList<C03PacketPlayer>()
    private var blink=false
    private var canBlink=false

    private var posX=0.0
    private var posY=0.0
    private var posZ=0.0
    private var motionX=0.0
    private var motionY=0.0
    private var motionZ=0.0
    private var detectedLocation: BlockPos? = null
    private var lastFound = 0F
    private var prevX = 0.0
    private var prevY = 0.0
    private var prevZ = 0.0

    override fun onDisable() {
        prevX = 0.0
        prevY = 0.0
        prevZ = 0.0
    }

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        if(modeValue.get() == "RedeSky") {
            if(!onlyOnce.get()){
                canBlink=true
            }
            if(onlyVoidValue.get() && PlayerUtil.isBlockUnder())
                return
            if(!blink){
                if(canBlink && mc.thePlayer.motionY<0 && mc.thePlayer.fallDistance>startFallDistValue.get()){
                    posX=mc.thePlayer.posX
                    posY=mc.thePlayer.posY
                    posZ=mc.thePlayer.posZ
                    motionX=mc.thePlayer.motionX
                    motionY=mc.thePlayer.motionY
                    motionZ=mc.thePlayer.motionZ

                    packetCache.clear()
                    blink=true
                }

                if(mc.thePlayer.onGround){
                    canBlink=true
                }
            }else{
                if(mc.thePlayer.fallDistance > maxFallDistValue.get()){
                    mc.thePlayer.setPositionAndUpdate(posX,posY,posZ)
                    if(resetMotion.get()){
                        mc.thePlayer.motionX=0.0
                        mc.thePlayer.motionY=0.0
                        mc.thePlayer.motionZ=0.0
                    }else{
                        mc.thePlayer.motionX=motionX
                        mc.thePlayer.motionY=motionY
                        mc.thePlayer.motionZ=motionZ
                    }

                    if(autoScaffold.get())
                        LiquidBounce.moduleManager.getModule(Scaffold::class.java).state = true

                    packetCache.clear()
                    blink = false
                    canBlink = false
                } else if(mc.thePlayer.onGround){
                    blink = false
                    packetCache.forEach {
                        mc.netHandler.addToSendQueue(it)
                    }
                }
            }
        }
        detectedLocation = null

        if (mc.thePlayer.onGround && BlockUtils.getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0,
                        mc.thePlayer.posZ)) !is BlockAir) {
            prevX = mc.thePlayer.prevPosX
            prevY = mc.thePlayer.prevPosY
            prevZ = mc.thePlayer.prevPosZ
        }

        if (!mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater) {
            val fallingPlayer = FallingPlayer(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    mc.thePlayer.motionX,
                    mc.thePlayer.motionY,
                    mc.thePlayer.motionZ,
                    mc.thePlayer.rotationYaw,
                    mc.thePlayer.moveStrafing,
                    mc.thePlayer.moveForward
            )

            detectedLocation = fallingPlayer.findCollision(60)?.pos

            if (detectedLocation != null && abs(mc.thePlayer.posY - detectedLocation!!.y) +
                    mc.thePlayer.fallDistance <= maxFallDistance.get()) {
                lastFound = mc.thePlayer.fallDistance
            }

            if (mc.thePlayer.fallDistance - lastFound > maxDistanceWithoutGround.get() && (!onlyVoidValue.get() || !PlayerUtil.isBlockUnder())) {
                val mode = modeValue.get()

                when (mode.toLowerCase()) {
                    "hypixel" -> {
                        mc.thePlayer.setPositionAndUpdate(prevX, prevY, prevZ)
                    }
                    "flyflag" -> {
                        mc.thePlayer.motionY += 0.1
                        mc.thePlayer.fallDistance = 0F
                    }
                    "spoofground" -> mc.netHandler.addToSendQueue(C03PacketPlayer(true))

                    "motionteleport-flag" -> {
                        mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY + 1f, mc.thePlayer.posZ)
                        mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true))
                        mc.thePlayer.motionY = 0.1
                        MovementUtils.strafe()
                        mc.thePlayer.fallDistance = 0f
                    }
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(blink && (event.packet is C03PacketPlayer) && modeValue.get() == "RedeSky"){
            packetCache.add(event.packet)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (detectedLocation == null || !indicator.get() ||
                mc.thePlayer.fallDistance + (mc.thePlayer.posY - (detectedLocation!!.y + 1)) < 3)
            return

        val x = detectedLocation!!.x
        val y = detectedLocation!!.y
        val z = detectedLocation!!.z

        val renderManager = mc.renderManager

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glLineWidth(2f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        RenderUtils.glColor(Color(255, 0, 0, 90))
        RenderUtils.drawFilledBox(AxisAlignedBB(
                x - renderManager.renderPosX,
                y + 1 - renderManager.renderPosY,
                z - renderManager.renderPosZ,
                x - renderManager.renderPosX + 1.0,
                y + 1.2 - renderManager.renderPosY,
                z - renderManager.renderPosZ + 1.0)
        )

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)

        val fallDist = floor(mc.thePlayer.fallDistance + (mc.thePlayer.posY - (y + 0.5))).toInt()

        RenderUtils.renderNameTag("${fallDist}m (~${max(0, fallDist - 3)} damage)", x + 0.5, y + 1.7, z + 0.5)

        GlStateManager.resetColor()
    }
}