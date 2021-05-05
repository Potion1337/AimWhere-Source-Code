package me.kiras.aimwhere.utils.math;

import java.util.Random;

public class AngleUtility
{
    private static float minYawSmoothing;
    private static float maxYawSmoothing;
    private static float minPitchSmoothing;
    private static float maxPitchSmoothing;
    private Vector3<Float> delta;
    private static Angle smoothedAngle;
    private float height;
    private static Random random = new Random();

    public AngleUtility(final float minYawSmoothing, final float maxYawSmoothing, final float minPitchSmoothing, final float maxPitchSmoothing) {
        this.height = Mafs.getRandomInRange(1.1f, 1.8f);
        this.minYawSmoothing = minYawSmoothing;
        this.maxYawSmoothing = maxYawSmoothing;
        this.minPitchSmoothing = minPitchSmoothing;
        this.maxPitchSmoothing = maxPitchSmoothing;
        this.delta = new Vector3<Float>(0.0f, 0.0f, 0.0f);
        this.smoothedAngle = new Angle(Float.valueOf(0.0f), Float.valueOf(0.0f));
    }

    public static float randomFloat(float min, float max) {
        return min + (random.nextFloat() * (max - min));
    }

    public Angle calculateAngle(final Vector3<Double> destination, final Vector3<Double> source) {
        final Angle angles = new Angle(Float.valueOf(0.0f), Float.valueOf(0.0f));
        this.delta.setX(destination.getX().floatValue() - source.getX().floatValue()).setY(destination.getY().floatValue() + this.height - (source.getY().floatValue() + this.height)).setZ(destination.getZ().floatValue() - source.getZ().floatValue());
        final double hypotenuse = Math.hypot(this.delta.getX().doubleValue(), this.delta.getZ().doubleValue());
        final float yawAtan = (float)Math.atan2(this.delta.getZ().floatValue(), this.delta.getX().floatValue());
        final float pitchAtan = (float)Math.atan2(this.delta.getY().floatValue(), hypotenuse);
        final float deg = 57.29578f;
        final float yaw = yawAtan * deg - 90.0f;
        final float pitch = -(pitchAtan * deg);
        return angles.setYaw(yaw).setPitch(pitch);
    }

    public void setHeight(final float height) {
        this.height = height;
    }

    public Angle smoothAngle(final Angle destination, final Angle source) {
        return this.smoothedAngle.setYaw(source.getYaw() - destination.getYaw()).setPitch(source.getPitch() - destination.getPitch()).constrantAngle().setYaw(source.getYaw() - this.smoothedAngle.getYaw()).constrantAngle().setPitch(source.getPitch() - this.smoothedAngle.getPitch()).constrantAngle();
    }

    public static Angle smoothAngle(Angle destination, Angle source, float i, float j) {
        return smoothedAngle
                .setYaw(source.getYaw() - destination.getYaw())
                .setPitch(source.getPitch() - destination.getPitch())
                .constrantAngle()
                .setYaw(source.getYaw() - smoothedAngle.getYaw() / 100 * randomFloat(minYawSmoothing, maxYawSmoothing))
                .setPitch(source.getPitch() - smoothedAngle.getPitch() / 100 * randomFloat(minPitchSmoothing, maxPitchSmoothing))
                .constrantAngle();
    }
}
