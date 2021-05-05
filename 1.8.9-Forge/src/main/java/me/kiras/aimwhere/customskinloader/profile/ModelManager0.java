package me.kiras.aimwhere.customskinloader.profile;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.HashMap;
import java.util.Map;

import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import org.apache.commons.lang3.StringUtils;

public class ModelManager0 {
    private static HashMap models = new HashMap();
    private static Type typeElytra = null;
    public enum Model {
        SKIN_DEFAULT,
        SKIN_SLIM,
        CAPE,
        ELYTRA;
    }

    static {
        Type[] var3;
        int var2 = (var3 = Type.values()).length;

        for(int var1 = 0; var1 < var2; ++var1) {
            Type type = var3[var1];
            if (type.ordinal() == 2) {
                typeElytra = type;
            }
        }

        models.put("default", Model.SKIN_DEFAULT);
        models.put("slim", Model.SKIN_SLIM);
        models.put("cape", Model.CAPE);
        if (typeElytra != null) {
            models.put("elytra", Model.ELYTRA);
        }

    }

    public static Model getEnumModel(String model) {
        return (Model)models.get(model);
    }

    public static boolean isSkin(Model model) {
        return model == Model.SKIN_DEFAULT || model == Model.SKIN_SLIM;
    }

    public static boolean isElytraSupported() {
        return typeElytra != null;
    }

    public static UserProfile toUserProfile(Map profile) {
        UserProfile userProfile = new UserProfile();
        if (profile == null) {
            return userProfile;
        } else {
            MinecraftProfileTexture skin = (MinecraftProfileTexture)profile.get(Type.SKIN);
            userProfile.skinUrl = skin == null ? null : skin.getUrl();
            userProfile.model = skin == null ? null : skin.getMetadata("model");
            if (StringUtils.isEmpty(userProfile.model)) {
                userProfile.model = "default";
            }

            MinecraftProfileTexture cape = (MinecraftProfileTexture)profile.get(Type.CAPE);
            userProfile.capeUrl = cape == null ? null : cape.getUrl();
            return userProfile;
        }
    }

    public static Map fromUserProfile(UserProfile profile) {
        Map map = Maps.newHashMap();
        if (profile == null) {
            return map;
        } else {
            if (profile.skinUrl != null) {
                Map metadata = null;
                if (profile.model != null && profile.model.equals("slim")) {
                    metadata = Maps.newHashMap();
                    metadata.put("model", "slim");
                }

                map.put(Type.SKIN, getProfileTexture(profile.skinUrl, metadata));
            }

            if (profile.capeUrl != null) {
                map.put(Type.CAPE, getProfileTexture(profile.capeUrl, (Map)null));
            }

            if (typeElytra != null && profile.elytraUrl != null) {
                map.put(typeElytra, getProfileTexture(profile.elytraUrl, (Map)null));
            }

            return map;
        }
    }

    public static MinecraftProfileTexture getProfileTexture(String url, Map metadata) {
        return new MinecraftProfileTexture(url, metadata);
    }
}
