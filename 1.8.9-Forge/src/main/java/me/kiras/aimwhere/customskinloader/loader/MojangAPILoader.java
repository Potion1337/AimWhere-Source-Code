package me.kiras.aimwhere.customskinloader.loader;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.util.UUIDTypeAdapter;
import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.ProfileLoader;
import me.kiras.aimwhere.customskinloader.profile.ModelManager0;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.HttpRequestUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class MojangAPILoader implements ProfileLoader.IProfileLoader {

   public static MinecraftSessionService defaultSessionService = null;


   public UserProfile loadProfile(SkinSiteProfile ssp, GameProfile gameProfile) throws Exception {
      if(defaultSessionService == null) {
         CustomSkinLoader.logger.warning("Session Service Not Exist.");
         return null;
      } else {
         Map map = getTextures(gameProfile);
         if(!map.isEmpty()) {
            CustomSkinLoader.logger.info("Default profile will be used.");
            return ModelManager0.toUserProfile(map);
         } else {
            String username = gameProfile.getName();
            GameProfile newGameProfile = loadGameProfile(username);
            if(newGameProfile == null) {
               CustomSkinLoader.logger.info("Profile not found.(" + username + "\'s profile not found.)");
               return null;
            } else {
               newGameProfile = defaultSessionService.fillProfileProperties(newGameProfile, false);
               map = getTextures(newGameProfile);
               if(!map.isEmpty()) {
                  gameProfile.getProperties().putAll(newGameProfile.getProperties());
                  return ModelManager0.toUserProfile(map);
               } else {
                  CustomSkinLoader.logger.info("Profile not found.(" + username + " doesn\'t have skin/cape.)");
                  return null;
               }
            }
         }
      }
   }

   public static GameProfile loadGameProfile(String username) throws Exception {
      HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest((new HttpRequestUtil.HttpRequest("https://api.mojang.com/users/profiles/minecraft/" + username)).setCacheTime(0));
      if(StringUtils.isEmpty(responce.content)) {
         return null;
      } else {
         Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
         GameProfile gameProfile = (GameProfile)gson.fromJson(responce.content, GameProfile.class);
         return gameProfile.getId() == null?null:new GameProfile(gameProfile.getId(), gameProfile.getName());
      }
   }

   public static Map<Type, MinecraftProfileTexture> getTextures(GameProfile gameProfile) throws Exception {
      Property textureProperty = (Property)Iterables.getFirst(gameProfile.getProperties().get("textures"), (Object)null);
      if(textureProperty == null) {
         return new HashMap();
      } else {
         String value = textureProperty.getValue();
         if(StringUtils.isBlank(value)) {
            return new HashMap();
         } else {
            String json = new String(Base64.decodeBase64(value), Charsets.UTF_8);
            Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
            MinecraftTexturesPayload result = (MinecraftTexturesPayload)gson.fromJson(json, MinecraftTexturesPayload.class);
            return (Map)(result != null && result.getTextures() != null?result.getTextures():new HashMap());
         }
      }
   }

   public boolean compare(SkinSiteProfile ssp0, SkinSiteProfile ssp1) {
      return true;
   }

   public String getName() {
      return "MojangAPI";
   }

   public void initLocalFolder(SkinSiteProfile ssp) {}
}
