package me.kiras.aimwhere.customskinloader.utils;

import me.kiras.aimwhere.customskinloader.utils.HttpUtil0;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.commons.lang3.StringUtils;

public class MinecraftUtil {

   public static File minecraftDataFolder = null;
   private static ArrayList<String> minecraftVersion = new ArrayList();
   private static String minecraftMainVersion = null;
   private static final Pattern MINECRAFT_VERSION_PATTERN = Pattern.compile(".*?(\\d+\\.\\d+[\\.]?\\d*).*?");
   private static final Pattern MINECRAFT_CORE_FILE_PATTERN = Pattern.compile("^(.*?)/versions/([^\\/\\\\]*?)/([^\\/\\\\]*?).jar$");
   private static final Pattern LIBRARY_FILE_PATTERN = Pattern.compile("^(.*?)/libraries/(.*?)/([^\\/\\\\]*?).jar$");


   public static File getMinecraftDataDir0() {
      return Minecraft.getMinecraft().mcDataDir;
   }

   public static File getMinecraftDataDir() {
      if(minecraftDataFolder != null) {
         return minecraftDataFolder;
      } else {
         testProbe();
         return minecraftDataFolder != null?minecraftDataFolder:new File("");
      }
   }

   public static ArrayList<String> getMinecraftVersions() {
      if(minecraftVersion != null && !minecraftVersion.isEmpty()) {
         return minecraftVersion;
      } else {
         testProbe();
         return minecraftVersion;
      }
   }

   public static String getMinecraftVersionText() {
      StringBuilder sb = new StringBuilder();
      Iterator var2 = getMinecraftVersions().iterator();

      while(var2.hasNext()) {
         String version = (String)var2.next();
         sb.append(version).append(" ");
      }

      return StringUtils.trim(sb.toString());
   }

   public static String getMinecraftMainVersion() {
      if(minecraftMainVersion != null) {
         return minecraftMainVersion;
      } else {
         Iterator var1 = getMinecraftVersions().iterator();

         while(var1.hasNext()) {
            String version = (String)var1.next();
            Matcher m = null;

            try {
               m = MINECRAFT_VERSION_PATTERN.matcher(version);
            } catch (Exception var4) {
               var4.printStackTrace();
            }

            if(m != null && m.matches()) {
               minecraftMainVersion = m.group(m.groupCount());
            }
         }

         return minecraftMainVersion;
      }
   }

   public static String getServerAddress() {
      ServerData data = Minecraft.getMinecraft().getCurrentServerData();
      return data == null?null:data.serverIP;
   }

   public static String getStandardServerAddress() {
      return HttpUtil0.parseAddress(getServerAddress());
   }

   public static boolean isLanServer() {
      return HttpUtil0.isLanServer(getStandardServerAddress());
   }

   public static String getCurrentUsername() {
      return Minecraft.getMinecraft().getSession().getProfile().getName();
   }

   private static void testProbe() {
      minecraftVersion.clear();
      URLClassLoader ucl = (URLClassLoader)(new MinecraftUtil()).getClass().getClassLoader();
      URL[] urls = ucl.getURLs();
      URL[] var5 = urls;
      int var4 = urls.length;

      for(int var3 = 0; var3 < var4; ++var3) {
         URL url = var5[var3];
         Matcher m = null;

         try {
            m = MINECRAFT_CORE_FILE_PATTERN.matcher(URLDecoder.decode(url.getPath(), "UTF-8"));
         } catch (Exception var8) {
            var8.printStackTrace();
         }

         if(m != null && m.matches()) {
            if(minecraftDataFolder == null) {
               minecraftDataFolder = new File(m.group(1));
            }

            minecraftVersion.add(m.group(2));
         }
      }

   }

   public static boolean isCoreFile(URL url) {
      Matcher m = null;

      try {
         m = MINECRAFT_CORE_FILE_PATTERN.matcher(URLDecoder.decode(url.getPath(), "UTF-8"));
      } catch (Exception var3) {
         var3.printStackTrace();
         return false;
      }

      return m != null && m.matches();
   }

   public static boolean isLibraryFile(URL url) {
      Matcher m = null;

      try {
         m = LIBRARY_FILE_PATTERN.matcher(URLDecoder.decode(url.getPath(), "UTF-8"));
      } catch (Exception var3) {
         var3.printStackTrace();
         return false;
      }

      return m != null && m.matches();
   }
}
