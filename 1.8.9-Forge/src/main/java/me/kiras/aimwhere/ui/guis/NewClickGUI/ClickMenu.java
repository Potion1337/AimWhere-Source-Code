package me.kiras.aimwhere.ui.guis.NewClickGUI;

import javafx.scene.control.Tab;
import me.kiras.aimwhere.ui.guis.NewClickGUI.button.Button;
import me.kiras.aimwhere.ui.guis.NewClickGUI.handler.MouseInputHandler;
import me.kiras.aimwhere.ui.guis.NewClickGUI.option.UISlider;
import me.kiras.aimwhere.ui.guis.NewClickGUI.option.UIToggleButton;
import me.kiras.aimwhere.utils.fonts.FontManager;
import me.kiras.aimwhere.utils.render.Colors;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;

import java.awt.Color;
import java.io.*;
import java.util.*;

public class ClickMenu
{
    private final ArrayList<ClickMenuCategory> categories;
    private final MouseInputHandler handler;
    private final String fileDir;
    public boolean settingMode;
    public Module currentMod;
    private final ArrayList<BoolValue> modBooleanValue;
    private final ArrayList<ListValue> modModeValue;
    private final ArrayList<FloatValue> modDoubleValue;
    private final ArrayList<IntegerValue> modIntValue;
    private final Map<BoolValue, Button> booleanValueMap = new HashMap<>();
    private final Map<FloatValue, UISlider> floatValueMap = new HashMap<>();
    private final Map<IntegerValue, UISlider> intValueMap = new HashMap<>();

    private boolean isSelectingMode;
    private ListValue currentSelectingMode;
    private float currentStartY;
    public boolean isDraggingSlider;
    private float wheelSmoothValue;
    private float wheelStateValue;
    
