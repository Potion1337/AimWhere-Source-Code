package me.kiras.aimwhere.customskinloader.config;

import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import me.kiras.aimwhere.customskinloader.loader.ProfileLoader;
import me.kiras.aimwhere.customskinloader.utils.HttpRequestUtil;
import me.kiras.aimwhere.customskinloader.utils.HttpTextureUtil;
import me.kiras.aimwhere.customskinloader.utils.HttpUtil0;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;

public class Config {

   public String version = "14.6";
   public boolean enable = true;
   public List<SkinSiteProfile> loadlist;
   public boolean enableSkull = true;
   public boolean enableDynamicSkull = true;
   public boolean enableTransparentSkin = true;
   public boolean ignoreHttpsCertificate = false;
   public int cacheExpiry = 10;
   public boolean enableUpdateSkull = false;
   public boolean enableLocalProfileCache = false;
   public boolean enableCacheAutoClean = false;


   public Config(SkinSiteProfile[] loadlist) {
      this.loadlist = Arrays.asList(loadlist);
   }

   public static Config loadConfig0() {
      Config config = loadConfig();
      if(config.loadlist == null) {
         config.loadlist = new ArrayList();
      } else {
         for(int floatVersion = 0; floatVersion < config.loadlist.size(); ++floatVersion) {
            if(config.loadlist.get(floatVersion) == null) {
               config.loadlist.remove(floatVersion--);
            }
         }
      }

      config.loadExtraList();
      config.initLocalFolder();
      if(config.ignoreHttpsCertificate) {
         HttpUtil0.ignoreHttpsCertificate();
      }

      if(config.enableCacheAutoClean && !config.enableLocalProfileCache) {
         HttpRequestUtil.CACHE_DIR.delete();
         HttpTextureUtil.cleanCacheDir();
      }

      CustomSkinLoader.logger.info("Enable:" + config.enable + ", EnableSkull:" + config.enableSkull + ", EnableDynamicSkull:" + config.enableDynamicSkull + ", EnableTranSkin:" + config.enableTransparentSkin + ", IgnoreHttpsCertificate:" + config.ignoreHttpsCertificate + ", CacheExpiry:" + config.cacheExpiry + ", EnableUpdateSkull:" + config.enableUpdateSkull + ", EnableLocalProfileCache:" + config.enableLocalProfileCache + ", EnableCacheAutoClean:" + config.enableCacheAutoClean + ", LoadList:" + (config.loadlist == null?0:config.loadlist.size()));
      float var5 = 0.0F;
      float configVersion = 0.0F;

      try {
         var5 = Float.parseFloat("14.6");
         configVersion = Float.parseFloat(config.version);
      } catch (Exception var4) {
         CustomSkinLoader.logger.warning("Exception occurs while parsing version: " + var4.toString());
      }

      if(config.version == null || configVersion == 0.0F || var5 > configVersion) {
         CustomSkinLoader.logger.info("Config File is out of date: " + config.version);
         config.version = "14.6";
         writeConfig(config, true);
      }

      return config;
   }

   private static Config loadConfig() {
      CustomSkinLoader.logger.info("Config File: " + CustomSkinLoader.CONFIG_FILE.getAbsolutePath());
      if(!CustomSkinLoader.CONFIG_FILE.exists()) {
         CustomSkinLoader.logger.info("Config file not found, use default instead.");
         return initConfig();
      } else {
         try {
            CustomSkinLoader.logger.info("Try to load config.");
            String e = FileUtils.readFileToString(CustomSkinLoader.CONFIG_FILE, Charsets.UTF_8);
            Config brokenFile1 = (Config)CustomSkinLoader.GSON.fromJson(e, Config.class);
            CustomSkinLoader.logger.info("Successfully load config.");
            return brokenFile1;
         } catch (Exception var2) {
            CustomSkinLoader.logger.info("Failed to load config, use default instead.(" + var2.toString() + ")");
            File brokenFile = new File(CustomSkinLoader.DATA_DIR, "BROKEN-CustomSkinLoader.json");
            if(brokenFile.exists()) {
               brokenFile.delete();
            }

            CustomSkinLoader.CONFIG_FILE.renameTo(brokenFile);
            return initConfig();
         }
      }
   }

