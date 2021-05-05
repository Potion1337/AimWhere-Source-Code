package me.kiras.aimwhere.utils.other;

public class AnimationTools {
    public static float clamp(float number, float min, float max) {
        return number < min ? min : Math.min((float)number, (float)max);
    }
}
