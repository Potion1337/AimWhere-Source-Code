package me.kiras.aimwhere.customskinloader.loader;

import com.mojang.authlib.GameProfile;
import me.kiras.aimwhere.customskinloader.config.SkinSiteProfile;
import me.kiras.aimwhere.customskinloader.loader.ElfSkinLoader;
import me.kiras.aimwhere.customskinloader.loader.JsonAPILoader;
import me.kiras.aimwhere.customskinloader.loader.LegacyLoader;
import me.kiras.aimwhere.customskinloader.loader.MojangAPILoader;
import me.kiras.aimwhere.customskinloader.profile.UserProfile;
import java.util.HashMap;

public class ProfileLoader {

   private static final IProfileLoader[] DEFAULT_LOADERS = new IProfileLoader[]{new MojangAPILoader(), new JsonAPILoader(JsonAPILoader.Type.CustomSkinAPI), new JsonAPILoader(JsonAPILoader.Type.CustomSkinAPIPlus), new LegacyLoader(), new JsonAPILoader(JsonAPILoader.Type.UniSkinAPI), new ElfSkinLoader()};
   public static final HashMap<String, IProfileLoader> LOADERS = initLoaders();


   private static HashMap<String, IProfileLoader> initLoaders() {
      HashMap loaders = new HashMap();
      IProfileLoader[] var4 = DEFAULT_LOADERS;
      int var3 = DEFAULT_LOADERS.length;

      for(int var2 = 0; var2 < var3; ++var2) {
         IProfileLoader loader = var4[var2];
         loaders.put(loader.getName().toLowerCase(), loader);
      }

      return loaders;
   }

   public interface IProfileLoader {

      UserProfile loadProfile(SkinSiteProfile var1, GameProfile var2) throws Exception;

      boolean compare(SkinSiteProfile var1, SkinSiteProfile var2);

      String getName();

      void initLocalFolder(SkinSiteProfile var1);
   }
}
