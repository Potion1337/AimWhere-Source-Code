package net.ccbluex.liquidbounce.features.module.modules.movement
import me.kiras.aimwhere.utils.other.PlayerUtil
import me.kiras.aimwhere.utils.timer.TimerUtil
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.exploit.Phase
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockChest
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.stats.StatList
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import kotlin.math.cos
import kotlin.math.sin


@ModuleInfo(
        name = "Step",
        description = "Allows you to step up blocks.",
        category = ModuleCategory.MOVEMENT
)
class Step : Module() {

    /**
     * OPTIONS
     */

    private val modeValue = ListValue("Mode", arrayOf(
            "Vanilla", "Jump", "NCP", "Motion", "OldNCP", "LAAC", "AAC3.3.4", "Spartan", "Rewinside", "WAC", "Hypixel", "NewNCP", "Packet"), "Motion")

    private val heightValue = FloatValue("Height", 1F, 0.6F, 10F)
    private val jumpHeightValue = FloatValue("JumpHeight", 0.42F, 0.37F, 0.42F)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)

    /**
     * VALUES
     */

    private var isStep = false
    private var stepX = 0.0
    private var stepY = 0.0
    private var stepZ = 0.0
    private var groundSpoof = false

    private var ncpNextStep = 0
    private var spartanSwitch = false
    private var isAACStep = false

    private val timer = TimerUtil()

    override fun onDisable() {
        mc.thePlayer ?: return

        // Change step height back to default (0.5 is default)
        mc.thePlayer.stepHeight = 0.5F
    }

    private fun canStep() : Boolean {
        return !(LiquidBounce.moduleManager[LongJump::class.java].state && LiquidBounce.moduleManager[Scaffold::class.java].state && LiquidBounce.moduleManager[Fly::class.java].state)
    }

    private fun ncpStep(n: Double) {
        if(!canStep())
            return
        val list: List<Double> = listOf(0.42, 0.333, 0.248, 0.083, -0.078)
        val posX: Double = mc.thePlayer.posX
        val posZ: Double = mc.thePlayer.posZ
        var posY: Double = mc.thePlayer.posY
        if (n < 1.1) {
            var n2 = 0.42
            var n3 = 0.75
            if (n != 1.0) {
                n2 *= n
                n3 *= n
                if (n2 > 0.425) {
                    n2 = 0.425
                }
                if (n3 > 0.78) {
                    n3 = 0.78
                }
                if (n3 < 0.49) {
                    n3 = 0.49
                }
            }
            if (n2 == 0.42) {
                n2 = 0.41999998688698
            }
            mc.thePlayer.sendQueue
                    .addToSendQueue(C04PacketPlayerPosition(posX, posY + n2, posZ, false))
            if (posY + n3 < posY + n) {
                mc.thePlayer.sendQueue
                        .addToSendQueue(C04PacketPlayerPosition(posX, posY + n3, posZ, false))
            }
            return
        }
        when {
            n < 1.6 -> {
                var i = 0
                while (i < list.size) {
                    posY += list[i]
                    mc.thePlayer.sendQueue
                            .addToSendQueue(C04PacketPlayerPosition(posX, posY, posZ, false))
                    ++i
                }
            }
            n < 2.1 -> {
                val array = doubleArrayOf(0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869)
                val length = array.size
                var j = 0
                while (j < length) {
                    mc.thePlayer.sendQueue.addToSendQueue(
                            C04PacketPlayerPosition(posX, posY + array[j], posZ, false))
                    ++j
                }
            }
            else -> {
                val array2 = doubleArrayOf(0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907)
                val length2 = array2.size
                var k = 0
                while (k < length2) {
                    mc.thePlayer.sendQueue.addToSendQueue(
                            C04PacketPlayerPosition(posX, posY + array2[k], posZ, false))
                    ++k
                }
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val mode = modeValue.get()

        // Motion steps
        when {
            mode.equals("jump", true) && mc.thePlayer.isCollidedHorizontally && mc.thePlayer.onGround
                    && !mc.gameSettings.keyBindJump.isKeyDown -> {
                fakeJump()
                mc.thePlayer.motionY = jumpHeightValue.get().toDouble()
            }

            mode.equals("newncp", true) -> {
                if (groundSpoof && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWeb && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava) {
                    mc.thePlayer.stepHeight = heightValue.get()
                } else if (groundSpoof && !mc.thePlayer.isInWeb) {
                    mc.thePlayer.stepHeight = 1F
                } else {
                    mc.thePlayer.stepHeight = 0F
                }
                groundSpoof = (mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically);
            }

            mode.equals("laac", true) -> if (mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder
                    && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb) {
                if (mc.thePlayer.onGround && timer.hasReached(delayValue.get().toDouble())) {
                    isStep = true

                    fakeJump()
                    mc.thePlayer.motionY += 0.620000001490116

                    val f = mc.thePlayer.rotationYaw * 0.017453292F
                    mc.thePlayer.motionX -= MathHelper.sin(f) * 0.2
                    mc.thePlayer.motionZ += MathHelper.cos(f) * 0.2
                    timer.reset()
                }

                mc.thePlayer.onGround = true
            } else
                isStep = false

            mode.equals("aac3.3.4", true) || mode.equals("wac", ignoreCase = true)-> if (mc.thePlayer.isCollidedHorizontally
                    && MovementUtils.isMoving()) {
                if (mc.thePlayer.onGround && couldStep()) {
                    mc.thePlayer.motionX *= 1.26
                    mc.thePlayer.motionZ *= 1.26
                    mc.thePlayer.jump()
                    isAACStep = true
                }

                if (isAACStep) {
                    mc.thePlayer.motionY -= 0.015

                    if(!mc.thePlayer.isUsingItem && mc.thePlayer.movementInput.moveStrafe == 0F)
                        mc.thePlayer.jumpMovementFactor = 0.3F
                }
            } else
                isAACStep = false
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val mode = modeValue.get()

        // Motion steps
        when {
            mode.equals("motion", true) && mc.thePlayer.isCollidedHorizontally && !mc.gameSettings.keyBindJump.isKeyDown -> {
                when {
                    mc.thePlayer.onGround && couldStep() -> {
                        fakeJump()
                        mc.thePlayer.motionY = 0.0
                        event.y = 0.41999998688698
                        ncpNextStep = 1
                    }

                    ncpNextStep == 1 -> {
                        event.y = 0.7532 - 0.2
                        ncpNextStep = 2
                    }

                    ncpNextStep == 2 -> {
                        val yaw = MovementUtils.getDirection()
                        event.y = 1.001335979112147 - 0.7531999805212
                        event.x = -sin(yaw) * 0.7
                        event.z = cos(yaw) * 0.7

                        ncpNextStep = 0
                    }
                }
            }
        }
    }

    var resetTimer = false
    private var type = ""
    private val time = TimerUtil()

    @EventTarget
    fun onMotion(event : MotionEvent) {
        type = event.eventState.stateName
    }

    @EventTarget
    fun onStep(event: StepEvent) {
        mc.thePlayer ?: return

        if(modeValue.get() == "Hypixel") {
            if(!canStep() || mc.theWorld.getBlockState(BlockPos(mc.thePlayer.posX,stepY,mc.thePlayer.posZ)).block is BlockChest) {
                return
            }
            if (!PlayerUtil.isInWater()) {
                if (!mc.thePlayer.onGround || !timer.hasReached(delayValue.get().toDouble())) {
                    mc.thePlayer.stepHeight = 0.5f
                    event.stepHeight = 0.5F
                    return
                }
                mc.thePlayer.stepHeight = heightValue.get()
                event.stepHeight = heightValue.get()
            }
        }

        // Phase should disable step
        if (LiquidBounce.moduleManager[Phase::class.java].state) {
            event.stepHeight = 0F
            return
        }

        val mode = modeValue.get()

        // Set step to default in some cases
        if (!mc.thePlayer.onGround || !timer.hasReached(delayValue.get().toDouble()) ||
                mode.equals("Jump", ignoreCase = true) || mode.equals("Motion", ignoreCase = true)
                || mode.equals("LAAC", ignoreCase = true) || mode.equals("AAC3.3.4", ignoreCase = true)) {
            mc.thePlayer.stepHeight = 0.5F
            event.stepHeight = 0.5F
            return
        }

        // Set step height
        val height = heightValue.get()
        mc.thePlayer.stepHeight = height
        event.stepHeight = height

        // Detect possible step
        if (event.stepHeight > 0.5F) {
            isStep = true
            stepX = mc.thePlayer.posX
            stepY = mc.thePlayer.posY
            stepZ = mc.thePlayer.posZ
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onStepConfirm(event: StepConfirmEvent) {
        if (mc.thePlayer == null || !isStep) // Check if step
            return
        if (mc.thePlayer.entityBoundingBox.minY - stepY > 0.5) { // Check if full block step
            val mode = modeValue.get()
            when {
                mode.equals("Hypixel", ignoreCase = true) -> {
                    if(canStep() && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb && event.stepHeight > 0.5) {
                        val height = mc.thePlayer.entityBoundingBox.minY - stepY
                        if (height >= 0.625) {
                            ncpStep(height)
                            timer.reset()
                        }
                    }
                }
                mode.equals("NCP", ignoreCase = true) -> {
                    fakeJump()

                    // Half legit step (1 packet missing) [COULD TRIGGER TOO MANY PACKETS]
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                            stepY + 0.41999998688698, stepZ, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                            stepY + 0.7531999805212, stepZ, false))
                    timer.reset()
                }

                mode.equals("Packet", ignoreCase = true) -> {
                    fakeJump()
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX, stepY + 1.16, stepZ, false))
                    timer.reset()
                }

                mode.equals("Spartan", ignoreCase = true) -> {
                    fakeJump()

                    if (spartanSwitch) {
                        // Vanilla step (3 packets) [COULD TRIGGER TOO MANY PACKETS]
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                                stepY + 0.41999998688698, stepZ, false))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                                stepY + 0.7531999805212, stepZ, false))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                                stepY + 1.001335979112147, stepZ, false))
                    } else // Force step
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                                stepY + 0.6, stepZ, false))

                    // Spartan allows one unlegit step so just swap between legit and unlegit
                    spartanSwitch = !spartanSwitch

                    // Reset timer
                    timer.reset()
                }

                mode.equals("Rewinside", ignoreCase = true) -> {
                    fakeJump()

                    // Vanilla step (3 packets) [COULD TRIGGER TOO MANY PACKETS]
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                            stepY + 0.41999998688698, stepZ, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                            stepY + 0.7531999805212, stepZ, false))
                    mc.netHandler.addToSendQueue(C04PacketPlayerPosition(stepX,
                            stepY + 1.001335979112147, stepZ, false))

                    // Reset timer
                    timer.reset()
                }
            }
        }

        isStep = false
        stepX = 0.0
        stepY = 0.0
        stepZ = 0.0
    }

    @EventTarget(ignoreCondition = true)
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer && isStep && modeValue.get().equals("OldNCP", ignoreCase = true)) {
            packet.y += 0.07
            isStep = false
        }
    }

    override fun onEnable() {
        groundSpoof = false
    }

    // There could be some anti cheats which tries to detect step by checking for achievements and stuff
    private fun fakeJump() {
        mc.thePlayer.isAirBorne = true
        mc.thePlayer.triggerAchievement(StatList.jumpStat)
    }

    private fun couldStep(): Boolean {
        val yaw = MovementUtils.getDirection()
        val x = -sin(yaw) * 0.4
        val z = cos(yaw) * 0.4

        return mc.theWorld.getCollisionBoxes(mc.thePlayer.entityBoundingBox.offset(x, 1.001335979112147, z))
                .isEmpty()
    }

    override val tag: String
        get() = modeValue.get()
}