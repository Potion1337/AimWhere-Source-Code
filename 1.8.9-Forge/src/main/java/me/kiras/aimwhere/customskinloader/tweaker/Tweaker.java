package me.kiras.aimwhere.customskinloader.tweaker;

import me.kiras.aimwhere.customskinloader.Logger;
import me.kiras.aimwhere.customskinloader.utils.MinecraftUtil;
import java.io.File;
import java.util.List;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.lang3.ArrayUtils;

public class Tweaker implements ITweaker {

   private String[] args;
   public static Logger logger = new Logger();


   public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
      MinecraftUtil.minecraftDataFolder = gameDir;
      File tweakerLogFile = new File(MinecraftUtil.getMinecraftDataDir(), "CustomSkinLoader/Tweaker.log");
      logger = new Logger(tweakerLogFile);
      logger.info("Using Tweaker");
      logger.info("Tweaker: acceptOptions");
      String[] temp = new String[]{"--gameDir", gameDir.getAbsolutePath(), "--assetsDir", assetsDir.getAbsolutePath(), "--version", profile};
      this.args = (String[])ArrayUtils.addAll(args.toArray(new String[args.size()]), temp);
   }

   public void injectIntoClassLoader(LaunchClassLoader classLoader) {
      logger.info("Tweaker: injectIntoClassLoader");
      logger.info("Loaded as a library.");
   }

   public String getLaunchTarget() {
      logger.info("Tweaker: getLaunchTarget");
      return "net.minecraft.client.main.Main";
   }

   public String[] getLaunchArguments() {
      logger.info("Tweaker: getLaunchArguments");
      return this.args;
   }
}
