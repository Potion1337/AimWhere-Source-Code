package me.kiras.aimwhere.customskinloader.loader;

import com.mojang.authlib.GameProfile;
import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.ProfileLoader;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.HttpRequestUtil;
import me.kiras.aimwhere.customskinloader.utils.MinecraftUtil;
import java.util.HashMap;
import java.util.prefs.Preferences;
import org.apache.commons.lang3.StringUtils;

public class ElfSkinLoader implements ProfileLoader.IProfileLoader {

   private static final String LOGIN_URL = "http://status.mcelf.com/login?gid=%SERVER_IP%&name=%USERNAME%&oid=%ELF_ID%";
   private static final String LOGOUT_URL = "http://status.mcelf.com/logout?gid=%SERVER_IP%&name=%USERNAME%";
   private static final String PROFILE_URL = "http://status.mcelf.com/s?gid=%SERVER_IP%&name=%USERNAME%";
   private static String elfID = Preferences.userRoot().node("elfskin").get("elfid", "null");
   private static String lastLoginServer = null;
   private static HashMap<String, Profile> cache = new HashMap();


   public UserProfile loadProfile(SkinSiteProfile ssp, GameProfile gameProfile) throws Exception {
      String username = gameProfile.getName();
      String ip = MinecraftUtil.getServerAddress();
      if(elfID != null && this.isIPChange(ip)) {
         if(lastLoginServer != null) {
            makeLogout(lastLoginServer, MinecraftUtil.getCurrentUsername());
         }

         Profile[] p = makeLogin(ip, MinecraftUtil.getCurrentUsername(), elfID);
         cache.clear();
         Profile[] var9 = p;
         int var8 = p.length;

         for(int var7 = 0; var7 < var8; ++var7) {
            Profile profile = var9[var7];
            cache.put(profile.name, profile);
         }
      }

      Profile var10 = cache.containsKey(username)?(Profile)cache.get(username):getProfile(MinecraftUtil.getServerAddress(), username);
      if(var10 == null) {
         return null;
      } else {
         UserProfile var11 = new UserProfile();
         var11.skinUrl = var10.skin;
         var11.capeUrl = var10.cape;
         if(var11.isEmpty()) {
            CustomSkinLoader.logger.info("Profile is empty.");
            return null;
         } else {
            return var11;
         }
      }
   }

   private boolean isIPChange(String newIP) {
      return lastLoginServer == null?newIP != null:!lastLoginServer.equalsIgnoreCase(newIP);
   }

   public static Profile[] makeLogin(String ip, String username, String elfID) {
      String url = "http://status.mcelf.com/login?gid=%SERVER_IP%&name=%USERNAME%&oid=%ELF_ID%".replaceAll("%SERVER_IP%", ip).replaceAll("%USERNAME%", username).replaceAll("%ELF_ID%", elfID);
      HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(url));
      if(responce.success && !StringUtils.isEmpty(responce.content)) {
         BasicResponce r = (BasicResponce)CustomSkinLoader.GSON.fromJson(responce.content, BasicResponce.class);
         if(r.error != 0) {
            CustomSkinLoader.logger.info("Error " + r.error + ": " + r.msg);
            return null;
         } else if(r.players != null && r.players.length != 0) {
            return r.players;
         } else {
            CustomSkinLoader.logger.info("No Profile found.");
            return null;
         }
      } else {
         CustomSkinLoader.logger.info("Request failed.");
         return null;
      }
   }

   public static void makeLogout(String ip, String username) {
      String url = "http://status.mcelf.com/logout?gid=%SERVER_IP%&name=%USERNAME%".replaceAll("%SERVER_IP%", ip).replaceAll("%USERNAME%", username);
      HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest((new HttpRequestUtil.HttpRequest(url)).setCacheTime(-1));
   }

   public static Profile getProfile(String ip, String username) {
      String url = "http://status.mcelf.com/s?gid=%SERVER_IP%&name=%USERNAME%".replaceAll("%SERVER_IP%", ip).replaceAll("%USERNAME%", username);
      HttpRequestUtil.HttpResponce responce = HttpRequestUtil.makeHttpRequest(new HttpRequestUtil.HttpRequest(url));
      if(responce.success && !StringUtils.isEmpty(responce.content)) {
         BasicResponce r = (BasicResponce)CustomSkinLoader.GSON.fromJson(responce.content, BasicResponce.class);
         if(r.error != 0) {
            CustomSkinLoader.logger.info("Error " + r.error + ": " + r.msg);
            return null;
         } else if(r.players != null && r.players.length != 0) {
            return r.players[0];
         } else {
            CustomSkinLoader.logger.info("Profile not found.");
            return null;
         }
      } else {
         CustomSkinLoader.logger.info("Request failed.");
         return null;
      }
   }

   public boolean compare(SkinSiteProfile ssp0, SkinSiteProfile ssp1) {
      return true;
   }

   public String getName() {
      return "ElfSkin";
   }

   public void initLocalFolder(SkinSiteProfile ssp) {}

   private static class Profile {

      String name;
      String login_time;
      String skin;
      String cape;


   }

   private static class BasicResponce {

      int error;
      String msg;
      String gid;
      String expired_at;
      Profile[] players;


   }
}
