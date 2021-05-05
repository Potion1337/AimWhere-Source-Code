package me.kiras.aimwhere.utils.other;

import net.minecraft.entity.EntityLivingBase;

public class Target {
    private int height;
    private EntityLivingBase target;
    public Target(EntityLivingBase target,int height) {
        this.target = target;
        this.height = height;
    }
    public int getHeight() {
        return this.height;
    }

    public EntityLivingBase getTarget() {
        return this.target;
    }
}
