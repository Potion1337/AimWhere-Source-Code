package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils.getBaseMoveSpeed
import net.ccbluex.liquidbounce.utils.MovementUtils.getJumpEffect
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.setMotion
import java.util.*
import kotlin.math.sqrt


class Hypixel : SpeedMode("Hypixel") {
    var lastDist: Double = 0.0
    var speed: Double = 0.0
    var stage: Int = 1

    override fun onEnable() {
        stage = 2
        super.onEnable()
    }

    override fun onDisable() {
        speed = getBaseMoveSpeed()
        stage = 2
    }

    override fun onMotion() {
        val thePlayer = mc.thePlayer ?: return
        val xDist: Double = thePlayer.posX - thePlayer.prevPosX
        val zDist: Double = thePlayer.posZ - thePlayer.prevPosZ
        lastDist = sqrt(xDist * xDist + zDist * zDist)
    }

    override fun onUpdate() {
    }

    override fun onMove(event: MoveEvent) {
        val thePlayer = mc.thePlayer ?: return
        if (stage > 0 && !thePlayer.isInWater) {

            if (stage == 1 && thePlayer.onGround && isMoving())
                stage += 1

            if (stage == 2 && thePlayer.onGround && isMoving()) {
                event.y = (0.399999986886975+ getJumpEffect() * .1).also {
                    mc.thePlayer!!.motionY = it
                }

            } else
                if (stage >= 3) {
                    if (mc.thePlayer!!.isCollidedVertically) {
                        speed = getBaseMoveSpeed()
                        lastDist = 0.0
                        stage = 0
                    }
                }

            getHypixelBest()
        } else {
            stage = 0
        }
        if (isMoving()) {
            setMotion(event, speed)
        } else {
            setMotion(event, 0.0)
            stage = 0
        }
        ++stage
    }

    private fun getHypixelBest() {
        val thePlayer = mc.thePlayer ?: return
        var slowdown = false

        when (stage) {
            1 -> {
                stage = 2
            }
            2 -> {
                if (thePlayer.onGround && isMoving())
                    speed *= 1.7
            }
            3 -> {
                speed += Random().nextDouble() / 4799
                val difference: Double = 0.66 * (lastDist - getBaseMoveSpeed())
                speed = lastDist - difference
                speed -= Random().nextDouble() / 4799
            }
            else -> {
                slowdown = thePlayer.fallDistance > 0.0

                if (mc.theWorld!!.getCollidingBoundingBoxes(
                        thePlayer,
                        thePlayer.entityBoundingBox.offset(0.0, thePlayer.motionY, 0.0)
                    ).isNotEmpty() || thePlayer.isCollidedVertically && thePlayer.onGround
                ) {
                    stage = 1
                }
                speed = lastDist - lastDist / 159
            }
        }
        speed = Math.max(speed - if (slowdown) Math.sqrt(lastDist * lastDist + speed * speed) * 0.012 else 0.02 * lastDist, getBaseMoveSpeed())

        if (slowdown) {
            speed *= 1.0 - (lastDist / 50)
        }
    }
}