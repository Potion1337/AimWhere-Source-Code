package me.kiras.aimwhere.modules.player
import me.kiras.aimwhere.utils.other.MessageBase
import me.kiras.aimwhere.utils.timer.TimerUtil
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.network.play.server.S02PacketChat

@ModuleInfo(
        name = "AntiSpammer",
        description = "check message and cancel",
        category = ModuleCategory.PLAYER
)
class AntiSpammer : Module() {
    private var currentMessage = ""
    private var playerMessage = ""
    private val messageList = ArrayList<MessageBase>()
    private var messageIndex = 0
    private val timer = TimerUtil()
    private val indexValue = IntegerValue("Index", 8,1,20)
    private val notCancelPlayerMessage = BoolValue("NoCancelPlayerMessage", true)
    @EventTarget
    fun onUpdate(event : UpdateEvent) {
        if(messageIndex == 0 || !timer.hasReached(10000.0))
            return
        ClientUtils.displayChatMessage("§b[§aAntiChat§b] §cRemoved $messageIndex Messages.")
        messageIndex = 0
        timer.reset()
    }

    override val tag: String
        get() = "Basic"
    @EventTarget
    fun onPacket(packetEvent: PacketEvent) {
        val packet = packetEvent.packet
        if(packet is C01PacketChatMessage) {
            playerMessage = packet.message
        }
        if (packet is S02PacketChat) {
            currentMessage = packet.chatComponent.unformattedText
            if(playerMessage == currentMessage && notCancelPlayerMessage.get() && currentMessage.contains(mc.thePlayer.name))
                return
            val messageBase = currentMessage.toLowerCase()
            if(messageBase.contains("loser") || messageBase.contains("lixo") || messageBase.contains("sb") || messageBase.contains("废物") || messageBase.contains("fw") || messageBase.contains("hack") || messageBase.contains("你妈") || messageBase.contains("mother") || messageBase.contains("加群")) {
                packetEvent.cancelEvent()
                messageIndex++
                return
            }
            messageList.add(MessageBase(currentMessage))
            for(i in messageList) {
                if(messageBase == i.message)
                    i.index++
                else
                    i.notThis = true
                if(i.index > 4) {
                    packetEvent.cancelEvent()
                    messageIndex++
                }
                if(!i.notThis)
                    i.nextIndex++
                else
                    i.nextIndex = 0
                if(i.nextIndex > indexValue.get())
                    messageList.remove(i)
            }
        }
    }

    override fun onEnable() {
        messageList.clear()
        messageIndex = 0
        timer.reset()
    }
}