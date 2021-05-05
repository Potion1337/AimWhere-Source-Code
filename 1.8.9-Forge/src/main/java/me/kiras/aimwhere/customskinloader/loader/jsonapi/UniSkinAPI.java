package me.kiras.aimwhere.customskinloader.loader.jsonapi;

import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.JsonAPILoader;
import me.kiras.aimwhere.customskinloader.profile.ModelManager0;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.HttpTextureUtil;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class UniSkinAPI implements JsonAPILoader.IJsonAPI {

   private static final String TEXTURES = "textures/";
   private static final String SUFFIX = ".json";


   public String toJsonUrl(String root, String username) {
      return root + username + ".json";
   }

   public UserProfile toUserProfile(String root, String json, boolean local) {
      UniSkinAPIProfile profile = (UniSkinAPIProfile)CustomSkinLoader.GSON.fromJson(json, UniSkinAPIProfile.class);
      UserProfile p = new UserProfile();
      if(StringUtils.isNotBlank(profile.cape)) {
         p.capeUrl = root + "textures/" + profile.cape;
         if(local) {
            p.capeUrl = HttpTextureUtil.getLocalFakeUrl(p.capeUrl);
         }
      }

      if(profile.skins != null && !profile.skins.isEmpty()) {
         boolean hasSkin = false;
         Iterator var8 = profile.model_preference.iterator();

         while(var8.hasNext()) {
            String model = (String)var8.next();
            ModelManager0.Model enumModel = ModelManager0.getEnumModel(model);
            if(enumModel != null && !StringUtils.isEmpty((CharSequence)profile.skins.get(model))) {
               if(ModelManager0.isSkin(enumModel)) {
                  if(hasSkin) {
                     continue;
                  }

                  hasSkin = true;
               }

               String url = root + "textures/" + (String)profile.skins.get(model);
               if(local) {
                  url = HttpTextureUtil.getLocalFakeUrl(url);
               }

               p.put(enumModel, url);
            }
         }

         return p;
      } else {
         return p;
      }
   }

   public String getPayload(SkinSiteProfile ssp) {
      return null;
   }

   public String getName() {
      return "UniSkinAPI";
   }

   private class UniSkinAPIProfile {

      public String player_name;
      public long last_update;
      public List<String> model_preference;
      public Map<String, String> skins;
      public String cape;


   }
}
