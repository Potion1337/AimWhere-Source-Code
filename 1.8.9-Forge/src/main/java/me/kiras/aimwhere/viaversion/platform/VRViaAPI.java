package me.kiras.aimwhere.viaversion.platform;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.ProtocolRegistry;
import us.myles.ViaVersion.boss.CommonBoss;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

public class VRViaAPI implements ViaAPI<UUID> {
    @Override
    public int getPlayerVersion(UUID uuid) {
        UserConnection con = Via.getManager().getConnection(uuid);
        if (con != null) {
            return Objects.requireNonNull(con.getProtocolInfo()).getProtocolVersion();
        }
        try {
            return Via.getManager().getInjector().getServerProtocolVersion();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public boolean isInjected(UUID uuid) {
        return Via.getManager().isClientConnected(uuid);
    }

    @Override
    public String getVersion() {
        return Via.getPlatform().getPluginVersion();
    }

    @Override
    public void sendRawPacket(UUID uuid, ByteBuf byteBuf) throws IllegalArgumentException {
        UserConnection ci = Via.getManager().getConnection(uuid);
        assert ci != null;
        ci.sendRawPacket(byteBuf);
    }

    @Override
    public BossBar<Void> createBossBar(String s, BossColor bossColor, BossStyle bossStyle) {
        return new VRBossBar(s, 1f, bossColor, bossStyle);
    }

    @Override
    public BossBar<Void> createBossBar(String s, float v, BossColor bossColor, BossStyle bossStyle) {
        return new VRBossBar(s, v, bossColor, bossStyle);
    }

    @Override
    public SortedSet<Integer> getSupportedVersions() {
        SortedSet<Integer> outputSet = new TreeSet<>(ProtocolRegistry.getSupportedVersions());
        outputSet.removeAll(Via.getPlatform().getConf().getBlockedProtocols());

        return outputSet;
    }
}
class VRBossBar extends CommonBoss<Void> {
    public VRBossBar(String title, float health, BossColor color, BossStyle style) {
        super(title, health, color, style);
    }
}
