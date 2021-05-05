package me.kiras.aimwhere.customskinloader.loader;

import com.mojang.authlib.GameProfile;
import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.ProfileLoader;
import me.kiras.aimwhere.customskinloader.loader.jsonapi.CustomSkinAPI;
import me.kiras.aimwhere.customskinloader.loader.jsonapi.CustomSkinAPIPlus;
import me.kiras.aimwhere.customskinloader.loader.jsonapi.UniSkinAPI;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.HttpRequestUtil;
import me.kiras.aimwhere.customskinloader.utils.HttpUtil0;
import java.io.File;
import java.io.FileInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class JsonAPILoader implements ProfileLoader.IProfileLoader {

   private Type type;


   public JsonAPILoader(Type type) {
      this.type = type;
   }

   public UserProfile loadProfile(SkinSiteProfile ssp, GameProfile gameProfile) throws Exception {
      String username = gameProfile.getName();
      boolean local = HttpUtil0.isLocal(ssp.root);
      if(ssp.root != null && !ssp.root.equals("")) {
         String jsonUrl = this.type.jsonAPI.toJsonUrl(ssp.root, username);
         String json;
         if(local) {
            File profile2 = new File(jsonUrl);
            if(!profile2.exists()) {
               CustomSkinLoader.logger.info("Profile File not found.");
               return null;
            }

            json = IOUtils.toString(new FileInputStream(profile2));
         } else {
            HttpRequestUtil.HttpResponce profile21 = HttpRequestUtil.makeHttpRequest((new HttpRequestUtil.HttpRequest(jsonUrl)).setCacheTime(60).setUserAgent(ssp.userAgent).setPayload(this.type.jsonAPI.getPayload(ssp)));
            json = profile21.content;
         }

         if(json != null && !json.equals("")) {
            ErrorProfile profile22 = (ErrorProfile)CustomSkinLoader.GSON.fromJson(json, ErrorProfile.class);
            if(profile22.errno != 0) {
               CustomSkinLoader.logger.info("Error " + profile22.errno + ": " + profile22.msg);
               return null;
            } else {
               UserProfile p = this.type.jsonAPI.toUserProfile(ssp.root, json, local);
               if(p != null && !p.isEmpty()) {
                  return p;
               } else {
                  CustomSkinLoader.logger.info("Both skin and cape not found.");
                  return null;
               }
            }
         } else {
            CustomSkinLoader.logger.info("Profile not found.");
            return null;
         }
      } else {
         CustomSkinLoader.logger.info("Root not defined.");
         return null;
      }
   }

   public boolean compare(SkinSiteProfile ssp0, SkinSiteProfile ssp1) {
      return StringUtils.isNoneEmpty(new CharSequence[]{ssp0.root})?ssp0.root.equalsIgnoreCase(ssp1.root):true;
   }

   public String getName() {
      return this.type.jsonAPI.getName();
   }

   public void initLocalFolder(SkinSiteProfile ssp) {
      if(HttpUtil0.isLocal(ssp.root)) {
         File f = new File(ssp.root);
         if(!f.exists()) {
            f.mkdirs();
         }
      }

   }

   public class ErrorProfile {

      public int errno;
      public String msg;


   }

   public interface IJsonAPI {

      String toJsonUrl(String var1, String var2);

      String getPayload(SkinSiteProfile var1);

      UserProfile toUserProfile(String var1, String var2, boolean var3);

      String getName();
   }

   public static enum Type {

      CustomSkinAPI("CustomSkinAPI", 0, new CustomSkinAPI()),
      CustomSkinAPIPlus("CustomSkinAPIPlus", 1, new CustomSkinAPIPlus()),
      UniSkinAPI("UniSkinAPI", 2, new UniSkinAPI());
      public IJsonAPI jsonAPI;
      // $FF: synthetic field
      private static final Type[] ENUM$VALUES = new Type[]{CustomSkinAPI, CustomSkinAPIPlus, UniSkinAPI};


      private Type(String var1, int var2, IJsonAPI jsonAPI) {
         this.jsonAPI = jsonAPI;
      }
   }
}
