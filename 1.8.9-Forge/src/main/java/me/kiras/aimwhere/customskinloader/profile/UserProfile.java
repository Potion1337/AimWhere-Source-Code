package me.kiras.aimwhere.customskinloader.profile;
import org.apache.commons.lang3.StringUtils;

public class UserProfile {

   public String skinUrl = null;
   public String model = null;
   public String capeUrl = null;
   public String elytraUrl = null;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$customskinloader$profile$ModelManager0$Model;


   public void put(ModelManager0.Model model, String url) {
      if(model != null && !StringUtils.isEmpty(url)) {
         switch($SWITCH_TABLE$customskinloader$profile$ModelManager0$Model()[model.ordinal()]) {
         case 1:
            this.skinUrl = url;
            this.model = "default";
            return;
         case 2:
            this.skinUrl = url;
            this.model = "slim";
            return;
         case 3:
            this.capeUrl = url;
            return;
         case 4:
            this.capeUrl = url;
            return;
         default:
         }
      }
   }

   public String toString() {
      return this.toString(0L);
   }

   public String toString(long expiry) {
      return "(SkinUrl: " + this.skinUrl + " , Model: " + this.model + " , CapeUrl: " + this.capeUrl + " , ElytraUrl: " + this.elytraUrl + (expiry == 0L?"":" , Expiry: " + expiry) + ")";
   }

   public boolean isEmpty() {
      return StringUtils.isEmpty(this.skinUrl) && StringUtils.isEmpty(this.capeUrl) && StringUtils.isEmpty(this.elytraUrl);
   }

   public boolean hasSkinUrl() {
      return StringUtils.isNotEmpty(this.skinUrl);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$customskinloader$profile$ModelManager0$Model() {
      if($SWITCH_TABLE$customskinloader$profile$ModelManager0$Model != null) {
         return $SWITCH_TABLE$customskinloader$profile$ModelManager0$Model;
      } else {
         int[] var0 = new int[ModelManager0.Model.values().length];

         try {
            var0[ModelManager0.Model.CAPE.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[ModelManager0.Model.ELYTRA.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[ModelManager0.Model.SKIN_DEFAULT.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[ModelManager0.Model.SKIN_SLIM.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$customskinloader$profile$ModelManager0$Model = var0;
         return var0;
      }
   }
}
