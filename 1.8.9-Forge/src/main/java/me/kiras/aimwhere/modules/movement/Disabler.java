package me.kiras.aimwhere.modules.movement;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.misc.RandomUtils;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleInfo(
        name = "Disabler",
        description = "Disabler",
        category = ModuleCategory.MOVEMENT
)
public final class Disabler extends Module {
    private final Queue<Short> queueID = new ConcurrentLinkedQueue<>();
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Hypixel","Mineplex","AAC1.9.10"}, "Hypixel");
    @EventTarget
    public void onWorld(WorldEvent event) {
        if(event.getWorldClient() != null && !queueID.isEmpty())
            return;
        queueID.clear();
    }
    @EventTarget
    public void onPacket(PacketEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null)
            return;
        final Packet<?> packet = event.getPacket();
        final short uid = -1;
        if(modeValue.get().equals("Mineplex")) {
            if(packet instanceof C00PacketKeepAlive && !(((C00PacketKeepAlive) packet).getKey() >= 1000)) {
                event.cancelEvent();
                int key = ((C00PacketKeepAlive) packet).getKey();
                key -= RandomUtils.nextInt(1000, 2147483647);
                mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive(key));
            }
        }
        if(modeValue.get().equals("AAC1.9.10")) {
            if(packet instanceof C03PacketPlayer) {
                mc.getNetHandler().addToSendQueue(new C0CPacketInput());
                ((C03PacketPlayer) packet).y += 7.0E-9;
            }
        }
        if (modeValue.get().equals("Hypixel")) {
            if (packet instanceof S32PacketConfirmTransaction) {
                final S32PacketConfirmTransaction packetConfirmTransaction = (S32PacketConfirmTransaction) packet;
                if (packetConfirmTransaction.getActionNumber() < 0 && packetConfirmTransaction.getWindowId() == 0) {
                    event.cancelEvent();
                    mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(mc.thePlayer.inventoryContainer.windowId, queueID.isEmpty() ? uid : queueID.poll(), false));
                }
            }
            if (packet instanceof C00PacketKeepAlive) {
                event.cancelEvent();
            }
            if (packet instanceof S00PacketKeepAlive) {
                event.cancelEvent();
                mc.getNetHandler().addToSendQueue(new C00PacketKeepAlive(RandomUtils.nextInt(-114514, 1919810)));
            }
            if (packet instanceof C0FPacketConfirmTransaction) {
                final C0FPacketConfirmTransaction packetConfirmTransaction = (C0FPacketConfirmTransaction) packet;
                if (packetConfirmTransaction.getWindowId() < 0 && packetConfirmTransaction.getWindowId() == 0) {
                    event.cancelEvent();
                    mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(0, queueID.isEmpty() ? uid : queueID.poll(), false));
                    queueID.offer(packetConfirmTransaction.getUid());
                }
            }
        }
    }
    @Override
    public String getTag() {
        return modeValue.get();
    }
}