   private void loadExtraList() {
      File listAddition = new File(CustomSkinLoader.DATA_DIR, "ExtraList");
      if(!listAddition.isDirectory()) {
         listAddition.mkdirs();
      } else {
         ArrayList adds = new ArrayList();
         File[] files = listAddition.listFiles();
         File[] var7 = files;
         int var6 = files.length;

         for(int var5 = 0; var5 < var6; ++var5) {
            File file = var7[var5];
            if(file.getName().toLowerCase().endsWith(".json") || file.getName().toLowerCase().endsWith(".txt")) {
               try {
                  CustomSkinLoader.logger.info("Try to load Extra List.(" + file.getName() + ")");
                  String e = FileUtils.readFileToString(file, Charsets.UTF_8);
                  SkinSiteProfile ssp = (SkinSiteProfile)CustomSkinLoader.GSON.fromJson(e, SkinSiteProfile.class);
                  CustomSkinLoader.logger.info("Successfully load Extra List.");
                  file.delete();
                  ProfileLoader.IProfileLoader loader = (ProfileLoader.IProfileLoader)ProfileLoader.LOADERS.get(ssp.type.toLowerCase());
                  if(loader == null) {
                     CustomSkinLoader.logger.info("Extra List will be ignore: Type \'" + ssp.type + "\' is not defined.");
                  } else {
                     boolean duplicate = false;
                     Iterator var13 = this.loadlist.iterator();

                     while(var13.hasNext()) {
                        SkinSiteProfile ssp0 = (SkinSiteProfile)var13.next();
                        if(ssp0.type.equalsIgnoreCase(ssp.type) && loader.compare(ssp0, ssp)) {
                           duplicate = true;
                           break;
                        }
                     }

                     if(!duplicate) {
                        adds.add(ssp);
                        CustomSkinLoader.logger.info("Successfully apply Extra List.(" + ssp.name + ")");
                     } else {
                        CustomSkinLoader.logger.info("Extra List will be ignore: Duplicate.(" + ssp.name + ")");
                     }
                  }
               } catch (Exception var14) {
                  CustomSkinLoader.logger.info("Failed to load Extra List.(" + var14.toString() + ")");
               }
            }
         }

         if(adds.size() != 0) {
            adds.addAll(this.loadlist);
            this.loadlist = adds;
            writeConfig(this, true);
         }

      }
   }

   private void initLocalFolder() {
      Iterator var2 = this.loadlist.iterator();

      while(var2.hasNext()) {
         SkinSiteProfile ssp = (SkinSiteProfile)var2.next();
         ProfileLoader.IProfileLoader loader = (ProfileLoader.IProfileLoader)ProfileLoader.LOADERS.get(ssp.type.toLowerCase());
         if(loader != null) {
            loader.initLocalFolder(ssp);
         }
      }

   }

   private static Config initConfig() {
      Config config = new Config(CustomSkinLoader.DEFAULT_LOAD_LIST);
      writeConfig(config, false);
      return config;
   }

   private static void writeConfig(Config config, boolean update) {
      String json = CustomSkinLoader.GSON.toJson(config);
      if(CustomSkinLoader.CONFIG_FILE.exists()) {
         CustomSkinLoader.CONFIG_FILE.delete();
      }

      try {
         CustomSkinLoader.CONFIG_FILE.createNewFile();
         FileUtils.write(CustomSkinLoader.CONFIG_FILE, json, Charsets.UTF_8);
         CustomSkinLoader.logger.info("Successfully " + (update?"update":"create") + " config.");
      } catch (Exception var4) {
         CustomSkinLoader.logger.info("Failed to " + (update?"update":"create") + " config.(" + var4.toString() + ")");
      }

   }
}
