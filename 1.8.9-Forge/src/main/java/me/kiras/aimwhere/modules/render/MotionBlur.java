package me.kiras.aimwhere.modules.render;
import java.lang.reflect.Field;
import java.util.Map;

import me.kiras.aimwhere.ui.motionblur.MotionBlurResourceManager;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
@ModuleInfo(
        name = "MotionBlur",
        description = "Render view.",
        category = ModuleCategory.RENDER
)
public class MotionBlur extends Module {
    public static IntegerValue MOTION_BLUR_AMOUNT = new IntegerValue("BlurAmount", 2, 1, 7);
    int lastValue = 0;
    private Map<String, MotionBlurResourceManager> domainResourceManagers;
    @Override
    public void onDisable() {
        mc.entityRenderer.stopUseShader();
    }
    @Override
    public void onEnable() {
        if(this.domainResourceManagers == null) {
            try {
                Field[] fields = SimpleReloadableResourceManager.class.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType() == Map.class) {
                        field.setAccessible(true);
                        this.domainResourceManagers = (Map<String, MotionBlurResourceManager>) field.get(mc.getResourceManager());
                        break;
                    }
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }

        if(!this.domainResourceManagers.containsKey("motionblur")) {
            this.domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
        }

        if(isFastRenderEnabled()) 
            ClientUtils.disableFastRender();
        this.lastValue = MOTION_BLUR_AMOUNT.get();
        applyShader();
    }

    public boolean isFastRenderEnabled() {
        try {
            Field fastRender = GameSettings.class.getDeclaredField("ofFastRender");
            return fastRender.getBoolean(mc.gameSettings);
        } catch (Exception exception) {
            return false;
        }
    }
    public void applyShader() {
        mc.entityRenderer.loadShader(new ResourceLocation("motionblur", "motionblur"));
    }
    @EventTarget
    public void onTick(TickEvent event) {
        if((!mc.entityRenderer.isShaderActive() || this.lastValue != MOTION_BLUR_AMOUNT.get()) && mc.theWorld != null && !isFastRenderEnabled()) {
            this.lastValue = MOTION_BLUR_AMOUNT.get();
            applyShader();
        }
        if(this.domainResourceManagers == null) {
            try {
                Field[] fields = SimpleReloadableResourceManager.class.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType() == Map.class) {
                        field.setAccessible(true);
                        this.domainResourceManagers = (Map<String, MotionBlurResourceManager>) field.get(mc.getResourceManager());
                        break;
                    }
                }
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        if(!this.domainResourceManagers.containsKey("motionblur")) {
            this.domainResourceManagers.put("motionblur", new MotionBlurResourceManager());
        }
        if(isFastRenderEnabled())
            ClientUtils.disableFastRender();
    }
}
