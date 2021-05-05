package me.kiras.aimwhere.utils.math;

public class Angle extends Vector2<Float>
{
    public static float b;
    public static float c;
    public static boolean a;
    public static int e;
    public static int d;

    public Angle(final Float x, final Float y) {
        super(x, y);
    }

    public Angle setYaw(final Float yaw) {
        this.setX(yaw);
        return this;
    }

    public Angle setPitch(final Float pitch) {
        this.setY(pitch);
        return this;
    }

    public Float getYaw() {
        return this.getX().floatValue();
    }

    public Float getPitch() {
        return this.getY().floatValue();
    }

    public static double a2(final double a, final double b) {
        return ((a - b) % 360.0 + 540.0) % 360.0 - 180.0;
    }

    public static float getNewAngle(float angle) {
        angle %= 360.0f;
        if (angle >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }

    public Angle constrantAngle() {
        this.setYaw(this.getYaw() % 360.0f);
        this.setPitch(this.getPitch() % 360.0f);
        while (this.getYaw() <= -380.0f) {
            this.setYaw(this.getYaw() + 360.0f);
        }
        while (this.getPitch() <= -380.0f) {
            this.setPitch(this.getPitch() + 360.0f);
        }
        while (this.getYaw() > 380.0f) {
            this.setYaw(this.getYaw() - 360.0f);
        }
        while (this.getPitch() > 380.0f) {
            this.setPitch(this.getPitch() - 360.0f);
        }
        return this;
    }
}
