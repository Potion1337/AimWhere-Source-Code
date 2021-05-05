package me.kiras.aimwhere.ui.guis.screens.clickgui;

import java.awt.Color;

import me.kiras.aimwhere.ui.fonts.UnicodeFontRenderer;
import me.kiras.aimwhere.utils.fonts.FontManager;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.modules.render.ClickGUI;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class ClickGui extends GuiScreen implements GuiYesNoCallback {
	public static ModuleCategory currentModuleCategory = ModuleCategory.COMBAT;
	public static Module currentModule = LiquidBounce.moduleManager.getModuleInCategory(currentModuleCategory).get(0);
	public static float startX = 100, startY = 85;
	public int moduleStart = 0;
	public int valueStart = 0;
	boolean previousmouse = true;
	boolean mouse;
	public Opacity opacity = new Opacity(0);
	public int opacityx = 255;
	public float moveX = 0, moveY = 0;
	private UnicodeFontRenderer LogoFont = FontManager.array35;

	int finheight;
	int animheight = 0;
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.scale(ClickGUI.scaleValue.get(), ClickGUI.scaleValue.get(),ClickGUI.scaleValue.get());
		if (isHovered(startX-40, startY, startX + 280, startY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
			if (moveX == 0 && moveY == 0) {
				moveX = mouseX - startX;
				moveY = mouseY - startY;
			} else {
				startX = mouseX - moveX;
				startY = mouseY - moveY;
			}
			this.previousmouse = true;
		} else if (moveX != 0 || moveY != 0) {
			moveX = 0;
			moveY = 0;
		}
		this.opacity.interpolate((float) opacityx);
			RenderUtils.drawRect(startX - 40, startY, startX + 60, startY + 235,
					ClickGUI.colorValue.get());
			RenderUtils.drawRect(startX + 60, startY, startX + 170, startY + 235,
					new Color(255, 255, 255, (int) opacity.getOpacity()).getRGB());
			RenderUtils.drawRect(startX + 170, startY, startX + 280, startY + 235,
					new Color(246, 246, 246, (int) opacity.getOpacity()).getRGB());
			RenderUtils.drawGradientSideways(startX +60 , startY, startX + 70, startY + 235,
					new Color(0, 0, 0, 70).getRGB(),new Color(255, 255, 255, 30).getRGB());

			RenderUtils.drawGradientSideways(startX +170 , startY, startX + 175, startY + 235,
					new Color(0, 0, 0, 50).getRGB(),new Color(255, 255, 255, 30).getRGB());
		LogoFont.drawCenteredString(LiquidBounce.CLIENT_NAME,startX+10,startY+10,new Color(255,255,255, (int) opacity.getOpacity()).getRGB());
		FontManager.array18.drawString(LiquidBounce.CLIENT_VERSION,startX+35,startY+25,new Color(200,200,200, (int) opacity.getOpacity()).getRGB());
		for (int i = 0; i < ModuleCategory.values().length; i++) {
			ModuleCategory[] iterator = ModuleCategory.values();
			if (iterator[i] == currentModuleCategory) {
				finheight = i*30;
				//RenderUtils.drawGradientSideways(startX-40,startY+50+animheight,startX+60,startY+75+animheight,new Color(0,80,255, (int) opacity.getOpacity()).getRGB(),new Color(0,150,255, (int) opacity.getOpacity()).getRGB());
				RenderUtils.drawRect(startX - 40, startY + 50 + animheight, startX + 60, startY + 75 + animheight, new Color(255, 255, 255, 60).getRGB());
				if(animheight<finheight){
					if(finheight - animheight<30) {
						animheight+=2;
					}else{
						animheight+=4;
					}
				}else if(animheight>finheight){
					if(animheight - finheight<30) {
						animheight-=2;
					}else{
						animheight-=4;
					}
				}
				if(animheight==finheight){
					FontManager.array20.drawString(iterator[i].name(),startX-20,startY+60+i*30,new Color(255,255,255, (int) opacity.getOpacity()).getRGB());
				}else{
					RenderUtils.drawRect(startX-20,startY+50+i*30,startX+60,startY+75+i*30,new Color(255,255,255,0).getRGB());
					FontManager.array20.drawString(iterator[i].name(),startX-20,startY+60+i*30,new Color(196,196,196, (int) opacity.getOpacity()).getRGB());
				}
			}else{
				RenderUtils.drawRect(startX-20,startY+50+i*30,startX+60,startY+75+i*30,new Color(255,255,255,0).getRGB());
				FontManager.array20.drawString(iterator[i].name(),startX-20,startY+60+i*30,new Color(196,196,196, (int) opacity.getOpacity()).getRGB());
			}
			try {
				if (this.isCategoryHovered(startX - 40, startY + 50 + i * 30, startX + 60, startY + 75 + i * 40, mouseX,
						mouseY) && Mouse.isButtonDown(0)) {
					currentModuleCategory = iterator[i];
					currentModule = LiquidBounce.moduleManager.getModuleInCategory(currentModuleCategory).get(0);
					moduleStart = 0;
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		}
		int m = Mouse.getDWheel();
		if (this.isCategoryHovered(startX + 60, startY, startX + 200, startY + 235, mouseX, mouseY)) {
			if (m < 0 && moduleStart < LiquidBounce.moduleManager.getModuleInCategory(currentModuleCategory).size() - 4) {
				moduleStart++;
			}
			if (m > 0 && moduleStart > 0) {
				moduleStart--;
			}
		}
		if (this.isCategoryHovered(startX + 200, startY, startX + 420, startY + 235, mouseX, mouseY)) {
			if (m < 0 && valueStart < currentModule.getValues().size() - 4) {
				valueStart++;
			}
			if (m > 0 && valueStart > 0) {
				valueStart--;
			}
		}
			FontManager.array16.drawString(
					currentModule == null ? currentModuleCategory.toString()
							: currentModuleCategory.toString() + "/" + currentModule.getName(),
					startX + 70, startY + 15, new Color(0, 0, 0).getRGB());
		if (currentModule != null) {
			float mY = startY + 30;
			for (int i = 0; i < LiquidBounce.moduleManager.getModuleInCategory(currentModuleCategory).size(); i++) {
				Module module = LiquidBounce.moduleManager.getModuleInCategory(currentModuleCategory).get(i);
				if (mY > startY + 220)
					break;
				if (i < moduleStart) {
					continue;
				}

				RenderUtils.drawRect(startX + 75, mY, startX + 185, mY + 2,
						new Color(246, 246, 246, 0).getRGB());
				if (isSettingsButtonHovered(startX + 75, mY,
						startX + 100 + (FontManager.array18.getStringWidth(module.getName())),
						mY + 8 + FontManager.array18.getStringHeight(""), mouseX, mouseY)) {
					if(!module.getState()) {
							FontManager.array18.drawString(module.getName(), startX + 90, mY + 8,
									new Color(20, 100, 200, (int) opacity.getOpacity()).getRGB(), false);
					}else{
						FontManager.array18.drawString(module.getName(), startX + 90, mY + 8,
								new Color(66,134,245, (int) opacity.getOpacity()).getRGB(), false);
					}
				}else{
					if(module.getState()){
							FontManager.array18.drawString(module.getName(), startX + 90, mY + 8,
									ClickGUI.colorValue.get(), false);
					}else {
						FontManager.array18.drawString(module.getName(), startX + 90, mY + 8,
								new Color(107, 107, 107, (int) opacity.getOpacity()).getRGB(), false);
					}
				}

				if (!module.getState()) {
					RenderUtils.circle(startX + 75, mY + 10, 3,
							new Color(174, 174, 174, (int) opacity.getOpacity()).getRGB());
				} else {
						RenderUtils.circle(startX + 75, mY + 10, 3,
								ClickGUI.colorValue.getValue().intValue());
				}
				if (isSettingsButtonHovered(startX + 75, mY,
						startX + 100 + (FontManager.array18.getStringWidth(module.getName())),
						mY + 8 + FontManager.array18.getStringHeight(""), mouseX, mouseY)) {
					if (!this.previousmouse && Mouse.isButtonDown(0)) {
						module.toggle();
						previousmouse = true;
					}
					if (!this.previousmouse && Mouse.isButtonDown(1)) 
						previousmouse = true;
				}

				if (!Mouse.isButtonDown((int) 0)) {
					this.previousmouse = false;
				}
				if (isSettingsButtonHovered(startX + 90, mY,
						startX + 100 + (FontManager.array20.getStringWidth(module.getName())),
						mY + 8 + FontManager.array20.getStringHeight(""), mouseX, mouseY) && Mouse.isButtonDown((int) 1)) {
					currentModule = module;
					valueStart = 0;
				}
				mY += 20;
			}
			mY = startY + 30;
			if(currentModule.getValues().size()<1){
				RenderUtils.drawRect(0,0,0,0,-1);
				FontManager.array20.drawString("NoSettingsHere",startX+185,startY+10,new Color(178,178,178).getRGB());

			}
			for (int i = 0; i < currentModule.getValues().size(); i++) {
				if (mY > startY + 220)
					break;
				if (i < valueStart) {
					continue;
				}
				UnicodeFontRenderer font = FontManager.array16;
				Value<?> value = currentModule.getValues().get(i);

				if (value instanceof BoolValue) {
					BoolValue boolValue = (BoolValue) value; 
					RenderUtils.drawRect(1,1,1,1,-1);
					float x = startX + 190;
					font.drawString(value.getName(), startX + 185, mY, new Color(136,136,136).getRGB());
					if (boolValue.get()) {
						RenderUtils.drawImage(new ResourceLocation("client/icons/option/Option.png"),(int)x + 55,(int) mY,11,8);
						RenderUtils.drawImage(new ResourceLocation("client/icons/option/True.png"),(int)x + 60, (int)mY-1,11,11);
					} else {
						RenderUtils.drawImage(new ResourceLocation("client/icons/option/Option.png"),(int)x + 60, (int)mY,11,8);
						RenderUtils.drawImage(new ResourceLocation("client/icons/option/False.png"),(int)x + 55, (int)mY-1,11,11);
					}
					if (this.isCheckBoxHovered(x + 55, mY, x + 76, mY + 9, mouseX, mouseY)) {
						if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
							mc.thePlayer.playSound("random.click",1,1);
							this.previousmouse = true;
							this.mouse = true;
						}

						if (this.mouse) {
							boolValue.set(!boolValue.get());
							this.mouse = false;
						}
					}
					if (!Mouse.isButtonDown(0)) {
						this.previousmouse = false;
					}
					mY += 25;
				}
				if (value instanceof ListValue) {
					ListValue listValue = ((ListValue) value);
					float x = startX + 190;
					font.drawString(value.getName(), startX + 185, mY-1,  new Color(136,136,136).getRGB());
						RenderUtils.drawRect(x - 10, mY + 6, x + 75, mY + 22,
								ClickGUI.colorValue.getValue().intValue());
						FontManager.array18.drawString(listValue.get(),
							 (x + 30 - font.getStringWidth(listValue.get()) / 2), mY+10, -1);
					if (this.isStringHovered(x-10, mY + 6, x + 75, mY + 22, mouseX, mouseY)) {
						if (Mouse.isButtonDown((int) 0) && !this.previousmouse) {
							mc.thePlayer.playSound("random.click",1,1);
							String current = listValue.get();
							int next = listValue.getModeListNumber(current) + 1 >= listValue.getValues().length ? 0
									: listValue.getModeListNumber(current) + 1;
							listValue.set(listValue.getValues()[next]);
							this.previousmouse = true;
						}
						if (!Mouse.isButtonDown((int) 0)) {
							this.previousmouse = false;
						}

					}
					mY += 25;
				}
				if(value instanceof ColorValue) {
					ColorValue colorValue = ((ColorValue) value);
					float x = startX + 190;
					font.drawString(value.getName(), startX + 185, mY+8,  new Color(136,136,136).getRGB());
					new ColorSettings().render(colorValue,(int)startX + 210,(int)mY,mouseX,mouseY,Mouse.isButtonDown(0));
					mY += 25;
				}
				if (value instanceof IntegerValue) {
					IntegerValue floatValue = ((IntegerValue) value);
					float x = startX + 190;
					double render = (double) (68.0F
							* (((Number) value.getValue()).floatValue() - (floatValue).getMinimum())
							/ ((floatValue).getMaximum()
							- (floatValue).getMinimum()));
					RenderUtils.drawRect( x - 11, mY + 7, (float) ((double) x + 70), mY + 8,
							(new Color(213, 213, 213, (int) opacity.getOpacity())).getRGB());
					RenderUtils.drawRect( x - 11, mY + 7, (float) ((double) x + render + 0.5D), mY + 8,
							(new Color(88, 182, 255, (int) opacity.getOpacity())).getRGB());
					RenderUtils.circle((float) (x + render + 2D), mY + 7, 2, new Color(0, 144, 255).getRGB());
					font.drawString(value.getName() + ": " + value.getValue(), startX + 185, mY - 3, new Color(136, 136, 136).getRGB());
					if (!Mouse.isButtonDown((int) 0)) {
						this.previousmouse = false;
					}
					if (this.isButtonHovered(x, mY - 4, x + 100, mY + 9, mouseX, mouseY)
							&& Mouse.isButtonDown((int) 0)) {
						if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
							render = (floatValue).getMinimum();
							double max = (floatValue).getMaximum();
							double inc = 1;
							double valAbs = (double) mouseX - ((double) x + 1.0D);
							double perc = valAbs / 68.0D;
							perc = Math.min(Math.max(0.0D, perc), 1.0D);
							double valRel = (max - render) * perc;
							float val = (float) (render + valRel);
							val = (float) (Math.round(val * (1.0D / inc)) / (1.0D / inc));
							floatValue.set((int)val);
						}
						if (!Mouse.isButtonDown(0)) {
							this.previousmouse = false;
						}
					}
					mY += 25;
				}
				if (value instanceof FloatValue) {
					FloatValue floatValue = ((FloatValue) value);
						float x = startX + 190;
						double render =  (68.0F
								* (((Number) value.getValue()).floatValue() - (floatValue).getMinimum())
								/ ((floatValue).getMaximum()
								- (floatValue).getMinimum()));
						RenderUtils.drawRect( x - 11, mY + 7, (float) ((double) x + 70), mY + 8,
								(new Color(213, 213, 213, (int) opacity.getOpacity())).getRGB());
						RenderUtils.drawRect( x - 11, mY + 7, (float) ((double) x + render + 0.5D), mY + 8,
								(new Color(88, 182, 255, (int) opacity.getOpacity())).getRGB());
						RenderUtils.circle((float) (x + render + 2D), mY + 7, 2, new Color(0, 144, 255).getRGB());
						font.drawString(value.getName() + ": " + value.getValue(), startX + 185, mY - 3, new Color(136, 136, 136).getRGB());
						if (!Mouse.isButtonDown((int) 0)) {
							this.previousmouse = false;
						}
						if (this.isButtonHovered(x, mY - 4, x + 100, mY + 9, mouseX, mouseY)
								&& Mouse.isButtonDown((int) 0)) {
							if (!this.previousmouse && Mouse.isButtonDown((int) 0)) {
								render = (floatValue).getMinimum();
								double max = (floatValue).getMaximum();
								double inc = 0.01;
								double valAbs = (double) mouseX - ((double) x + 1.0D);
								double perc = valAbs / 68.0D;
								perc = Math.min(Math.max(0.0D, perc), 1.0D);
								double valRel = (max - render) * perc;
								float val = (float) (render + valRel);
								val = (float) (Math.round(val * (1.0D / inc)) / (1.0D / inc));
								floatValue.set(val);
							}
							if (!Mouse.isButtonDown(0)) {
								this.previousmouse = false;
							}
					}
					mY += 25;
				}
			}
		}

	}

	public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*ClickGUI.scaleValue.get() && mouseX <= g*ClickGUI.scaleValue.get() && mouseY >= y*ClickGUI.scaleValue.get() && mouseY <= y2*ClickGUI.scaleValue.get()) {
			return true;
		}

		return false;
	}

	public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*ClickGUI.scaleValue.get() && mouseX <= x2*ClickGUI.scaleValue.get() && mouseY >= y*ClickGUI.scaleValue.get() && mouseY <= y2) {
			return true;
		}

		return false;
	}

	public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*ClickGUI.scaleValue.get() && mouseX <= g*ClickGUI.scaleValue.get() && mouseY >= y*ClickGUI.scaleValue.get() && mouseY <= y2*ClickGUI.scaleValue.get()) {
			return true;
		}

		return false;
	}

	public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f*ClickGUI.scaleValue.get() && mouseX <= g*ClickGUI.scaleValue.get() && mouseY >= y*ClickGUI.scaleValue.get() && mouseY <= y2*ClickGUI.scaleValue.get()) {
			return true;
		}

		return false;
	}

	public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*ClickGUI.scaleValue.get() && mouseX <= x2*ClickGUI.scaleValue.get() && mouseY >= y*ClickGUI.scaleValue.get() && mouseY <= y2*ClickGUI.scaleValue.get()) {
			return true;
		}

		return false;
	}

	public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
		if (mouseX >= x*ClickGUI.scaleValue.get() && mouseX <= x2*ClickGUI.scaleValue.get() && mouseY >= y*ClickGUI.scaleValue.get() && mouseY <= y2*ClickGUI.scaleValue.get()) {
			return true;
		}

		return false;
	}

	@Override
	public void onGuiClosed() {
		this.opacity.setOpacity(0);
	}
}
