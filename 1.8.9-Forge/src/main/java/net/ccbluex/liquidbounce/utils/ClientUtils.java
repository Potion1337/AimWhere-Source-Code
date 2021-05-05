/*
 * AimWhere Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.utils;

import com.google.gson.JsonObject;
import me.kiras.aimwhere.utils.render.Notifications;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.crypto.SecretKey;
import java.awt.*;
import java.lang.reflect.Field;
import java.security.PublicKey;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public final class ClientUtils extends MinecraftInstance {
    private static ArrayList<Notifications> notifications = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger("AimWhere");

    private static Field fastRenderField;

    public static void sendClientMessage(String message, Notifications.Type type) {
        if(notifications.size() > 6)
            notifications.remove(0);
        notifications.add(new Notifications(message, type));
    }

    public static void drawNotifications() {
        ScaledResolution res = new ScaledResolution(mc);
        double startY = res.getScaledHeight() - 25;
        for (int i = 0; i < notifications.size(); ++i) {
            Notifications not = notifications.get(i);
            notifications.removeIf(Notifications::shouldDelete);
            not.draw(startY);
            startY -= not.getHeight() + 1.0;
        }
    }

    static {
        try {
            fastRenderField = GameSettings.class.getDeclaredField("ofFastRender");

            if(!fastRenderField.isAccessible())
                fastRenderField.setAccessible(true);
        }catch(final NoSuchFieldException ignored) {
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void disableFastRender() {
        try {
            if(fastRenderField != null) {
                if(!fastRenderField.isAccessible())
                    fastRenderField.setAccessible(true);

                fastRenderField.setBoolean(mc.gameSettings, false);
            }
        }catch(final IllegalAccessException ignored) {
        }
    }

    public static void sendEncryption(final NetworkManager networkManager, final SecretKey secretKey, final PublicKey publicKey, final S01PacketEncryptionRequest encryptionRequest) {
        networkManager.sendPacket(new C01PacketEncryptionResponse(secretKey, publicKey, encryptionRequest.getVerifyToken()), p_operationComplete_1_ -> networkManager.enableEncryption(secretKey));
    }

    public static void displayChatMessage(final String message) {
        if (mc.thePlayer == null) {
            getLogger().info("(MCChat)" + message);
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);

        mc.thePlayer.addChatMessage(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()));
    }

    public static String removeColorCode(String displayString) {
        return displayString.replaceAll("\247.", "");
    }
    public static int reAlpha(int color, float alpha) {
        Color c = new Color(color);
        float r = 0.003921569f * (float)c.getRed();
        float g = 0.003921569f * (float)c.getGreen();
        float b = 0.003921569f * (float)c.getBlue();
        return new Color(r, g, b, alpha).getRGB();
    }
}