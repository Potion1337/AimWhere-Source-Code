package me.kiras.aimwhere.customskinloader.utils;


public class TimeUtil {

   public static long getCurrentUnixTimestamp() {
      return System.currentTimeMillis() / 1000L;
   }

   public static long getUnixTimestamp(long offset) {
      return getCurrentUnixTimestamp() + offset;
   }

   public static long getUnixTimestampRandomDelay(long offset) {
      return getCurrentUnixTimestamp() + offset + (long)random(0, 5);
   }

   private static int random(int min, int max) {
      return (int)(Math.random() * (double)(max - min + 1)) + min;
   }
}
