package me.kiras.aimwhere.utils.math;

import java.util.*;
import java.security.*;
import java.math.*;

public class Mafs
{
    private static final Random rng;

    public static boolean isInteger(final String s) {
        try {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
        return true;
    }

    public static Double clamp(final double number, final double min, final double max) {
        if (number < min) {
            return min;
        }
        if (number > max) {
            return max;
        }
        return number;
    }

    public static Double getDifference(double num1, double num2) {
        if (num1 > num2) {
            final double tempNum = num1;
            num1 = num2;
            num2 = tempNum;
        }
        return num2 - num1;
    }

    public static float randomSeed(long seed) {
        seed += System.currentTimeMillis();
        return 0.4f + new Random(seed).nextInt(80000000) / 1.0E9f + 1.45E-9f;
    }

    public static float secRanFloat(final float min, final float max) {
        final SecureRandom rand = new SecureRandom();
        return rand.nextFloat() * (max - min) + min;
    }

    public static int randInt(final int min, final int max) {
        final SecureRandom rand = new SecureRandom();
        return rand.nextInt() * (max - min) + min;
    }

    public static double secRanDouble(final double min, final double max) {
        final SecureRandom rand = new SecureRandom();
        return rand.nextDouble() * (max - min) + min;
    }

    public static float getAngleDifference(final float direction, final float rotationYaw) {
        final float phi = Math.abs(rotationYaw - direction) % 360.0f;
        return (phi > 180.0f) ? (360.0f - phi) : phi;
    }

    public static double getMiddle(final double d, final double e) {
        return (d + e) / 2.0;
    }

    public static float getMiddle(final float i, final float i1) {
        return (i + i1) / 2.0f;
    }

    public static double getMiddleint(final double d, final double e) {
        return (d + e) / 2.0;
    }

    public static int getRandom(final int floor, final int cap) {
        return floor + Mafs.rng.nextInt(cap - floor + 1);
    }

    public static double getRandom(final double floor, final double cap) {
        return floor + Mafs.rng.nextInt((int)(cap - floor + 1.0));
    }

    public static double getRandomInRange(final double min, final double max) {
        final Random random = new Random();
        final double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;
        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }

    public static float getRandomInRange(final float min, final float max) {
        final Random random = new Random();
        final float range = max - min;
        final float scaled = random.nextFloat() * range;
        final float shifted = scaled + min;
        return shifted;
    }

    public static int getRandomInRange(final int min, final int max) {
        final Random rand = new Random();
        final int randomNum = rand.nextInt(max - min + 1) + min;
        return randomNum;
    }

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    static {
        rng = new Random();
    }
}
