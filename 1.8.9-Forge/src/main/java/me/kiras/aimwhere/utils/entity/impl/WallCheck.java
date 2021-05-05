package me.kiras.aimwhere.utils.entity.impl;
import me.kiras.aimwhere.utils.entity.ICheck;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public final class WallCheck
implements ICheck {
    @Override
    public boolean validate(Entity entity) {
        return mc.thePlayer.canEntityBeSeen(entity);
    }
}

