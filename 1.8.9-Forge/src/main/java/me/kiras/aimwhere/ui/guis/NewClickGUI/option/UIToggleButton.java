package me.kiras.aimwhere.ui.guis.NewClickGUI.option;
import me.kiras.aimwhere.ui.guis.NewClickGUI.ClickMenu;
import me.kiras.aimwhere.ui.guis.NewClickGUI.button.Button;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.Value;

public class UIToggleButton extends Button {
   public Value<?> value;
   public ClickMenu clickmenu;
   public String name;
   public boolean state;

   public UIToggleButton(ClickMenu clickmenu, String name, boolean state, Value<?> value) {
       super(name, state);
      this.clickmenu = clickmenu;
      this.value = value;
      this.name = name;
      this.state = state;
   }

   public void onPress() {
      if (this.parent != null) {
         if (this.parent.equals(LiquidBounce.crink.menu.currentMod.getName())) {
            BoolValue boolValue = (BoolValue) this.value;
            boolValue.set(!boolValue.get());
            super.onPress();
         }
      }
   }
   
   
}
