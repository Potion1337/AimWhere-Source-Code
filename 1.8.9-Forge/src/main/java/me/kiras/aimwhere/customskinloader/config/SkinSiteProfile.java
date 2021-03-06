package me.kiras.aimwhere.customskinloader.config;

import me.kiras.aimwhere.customskinloader.loader.jsonapi.CustomSkinAPIPlus;

public class SkinSiteProfile {

   public String name;
   public String type;
   public String userAgent;
   public String root;
   public CustomSkinAPIPlus.CustomSkinAPIPlusPrivacy privacy;
   public Boolean checkPNG;
   public String skin;
   public String model;
   public String cape;
   public String elytra;


   public static SkinSiteProfile createMojangAPI(String name) {
      SkinSiteProfile ssp = new SkinSiteProfile();
      ssp.name = name;
      ssp.type = "MojangAPI";
      return ssp;
   }

   public static SkinSiteProfile createCustomSkinAPI(String name, String root) {
      SkinSiteProfile ssp = new SkinSiteProfile();
      ssp.name = name;
      ssp.type = "CustomSkinAPI";
      ssp.root = root;
      return ssp;
   }

   public static SkinSiteProfile creatCustomSkinAPIPlus(String name, String root) {
      SkinSiteProfile ssp = new SkinSiteProfile();
      ssp.name = name;
      ssp.type = "CustomSkinAPIPlus";
      ssp.root = root;
      ssp.privacy = new CustomSkinAPIPlus.CustomSkinAPIPlusPrivacy();
      return ssp;
   }

   public static SkinSiteProfile createUniSkinAPI(String name, String root) {
      SkinSiteProfile ssp = new SkinSiteProfile();
      ssp.name = name;
      ssp.type = "UniSkinAPI";
      ssp.root = root;
      return ssp;
   }

   public static SkinSiteProfile createLegacy(String name, String skin, String cape, String elytra) {
      SkinSiteProfile ssp = new SkinSiteProfile();
      ssp.name = name;
      ssp.type = "Legacy";
      ssp.checkPNG = Boolean.valueOf(false);
      ssp.skin = skin;
      ssp.model = "default";
      ssp.cape = cape;
      ssp.elytra = elytra;
      return ssp;
   }

   public static SkinSiteProfile createElfSkin(String name) {
      SkinSiteProfile ssp = new SkinSiteProfile();
      ssp.name = name;
      ssp.type = "ElfSkin";
      return ssp;
   }
}
