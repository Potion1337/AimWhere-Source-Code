package me.kiras.aimwhere.utils.math;

public class Vector<T extends Number>
{
    private T x;
    private T y;
    private T z;

    public Vector(final T x, final T y, final T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector setX(final T x) {
        this.x = x;
        return this;
    }

    public Vector setY(final T y) {
        this.y = y;
        return this;
    }

    public Vector setZ(final T z) {
        this.z = z;
        return this;
    }

    public T getX() {
        return this.x;
    }

    public T getY() {
        return this.y;
    }

    public T getZ() {
        return this.z;
    }
}
