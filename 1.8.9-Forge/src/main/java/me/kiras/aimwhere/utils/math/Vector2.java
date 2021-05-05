package me.kiras.aimwhere.utils.math;

public class Vector2<T extends Number> extends Vector<Number>
{
    public Vector2(final T x, final T y) {
        super(x, y, 0);
    }

    public Vector3<T> toVector3() {
        return new Vector3<T>((T)this.getX(), (T)this.getY(), (T)this.getZ());
    }
}
