package me.kiras.aimwhere.customskinloader.loader.jsonapi;

import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.JsonAPILoader;
import me.kiras.aimwhere.customskinloader.profile.ModelManager0;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.HttpTextureUtil;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class CustomSkinAPI implements JsonAPILoader.IJsonAPI {

   private static final String TEXTURES = "textures/";
   private static final String SUFFIX = ".json";


   public String toJsonUrl(String root, String username) {
      return root + username + ".json";
   }

   public UserProfile toUserProfile(String root, String json, boolean local) {
      CustomSkinAPIProfile profile = (CustomSkinAPIProfile)CustomSkinLoader.GSON.fromJson(json, CustomSkinAPIProfile.class);
      UserProfile p = new UserProfile();
      if(StringUtils.isNotBlank(profile.skin)) {
         p.skinUrl = root + "textures/" + profile.skin;
         if(local) {
            p.skinUrl = HttpTextureUtil.getLocalFakeUrl(p.skinUrl);
         }
      }

      if(StringUtils.isNotBlank(profile.cape)) {
         p.capeUrl = root + "textures/" + profile.cape;
         if(local) {
            p.capeUrl = HttpTextureUtil.getLocalFakeUrl(p.capeUrl);
         }
      }

      if(StringUtils.isNotBlank(profile.elytra)) {
         p.elytraUrl = root + "textures/" + profile.elytra;
         if(local) {
            p.elytraUrl = HttpTextureUtil.getLocalFakeUrl(p.elytraUrl);
         }
      }

      LinkedHashMap textures = new LinkedHashMap();
      if(profile.skins != null) {
         textures.putAll(profile.skins);
      }

      if(profile.textures != null) {
         textures.putAll(profile.textures);
      }

      if(textures.isEmpty()) {
         return p;
      } else {
         boolean hasSkin = false;
         Iterator var9 = textures.keySet().iterator();

         while(var9.hasNext()) {
            String model = (String)var9.next();
            ModelManager0.Model enumModel = ModelManager0.getEnumModel(model);
            if(enumModel != null && !StringUtils.isEmpty((CharSequence)textures.get(model))) {
               if(ModelManager0.isSkin(enumModel)) {
                  if(hasSkin) {
                     continue;
                  }

                  hasSkin = true;
               }

               String url = root + "textures/" + (String)textures.get(model);
               if(local) {
                  url = HttpTextureUtil.getLocalFakeUrl(url);
               }

               p.put(enumModel, url);
            }
         }

         return p;
      }
   }

   public String getPayload(SkinSiteProfile ssp) {
      return null;
   }

   public String getName() {
      return "CustomSkinAPI";
   }

   private class CustomSkinAPIProfile {

      public String username;
      public Map<String, String> textures;
      public Map<String, String> skins;
      public String skin;
      public String cape;
      public String elytra;


   }
}
