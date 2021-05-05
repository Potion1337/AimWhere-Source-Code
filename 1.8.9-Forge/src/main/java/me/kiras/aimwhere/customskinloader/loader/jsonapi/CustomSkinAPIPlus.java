package me.kiras.aimwhere.customskinloader.loader.jsonapi;

import com.google.gson.Gson;
import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.JsonAPILoader;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.MinecraftUtil;
import java.io.File;
import java.util.UUID;
import org.apache.commons.io.FileUtils;

public class CustomSkinAPIPlus implements JsonAPILoader.IJsonAPI {

   private static String clientID = null;


   public CustomSkinAPIPlus() {
      File clientIDFile = new File(CustomSkinLoader.DATA_DIR, "CustomSkinAPIPlus-ClientID");
      if(clientIDFile.isFile()) {
         try {
            clientID = FileUtils.readFileToString(clientIDFile);
         } catch (Exception var4) {
            var4.printStackTrace();
         }
      }

      if(clientID == null) {
         clientID = UUID.randomUUID().toString();

         try {
            FileUtils.write(clientIDFile, clientID);
         } catch (Exception var3) {
            var3.printStackTrace();
         }
      }

   }

   public String toJsonUrl(String root, String username) {
      return JsonAPILoader.Type.CustomSkinAPI.jsonAPI.toJsonUrl(root, username);
   }

   public String getPayload(SkinSiteProfile ssp) {
      if(ssp.privacy == null) {
         ssp.privacy = new CustomSkinAPIPlusPrivacy();
      }

      return (new Gson()).toJson(new CustomSkinAPIPlusPayload(ssp.privacy));
   }

   public UserProfile toUserProfile(String root, String json, boolean local) {
      return JsonAPILoader.Type.CustomSkinAPI.jsonAPI.toUserProfile(root, json, local);
   }

   public String getName() {
      return "CustomSKinAPIPlus";
   }

   public static class CustomSkinAPIPlusPrivacy {

      public boolean gameVersion = true;
      public boolean modVersion = true;
      public boolean serverAddress = true;
      public boolean clientID = true;


   }

   public static class CustomSkinAPIPlusPayload {

      public String gameVersion;
      public String modVersion;
      public String serverAddress;
      public String clientID;


      public CustomSkinAPIPlusPayload(CustomSkinAPIPlusPrivacy privacy) {
         if(privacy.gameVersion) {
            this.gameVersion = MinecraftUtil.getMinecraftMainVersion();
         }

         if(privacy.modVersion) {
            this.modVersion = "14.6";
         }

         if(privacy.serverAddress) {
            this.serverAddress = MinecraftUtil.isLanServer()?null:MinecraftUtil.getStandardServerAddress();
         }

         if(privacy.clientID) {
            this.clientID = CustomSkinAPIPlus.clientID;
         }

      }
   }
}
