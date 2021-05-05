package me.kiras.aimwhere.customskinloader;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import me.kiras.aimwhere.customskinloader.Logger;
import me.kiras.aimwhere.customskinloader.config.Config;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.ProfileLoader;
import me.kiras.aimwhere.customskinloader.profile.DynamicSkullManager;
import me.kiras.aimwhere.customskinloader.profile.ModelManager0;
import me.kiras.aimwhere.customskinloader.profile.ProfileCache;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import me.kiras.aimwhere.customskinloader.utils.MinecraftUtil;
import java.io.File;
import java.util.Map;

public class CustomSkinLoader {

   public static final String CustomSkinLoader_VERSION = "14.6";
   public static final String CustomSkinLoader_FULL_VERSION = "14.6a";
   public static final File DATA_DIR = new File(MinecraftUtil.getMinecraftDataDir0(), "CustomSkinLoader");
   public static final File LOG_FILE = new File(DATA_DIR, "CustomSkinLoader.log");
   public static final File CONFIG_FILE = new File(DATA_DIR, "CustomSkinLoader.json");
   public static final SkinSiteProfile[] DEFAULT_LOAD_LIST = new SkinSiteProfile[]{SkinSiteProfile.createMojangAPI("Mojang"), SkinSiteProfile.createCustomSkinAPI("BlessingSkin", "http://skin.prinzeugen.net/"), SkinSiteProfile.createCustomSkinAPI("OneSkin", "http://fleey.org/skin/skin_user/skin_json.php/"), SkinSiteProfile.createUniSkinAPI("SkinMe", "http://www.skinme.cc/uniskin/"), SkinSiteProfile.createLegacy("LocalSkin", "LocalSkin/skins/{USERNAME}.png", "LocalSkin/capes/{USERNAME}.png", "LocalSkin/elytras/{USERNAME}.png")};
   public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   public static final Logger logger = initLogger();
   public static final Config config = Config.loadConfig0();
   private static final ProfileCache profileCache = new ProfileCache();
   private static final DynamicSkullManager dynamicSkullManager = new DynamicSkullManager();


   public static Map loadProfile(GameProfile gameProfile) {
      String username = gameProfile.getName();
      if(username == null) {
         logger.warning("Could not load profile: username is null.");
         return Maps.newHashMap();
      } else {
         String tempName = Thread.currentThread().getName();
         Thread.currentThread().setName(username);
         UserProfile profile = null;
         if(profileCache.isReady(username)) {
            logger.info("Cached profile will be used.");
            profile = profileCache.getProfile(username);
            if(profile == null) {
               logger.warning("(!Cached Profile is empty!) Expiry:" + profileCache.getExpiry(username));
               if(profileCache.isExpired(username)) {
                  profile = loadProfile0(gameProfile);
               }
            } else {
               logger.info(profile.toString(profileCache.getExpiry(username)));
            }
         } else {
            profileCache.setLoading(username, true);
            profile = loadProfile0(gameProfile);
         }

         Thread.currentThread().setName(tempName);
         return ModelManager0.fromUserProfile(profile);
      }
   }

   public static UserProfile loadProfile0(GameProfile gameProfile) {
      String username = gameProfile.getName();
      profileCache.setLoading(username, true);
      logger.info("Loading " + username + "\'s profile.");
      if(config.loadlist != null && !config.loadlist.isEmpty()) {
         for(int profile = 0; profile < config.loadlist.size(); ++profile) {
            SkinSiteProfile ssp = (SkinSiteProfile)config.loadlist.get(profile);
            logger.info(profile + 1 + "/" + config.loadlist.size() + " Try to load profile from \'" + ssp.name + "\'.");
            ProfileLoader.IProfileLoader loader = (ProfileLoader.IProfileLoader)ProfileLoader.LOADERS.get(ssp.type.toLowerCase());
            if(loader == null) {
               logger.info("Type \'" + ssp.type + "\' is not defined.");
            } else {
               UserProfile profile1 = null;

               try {
                  profile1 = loader.loadProfile(ssp, gameProfile);
               } catch (Exception var7) {
                  logger.warning("Exception occurs while loading.");
                  logger.warning(var7);
               }

               if(profile1 != null) {
                  logger.info(username + "\'s profile loaded.");
                  profileCache.updateCache(username, profile1);
                  profileCache.setLoading(username, false);
                  logger.info(profile1.toString(profileCache.getExpiry(username)));
                  return profile1;
               }
            }
         }

         logger.info(username + "\'s profile not found in load list.");
         if(config.enableLocalProfileCache) {
            UserProfile var8 = profileCache.getLocalProfile(username);
            if(var8 != null) {
               profileCache.updateCache(username, var8, false);
               profileCache.setLoading(username, false);
               logger.info(username + "\'s LocalProfile will be used.");
               logger.info(var8.toString(profileCache.getExpiry(username)));
               return var8;
            }

            logger.info(username + "\'s LocalProfile not found.");
         }

         profileCache.setLoading(username, false);
         return null;
      } else {
         logger.info("LoadList is Empty.");
         return null;
      }
   }

   public static Map<Type, MinecraftProfileTexture> loadProfileFromCache(final GameProfile gameProfile) {
      String username = gameProfile.getName();
      if(username == null) {
         return dynamicSkullManager.getTexture(gameProfile);
      } else {
         label18: {
            if(config.enableUpdateSkull) {
               if(profileCache.isReady(username)) {
                  break label18;
               }
            } else if(profileCache.isExist(username)) {
               break label18;
            }

            Thread loadThread = new Thread() {
               public void run() {
                  CustomSkinLoader.loadProfile0(gameProfile);
               }
            };
            loadThread.setName(username + "\'s skull");
            loadThread.start();
            return Maps.newHashMap();
         }

         UserProfile loadThread1 = profileCache.getProfile(username);
         return ModelManager0.fromUserProfile(loadThread1);
      }
   }

   private static Logger initLogger() {
      Logger logger = new Logger(LOG_FILE);
      logger.info("CustomSkinLoader 14.6a");
      logger.info("DataDir: " + DATA_DIR.getAbsolutePath());
      logger.info("Minecraft: " + MinecraftUtil.getMinecraftMainVersion() + "(" + MinecraftUtil.getMinecraftVersionText() + ")");
      return logger;
   }
}
