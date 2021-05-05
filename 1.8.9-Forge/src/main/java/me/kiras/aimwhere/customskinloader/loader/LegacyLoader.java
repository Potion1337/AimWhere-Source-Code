package me.kiras.aimwhere.customskinloader.loader;

import com.mojang.authlib.GameProfile;
import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.ProfileLoader;
import me.kiras.aimwhere.customskinloader.profile.ModelManager0;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.HttpRequestUtil;
import me.kiras.aimwhere.customskinloader.utils.HttpTextureUtil;
import me.kiras.aimwhere.customskinloader.utils.HttpUtil0;
import java.io.File;
import org.apache.commons.lang3.StringUtils;

public class LegacyLoader implements ProfileLoader.IProfileLoader {

   public static final String USERNAME_REGEX = "\\{USERNAME\\}";


   public UserProfile loadProfile(SkinSiteProfile ssp, GameProfile gameProfile) throws Exception {
      String username = gameProfile.getName();
      UserProfile profile = new UserProfile();
      String elytra;
      File responce;
      HttpRequestUtil.HttpResponce responce1;
      if(StringUtils.isNoneEmpty(new CharSequence[]{ssp.skin})) {
         elytra = ssp.skin.replaceAll("\\{USERNAME\\}", username);
         if(HttpUtil0.isLocal(ssp.skin)) {
            responce = new File(CustomSkinLoader.DATA_DIR, elytra);
            if(responce.exists() && responce.isFile()) {
               profile.skinUrl = HttpTextureUtil.getLocalLegacyFakeUrl(elytra, HttpTextureUtil.getHash(elytra, responce.length(), responce.lastModified()));
            }
         } else {
            responce1 = HttpRequestUtil.makeHttpRequest((new HttpRequestUtil.HttpRequest(elytra)).setUserAgent(ssp.userAgent).setCheckPNG(ssp.checkPNG != null && ssp.checkPNG.booleanValue()).setLoadContent(false).setCacheTime(60));
            if(responce1.success) {
               profile.skinUrl = HttpTextureUtil.getLegacyFakeUrl(elytra);
            }
         }

         profile.model = profile.hasSkinUrl()?ssp.model:null;
      }

      if(StringUtils.isNoneEmpty(new CharSequence[]{ssp.cape})) {
         elytra = ssp.cape.replaceAll("\\{USERNAME\\}", username);
         if(HttpUtil0.isLocal(ssp.cape)) {
            responce = new File(CustomSkinLoader.DATA_DIR, elytra);
            if(responce.exists() && responce.isFile()) {
               profile.capeUrl = HttpTextureUtil.getLocalLegacyFakeUrl(elytra, HttpTextureUtil.getHash(elytra, responce.length(), responce.lastModified()));
            }
         } else {
            responce1 = HttpRequestUtil.makeHttpRequest((new HttpRequestUtil.HttpRequest(elytra)).setUserAgent(ssp.userAgent).setCheckPNG(ssp.checkPNG != null && ssp.checkPNG.booleanValue()).setLoadContent(false).setCacheTime(60));
            if(responce1.success) {
               profile.capeUrl = HttpTextureUtil.getLegacyFakeUrl(elytra);
            }
         }
      }

      if(ModelManager0.isElytraSupported() && StringUtils.isNoneEmpty(new CharSequence[]{ssp.elytra})) {
         elytra = ssp.elytra.replaceAll("\\{USERNAME\\}", username);
         if(HttpUtil0.isLocal(ssp.elytra)) {
            responce = new File(CustomSkinLoader.DATA_DIR, elytra);
            if(responce.exists() && responce.isFile()) {
               profile.elytraUrl = HttpTextureUtil.getLocalLegacyFakeUrl(elytra, HttpTextureUtil.getHash(elytra, responce.length(), responce.lastModified()));
            }
         } else {
            responce1 = HttpRequestUtil.makeHttpRequest((new HttpRequestUtil.HttpRequest(elytra)).setUserAgent(ssp.userAgent).setCheckPNG(ssp.checkPNG != null && ssp.checkPNG.booleanValue()).setLoadContent(false).setCacheTime(60));
            if(responce1.success) {
               profile.elytraUrl = HttpTextureUtil.getLegacyFakeUrl(elytra);
            }
         }
      }

      if(profile.isEmpty()) {
         CustomSkinLoader.logger.info("Both skin and cape not found.");
         return null;
      } else {
         return profile;
      }
   }

   public boolean compare(SkinSiteProfile ssp0, SkinSiteProfile ssp1) {
      return !StringUtils.isNoneEmpty(new CharSequence[]{ssp0.skin}) || ssp0.skin.equalsIgnoreCase(ssp1.skin) || !StringUtils.isNoneEmpty(new CharSequence[]{ssp0.cape}) || ssp0.cape.equalsIgnoreCase(ssp1.cape);
   }

   public String getName() {
      return "Legacy";
   }

   public void initLocalFolder(SkinSiteProfile ssp) {
      if(HttpUtil0.isLocal(ssp.skin)) {
         this.initFolder(ssp.skin);
      }

      if(HttpUtil0.isLocal(ssp.cape)) {
         this.initFolder(ssp.cape);
      }

      if(HttpUtil0.isLocal(ssp.elytra)) {
         this.initFolder(ssp.elytra);
      }

   }

   private void initFolder(String target) {
      String file = target.replaceAll("\\{USERNAME\\}", "init");
      File folder = (new File(CustomSkinLoader.DATA_DIR, file)).getParentFile();
      if(folder != null && !folder.exists()) {
         folder.mkdirs();
      }

   }
}
