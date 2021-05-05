package me.kiras.aimwhere.customskinloader.utils;

import me.kiras.aimwhere.customskinloader.CustomSkinLoader;
import java.io.File;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

public class HttpTextureUtil {

   private static final String LEGACY_MARK = "(LEGACY)";
   private static final String LOCAL_MARK = "(LOCAL)";
   private static final String LOCAL_LEGACY_MARK = "(LOCAL_LEGACY)";
   public static File defaultCacheDir;


   public static void cleanCacheDir() {
      if(defaultCacheDir != null) {
         defaultCacheDir.delete();
         defaultCacheDir.mkdirs();
      }

   }

   public static HttpTextureInfo toHttpTextureInfo(String fakeUrl) {
      HttpTextureInfo info = new HttpTextureInfo();
      if(fakeUrl.startsWith("http")) {
         info.url = fakeUrl;
         info.hash = FilenameUtils.getBaseName(fakeUrl);
         info.cacheFile = getCacheFile(info.hash);
         return info;
      } else if(fakeUrl.startsWith("(LOCAL_LEGACY)")) {
         fakeUrl = fakeUrl.replace("(LOCAL_LEGACY)", "");
         String[] t = fakeUrl.split(",", 2);
         if(t.length != 2) {
            return info;
         } else {
            info.cacheFile = new File(CustomSkinLoader.DATA_DIR, t[1]);
            info.hash = t[0];
            return info;
         }
      } else if(fakeUrl.startsWith("(LOCAL)")) {
         fakeUrl = fakeUrl.replace("(LOCAL)", "");
         info.cacheFile = new File(CustomSkinLoader.DATA_DIR, fakeUrl);
         info.hash = FilenameUtils.getBaseName(fakeUrl);
         return info;
      } else if(fakeUrl.startsWith("(LEGACY)")) {
         fakeUrl = fakeUrl.replace("(LEGACY)", "");
         info.url = fakeUrl;
         info.hash = DigestUtils.sha1Hex(info.url);
         info.cacheFile = getCacheFile(info.hash);
         return info;
      } else {
         return info;
      }
   }

   public static String getLegacyFakeUrl(String url) {
      return "(LEGACY)" + url;
   }

   public static String getLocalFakeUrl(String path) {
      return "(LOCAL)" + path;
   }

   public static String getLocalLegacyFakeUrl(String path, String hash) {
      return "(LOCAL_LEGACY)" + hash + "," + path;
   }

   public static String getHash(String url, long size, long lastModified) {
      return DigestUtils.sha1Hex(size + url + lastModified);
   }

   public static File getCacheFile(String hash) {
      return getCacheFile(defaultCacheDir, hash);
   }

   public static File getCacheFile(File cacheDir, String hash) {
      return new File(new File(cacheDir, hash.length() > 2?hash.substring(0, 2):"xx"), hash);
   }

   public static class HttpTextureInfo {

      public String url = "";
      public File cacheFile;
      public String hash;


   }
}