    public ClickMenu() {
        this.handler = new MouseInputHandler(0);
        Minecraft mc = Minecraft.getMinecraft();
        this.settingMode = false;
        this.currentMod = null;
        this.modBooleanValue = new ArrayList<>();
        this.modModeValue = new ArrayList<>();
        this.modDoubleValue = new ArrayList<>();
        this.modIntValue = new ArrayList<>();
        this.isSelectingMode = false;
        this.currentSelectingMode = null;
        this.currentStartY = 0.0f;
        this.isDraggingSlider = false;
        this.fileDir = mc.mcDataDir.getAbsolutePath() + "/" + "Config";
        this.categories = new ArrayList<>();
        this.addCategories();
        try {
            this.loadClickGui();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void draw(int mouseX, int mouseY) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        float n3 = (float)scaledResolution.getScaledWidth();
        float n4 = (float)scaledResolution.getScaledHeight();
        for (ClickMenuCategory category : this.categories) {
            category.draw(mouseX, mouseY);
        }
        if (this.settingMode && this.currentMod != null) {
            RenderUtils.drawRect(0.0f, 0.0f, n3, n4, ClientUtils.reAlpha(Colors.BLACK.c, 0.45f));
            this.modBooleanValue.clear();
            this.modModeValue.clear();
            this.modDoubleValue.clear();
            for (Value<?> values : currentMod.getValues()) {
                if (values instanceof FloatValue) {
                    this.modDoubleValue.add((FloatValue) values);
                }
                if (values instanceof IntegerValue)
                    this.modIntValue.add((IntegerValue) values);
                if (values instanceof ListValue) {
                    this.modModeValue.add((ListValue) values);
                }
                if (values instanceof BoolValue) {
                    this.modBooleanValue.add((BoolValue) values);
                }
            }
            FontManager.tahoma30.drawString(this.currentMod.getBreakName(true), n3 / 2.0f - 100.0f, n4 / 2.0f - 135.0f - FontManager.tahoma30.FONT_HEIGHT, Colors.WHITE.c);
            RenderUtils.drawRoundedRect(n3 / 2.0f - 100.0f, n4 / 2.0f - 130.0f, n3 / 2.0f + 100.0f, n4 / 2.0f + 130.0f, (int) 5.0f, Colors.WHITE.c);
            if (!RenderUtils.isHovering(mouseX, mouseY, n3 / 2.0f - 105.0f, n4 / 2.0f - 135.0f, n3 / 2.0f + 105.0f, n4 / 2.0f + 135.0f) && this.handler.canExcecute() && !this.isSelectingMode && !this.isDraggingSlider) {
                this.settingMode = false;
                this.currentMod = null;
                this.wheelStateValue = 0.0F;
            }
            if (!this.isSelectingMode) {
                this.processWheel(mouseX, mouseY, n3, n4);
            }
            this.wheelSmoothValue = (float) RenderUtils.getAnimationState(this.wheelSmoothValue, this.wheelStateValue * 30.0f, (float)(Math.max(10.0f, Math.abs(this.wheelSmoothValue - this.wheelStateValue * 30.0f) * 50.0f) * 0.3));
            float currentStartY = n4 / 2.0f - 122.0f + this.wheelSmoothValue;
            GL11.glPushMatrix();
            GL11.glEnable(3089);
            RenderUtils.doGlScissor((int)n3 / 2 - 98, (int)n4 / 2 - 128, (int)(n3 / 2.0f + 98.0f - (n3 / 2.0f - 98.0f)), (int)(n4 / 2.0f + 128.0f) - (int)(n4 / 2.0f - 128.0f));
            //boolean
            for (BoolValue boolValue : this.modBooleanValue) {
                String name = currentMod.getName();
                FontManager.tahoma20.drawString(boolValue.getName(), n3 / 2.0f - 94.0f, currentStartY - 2.0f, Colors.BLACK.c);
                Button button;
                if (booleanValueMap.containsKey(boolValue)) {
                    button = booleanValueMap.get(boolValue);
                }
                else {
                    button = new UIToggleButton(this, name, boolValue.get(), boolValue);
                    booleanValueMap.put(boolValue, button);
                }
                button.draw(n3 / 2.0f + 92.0f, currentStartY + 4.0f);
                currentStartY += 18.0f;
            }
            
            //double
            for (FloatValue floatValue : this.modDoubleValue) {
                UISlider uislider;
                if (floatValueMap.containsKey(floatValue)) {
                    uislider = floatValueMap.get(floatValue);
                }
                else {
                    uislider = new UISlider(floatValue);
                    floatValueMap.put(floatValue, uislider);
                }
                uislider.draw(n3 / 2.0f + 104.0f, currentStartY);
                uislider.onPress(mouseX, mouseY);
                currentStartY += 18.0f;
            }

            for(IntegerValue integerValue : this.modIntValue) {
                UISlider uislider;
                if (intValueMap.containsKey(integerValue)) {
                    uislider = intValueMap.get(integerValue);
                }
                else {
                    uislider = new UISlider(integerValue);
                    intValueMap.put(integerValue, uislider);
                }
                uislider.draw(n3 / 2.0f + 104.0f, currentStartY);
                uislider.onPress(mouseX, mouseY);
                currentStartY += 18.0f;
            }
            
            
            for (ListValue currentSelectingMode : this.modModeValue) {
                FontManager.tahoma20.drawString(currentSelectingMode.getName(), n3 / 2.0f - 94.0f, currentStartY - 2.0f, Colors.BLACK.c);
                FontManager.tahoma16.drawString(currentSelectingMode.get(), n3 / 2.0f + 28.0f, currentStartY, Colors.DARKGREY.c);
                if (!this.isSelectingMode) {
                    FontManager.tahoma25.drawString(">", n3 / 2.0f + 84.0f, currentStartY - 4.0f, new Color(Colors.GREY.c).brighter().getRGB());
                    if (RenderUtils.isHovering(mouseX, mouseY, n3 / 2.0f + 85.0f, currentStartY, n3 / 2.0f + 95.0f, currentStartY + 10.0f) && this.handler.canExcecute()) {
                        this.isSelectingMode = true;
                        this.currentSelectingMode = currentSelectingMode;
                    }
                }
                else if (RenderUtils.isHovering(mouseX, mouseY, n3 / 2.0f + 84.0f, currentStartY, n3 / 2.0f + 95.0f, currentStartY + 10.0f) && this.handler.canExcecute()) {
                    this.isSelectingMode = false;
                    this.currentSelectingMode = null;
                }
                if (this.isSelectingMode) {
                    if (this.currentSelectingMode == currentSelectingMode) {
                        FontManager.tahoma25.drawString("-", n3 / 2.0f + 87.0f, currentStartY - 4.0f, new Color(Colors.GREY.c).brighter().getRGB());
                        this.currentStartY = currentStartY;
                    }
                    else {
                        FontManager.tahoma25.drawString(">", n3 / 2.0f + 84.0f, currentStartY - 3.0f, new Color(Colors.GREY.c).brighter().getRGB());
                    }
                }
                currentStartY += 18.0f;
            }
            GL11.glDisable(3089);
            GL11.glPopMatrix();
            if (this.isSelectingMode && this.currentSelectingMode != null) {
                float currentStartY2 = this.currentStartY;
                RenderUtils.drawRoundedRect(n3 / 2.0f + 102.0f, currentStartY2, n3 / 2.0f + 182.0f, currentStartY2 + 15 * currentSelectingMode.getValues().length, (int) 2.0f, Colors.WHITE.c);
                int i = 0;
                while (true) {
                    assert currentSelectingMode != null;
                    if (!(i < currentSelectingMode.getValues().length)) break;
                    FontManager.tahoma15.drawString(currentSelectingMode.getValues()[i], n3 / 2.0f + 106.0f, currentStartY2 + 3.0f, Colors.BLACK.c);
                    if (RenderUtils.isHovering(mouseX, mouseY, n3 / 2.0f + 102.0f, currentStartY2, n3 / 2.0f + 182.0f, currentStartY2 + 15.0f) && Mouse.isButtonDown(0)) {
                        this.currentSelectingMode.set(currentSelectingMode.getValues()[i]);
                        this.isSelectingMode = false;
                        this.currentSelectingMode = null;
                    }
                    currentStartY2 += 15.0f;
                    ++i;
                }
            }
        }
    }
    
    private void processWheel(int mouseX, int mouseY, float windowsX, float windowsY) {
        int mouseWheel = Mouse.getDWheel();
        if (RenderUtils.isHovering(mouseX, mouseY, windowsX / 2.0f - 100.0f, windowsY / 2.0f - 130.0f, windowsX / 2.0f + 100.0f, windowsY / 2.0f + 130.0f)) {
            if (mouseWheel > 0) {
                if (this.wheelStateValue < 0.0f) {
                    ++this.wheelStateValue;
                }
            }
            else if (mouseWheel < 0 && this.wheelStateValue * 30.0f > this.currentMod.getValues().size() * -40) {
                --this.wheelStateValue;
            }
        }
    }
    
    private void addCategories() {
        int TAB_HEIGHT = 25;
        int WIDTH = 85;
        int posX = 10;
        for(ModuleCategory moduleCategory : ModuleCategory.values()) {
            this.categories.add(new ClickMenuCategory(moduleCategory,posX, 100, WIDTH, TAB_HEIGHT, this.handler));
            posX += 110;
        }
    }
    
    public void mouseClick(int mouseX, int mouseY) {
        for (Button button : booleanValueMap.values())
            button.isPressed(mouseX, mouseY);
        for (ClickMenuCategory category : this.categories)
            category.mouseClick(mouseX, mouseY);
    }
    
    public void mouseRelease(int mouseX, int mouseY) {
        for (ClickMenuCategory category : this.categories) {
            category.mouseRelease(mouseX, mouseY);
        }
        this.saveClickGui();
    }
    
    public ArrayList<ClickMenuCategory> getCategories() {
        return this.categories;
    }
    
    public void saveClickGui() {
        File file = new File(this.fileDir + "/gui.txt");
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            PrintWriter printWriter = new PrintWriter(file);
            for (ClickMenuCategory menu : this.getCategories()) {
                printWriter.print(menu.c.name() + ":" + menu.x + ":" + menu.y + ":" + menu.uiMenuMods.open + "\n");
            }
            printWriter.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void loadClickGui() throws IOException {
        File file = new File(this.fileDir + "/gui.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
        else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                try {
                    String[] array = line.split(":");
                    if (array.length > 4) {
                        String s = array[0];
                        int intValue = Integer.parseInt(array[1]);
                        int intValue2 = Integer.parseInt(array[2]);
                        boolean booleanValue = Boolean.parseBoolean(array[3]);
                        for (ClickMenuCategory menu : this.getCategories()) {
                            if (menu.c.name().equals(s)) {
                                menu.x = intValue;
                                menu.y = intValue2;
                                menu.uiMenuMods.open = booleanValue;
                            }
                        }
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            bufferedReader.close();
        }
    }
}
