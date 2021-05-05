package me.kiras.aimwhere.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.value.ColorValue;
import net.ccbluex.liquidbounce.value.ListValue;
import java.awt.*;

@ModuleInfo(
        name = "EnchantEffect",
        description = "Change the enchant colour.",
        category = ModuleCategory.RENDER
)
public class EnchantEffect extends Module {
    public ColorValue colorValue = new ColorValue("Color", -1000000);
    public ListValue modeValue = new ListValue("Mode", new String[]{"Rainbow","Custom"}, "Custom");
    public ListValue rainbowModeValue = new ListValue("Rainbow", new String[] {"Normal","Test","Super"},"Normal");
    public int currentColor = new Color(colorValue.get()).getRGB();
    private int rainbowTicks = 0;
    private int r = 0,g = 0,b = 0;
    @EventTarget
    private void onUpdate(UpdateEvent event) {
        switch(modeValue.get()) {
            case "Custom":
                currentColor = new Color(colorValue.get()).getRGB();
                break;
            case "Rainbow":
                switch(rainbowModeValue.get()) {
                    case "Super":
                        currentColor = ColorUtils.rainbow3(400000000L, 1, 1).getRGB();
                        break;
                    case "Normal":
                        if(++rainbowTicks > 50) {
                            rainbowTicks = 0;
                        }
                        currentColor = Color.HSBtoRGB((float) ((mc.thePlayer.ticksExisted * rainbowTicks) * 1.6 % 2), 0.5F, 1f);
                        break;
                    case "Test":
                        if(++r >= 255) {
                            r = 0;
                        }
                        if(++g >= 255) {
                            g = 0;
                        }
                        if(++b >= 255) {
                            b = 0;
                        }
                        currentColor = new Color(r,g,b).getRGB();
                        break;
                }
                break;
        }
    }
}
