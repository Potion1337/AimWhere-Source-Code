package me.kiras.aimwhere.utils.math;

import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.util.MathHelper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public final class MathUtils extends MinecraftInstance {
    private static Random random = new Random();
    public static double round(double num, double increment) {
        if (increment < 0.0D) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(num);
        bd = bd.setScale((int) increment, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double randomNumber(double max, double min) {
        return Math.random() * (max - min) + min;
    }

    public static float toDegree(final double x, final double z) {
        final double n = z - mc.thePlayer.posZ;
        return (float)(Math.atan2(n, x - mc.thePlayer.posX) * 180.0 / 3.141592653589793) - 90.0f;
    }

    public static double randomDouble2(final double min, final double max) {
        return MathHelper.clamp_double(min + random.nextDouble() * max, min, max);
    }
    public static double getRandomInRange(double max, double min) {
        return min + (max - min) * random.nextDouble();
    }
}
