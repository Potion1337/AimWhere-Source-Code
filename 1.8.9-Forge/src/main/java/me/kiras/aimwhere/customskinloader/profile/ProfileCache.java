package me.kiras.aimwhere.customskinloader.profile;

import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.profile.CachedProfile;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.TimeUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import org.apache.commons.io.IOUtils;

public class ProfileCache {

   public static File PROFILE_CACHE_DIR = new File(CustomSkinLoader.DATA_DIR, "ProfileCache");
   private HashMap<String, CachedProfile> cachedProfiles = new HashMap();
   private HashMap<String, UserProfile> localProfiles = new HashMap();


   public ProfileCache() {
      if(!PROFILE_CACHE_DIR.exists()) {
         PROFILE_CACHE_DIR.mkdir();
      }

   }

   public boolean isExist(String username) {
      return this.cachedProfiles.containsKey(username.toLowerCase());
   }

   public boolean isReady(String username) {
      CachedProfile cp = (CachedProfile)this.cachedProfiles.get(username.toLowerCase());
      return cp == null?false:cp.loading || cp.expiryTime > TimeUtil.getCurrentUnixTimestamp();
   }

   public boolean isExpired(String username) {
      CachedProfile cp = (CachedProfile)this.cachedProfiles.get(username.toLowerCase());
      return cp == null?true:cp.expiryTime <= TimeUtil.getCurrentUnixTimestamp();
   }

   public UserProfile getProfile(String username) {
      return this.getCachedProfile(username).profile;
   }

   public long getExpiry(String username) {
      return this.getCachedProfile(username).expiryTime;
   }

   public UserProfile getLocalProfile(String username) {
      return this.localProfiles.containsKey(username.toLowerCase())?(UserProfile)this.localProfiles.get(username.toLowerCase()):this.loadLocalProfile(username);
   }

   public void setLoading(String username, boolean loading) {
      this.getCachedProfile(username).loading = loading;
   }

   public void updateCache(String username, UserProfile profile) {
      this.updateCache(username, profile, CustomSkinLoader.config.enableLocalProfileCache);
   }

   public void updateCache(String username, UserProfile profile, boolean saveLocalProfile) {
      CachedProfile cp = this.getCachedProfile(username);
      cp.profile = profile;
      cp.expiryTime = TimeUtil.getUnixTimestampRandomDelay((long)CustomSkinLoader.config.cacheExpiry);
      if(saveLocalProfile) {
         this.saveLocalProfile(username, profile);
      }
   }

   private CachedProfile getCachedProfile(String username) {
      CachedProfile cp = (CachedProfile)this.cachedProfiles.get(username.toLowerCase());
      if(cp != null) {
         return cp;
      } else {
         cp = new CachedProfile();
         this.cachedProfiles.put(username.toLowerCase(), cp);
         return cp;
      }
   }

   private UserProfile loadLocalProfile(String username) {
      File localProfile = new File(PROFILE_CACHE_DIR, username.toLowerCase() + ".json");
      if(!localProfile.exists()) {
         this.localProfiles.put(username.toLowerCase(), null);
      }

      try {
         String e = IOUtils.toString(new FileInputStream(localProfile));
         UserProfile profile = (UserProfile)CustomSkinLoader.GSON.fromJson(e, UserProfile.class);
         this.localProfiles.put(username.toLowerCase(), profile);
         CustomSkinLoader.logger.info("Successfully load LocalProfile.");
         return profile;
      } catch (Exception var5) {
         CustomSkinLoader.logger.info("Failed to load LocalProfile.(" + var5.toString() + ")");
         this.localProfiles.put(username.toLowerCase(), null);
         return null;
      }
   }

   private void saveLocalProfile(String username, UserProfile profile) {
      String json = CustomSkinLoader.GSON.toJson(profile);
      File localProfile = new File(PROFILE_CACHE_DIR, username.toLowerCase() + ".json");
      if(localProfile.exists()) {
         localProfile.delete();
      }

      try {
         localProfile.createNewFile();
         IOUtils.write(json, new FileOutputStream(localProfile));
         CustomSkinLoader.logger.info("Successfully save LocalProfile.");
      } catch (Exception var6) {
         CustomSkinLoader.logger.info("Failed to save LocalProfile.(" + var6.toString() + ")");
      }

   }
}
