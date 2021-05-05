package me.kiras.aimwhere.viaversion.util;
import us.myles.ViaVersion.api.protocol.ProtocolVersion;

public class ProtocolUtils {

    public static String getProtocolName(int id) {
        if (!ProtocolVersion.isRegistered(id)) return Integer.toString(id);
        return ProtocolVersion.getProtocol(id).getName();
    }
}
