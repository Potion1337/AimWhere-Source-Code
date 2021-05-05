package net.ccbluex.liquidbounce.features.module.modules.player;

import me.kiras.aimwhere.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.event.WorldEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

import java.util.Arrays;
import java.util.List;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "InventoryManager",
        description = "Auto Clean your inventory.",
        category = ModuleCategory.PLAYER,
        keyBind = Keyboard.KEY_X
)
public class InventoryManager extends Module {
    public final int weaponSlot = 36;
    public final int pickaxeSlot = 37;
    public final int axeSlot = 38;
    public final int shovelSlot = 39;
    public final int gappleSlot = 40;
    private final List<Block> blacklistedBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.oak_fence);
    public final TimerUtil timer = new TimerUtil();
    private final IntegerValue maxblocks = new IntegerValue("MaxBlocks", 512, 0, 1024);
    private final IntegerValue delay = new IntegerValue("Delay", 200, 0, 500);
    // FIXME: 2020-12-13
    private final BoolValue gapple = new BoolValue("GoldenApple", true);
    private final BoolValue openinv = new BoolValue("OpenInv", false);

    @EventTarget
    public void onWorld(WorldEvent event) {
        if(event.getWorldClient() != null)
            return;
        setState(false);
    }
    @Override
    public String getTag() {
        return delay.get()+".0";
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (openinv.get() && !(mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        if (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChat) {
            if (timer.delay(delay.get())) {
                if (!mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getHasStack()) {
                    getBestWeapon(weaponSlot);
                } else if (!isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack())) {
                    getBestWeapon(weaponSlot);
                }
            }
            if (timer.delay(delay.get())) {
                getBestPickaxe();
            }
            if (timer.delay(delay.get())) {
                getBestShovel();
            }
            if (timer.delay(delay.get())) {
                getBestAxe();
            }
            if(timer.delay(delay.get()) && gapple.get()) {
                getGapple();
            }
            if (timer.delay(delay.get())) {
                int i = 9;
                while (i < 45) {
                    if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && shouldDrop(mc.thePlayer.inventoryContainer.getSlot(i).getStack(), i)) {
                        drop(i);
                        timer.reset();
                        if (delay.get() > 0.0) break;
                    }
                    ++i;
                }
            }
        }
    }

    public void swap(int slot1, int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, slot, 2, mc.thePlayer);
    }

    public void drop(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 1, 4, mc.thePlayer);
    }

    public boolean isBestWeapon(ItemStack stack) {
        float damage = getDamage(stack);
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && getDamage(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > damage && is.getItem() instanceof ItemSword) {
                return false;
            }
            ++i;
        }
        return stack.getItem() instanceof ItemSword;
    }

    public void getBestWeapon(int slot) {
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isBestWeapon(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) && getDamage(is) > 0.0f && is.getItem() instanceof ItemSword) {
                swap(i, slot - 36);
                timer.reset();
                break;
            }
            ++i;
        }
    }

    private float getDamage(ItemStack stack) {
        float damage = 0.0f;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            damage += stack.getItemDamage();
        }
        if (item instanceof ItemSword) {
            ItemSword sword = (ItemSword)item;
            damage += sword.getDamageVsEntity();
        }
        return damage + ((float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f + (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.01f);
    }

    public boolean shouldDrop(ItemStack stack, int slot) {
        String name = stack.getDisplayName().toLowerCase();
        if (name.contains("(right click)") || name.contains("右键") || name.contains("menu") || name.contains("大厅") || name.contains("菜单") || name.contains("k||")) {
            return false;
        }
        if (stack.getItem() instanceof ItemFood || stack.getItem() instanceof ItemAppleGold) {
            return false;
        }
        if (stack.getItem() instanceof ItemBlock && (double)getBlockCount() > maxblocks.get() && !(stack.getItem() instanceof ItemSkull)) {
            return true;
        }
        if (stack.getItem() instanceof ItemBlock && (double)getBlockCount() <= maxblocks.get() || stack.getItem() instanceof ItemSkull) {
            return false;
        }
        if (slot == weaponSlot && isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack()) || slot == pickaxeSlot && isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack()) && pickaxeSlot >= 0 || slot == axeSlot && isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack()) && axeSlot >= 0 || slot == shovelSlot && isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack()) && shovelSlot >= 0) {
            return false;
        }
        if (stack.getItem() instanceof ItemArmor) {
            int type = 1;
            while (type < 5) {
                if (!(mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack() && isBestArmor(mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack(), type) || !isBestArmor(stack, type))) {
                    return false;
                }
                ++type;
            }
        }
        if (stack.getItem() instanceof ItemPotion && isBadPotion(stack)) {
            return true;
        }
        if (stack.getItem() instanceof ItemHoe || stack.getItem() instanceof ItemTool || stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemArmor) {
            return true;
        }
        return stack.getItem().getUnlocalizedName().contains("stick") || stack.getItem().getUnlocalizedName().contains("egg") || stack.getItem().getUnlocalizedName().contains("string") || stack.getItem().getUnlocalizedName().contains("cake") || stack.getItem().getUnlocalizedName().contains("mushroom") || stack.getItem().getUnlocalizedName().contains("flint") || stack.getItem().getUnlocalizedName().contains("dyePowder") || stack.getItem().getUnlocalizedName().contains("feather") || stack.getItem().getUnlocalizedName().contains("bucket") || stack.getItem().getUnlocalizedName().contains("chest") && !stack.getDisplayName().toLowerCase().contains("collect") || stack.getItem().getUnlocalizedName().contains("snow") || stack.getItem().getUnlocalizedName().contains("fish") || stack.getItem().getUnlocalizedName().contains("enchant") || stack.getItem().getUnlocalizedName().contains("exp") || stack.getItem().getUnlocalizedName().contains("shears") || stack.getItem().getUnlocalizedName().contains("anvil") || stack.getItem().getUnlocalizedName().contains("torch") || stack.getItem().getUnlocalizedName().contains("seeds") || stack.getItem().getUnlocalizedName().contains("leather") || stack.getItem().getUnlocalizedName().contains("reeds") || stack.getItem().getUnlocalizedName().contains("skull") || stack.getItem().getUnlocalizedName().contains("record") || stack.getItem().getUnlocalizedName().contains("snowball") || stack.getItem() instanceof ItemGlassBottle || stack.getItem().getUnlocalizedName().contains("piston");
    }

    public static boolean isBestArmor(ItemStack stack, int type) {
        float prot = getProtection(stack);
        String strType = "";
        if (type == 1) {
            strType = "helmet";
        } else if (type == 2) {
            strType = "chestplate";
        } else if (type == 3) {
            strType = "leggings";
        } else if (type == 4) {
            strType = "boots";
        }
        if (!stack.getUnlocalizedName().contains(strType)) {
            return false;
        }
        int i = 5;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && getProtection(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > prot && is.getUnlocalizedName().contains(strType)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static float getProtection(ItemStack stack) {
        float prot = 0.0f;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)stack.getItem();
            prot = (float)((double)prot + ((double)armor.damageReduceAmount + (double)((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075));
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 50.0);
            prot = (float)((double)prot + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack) / 100.0);
        }
        return prot;
    }
    private int getBlockCount() {
        int blockCount = 0;
        int i = 0;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                Item item = is.getItem();
                if (is.getItem() instanceof ItemBlock && !blacklistedBlocks.contains(((ItemBlock)item).getBlock())) {
                    blockCount += is.stackSize;
                }
            }
            ++i;
        }
        return blockCount;
    }

    private void getBestPickaxe() {
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isBestPickaxe(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) && pickaxeSlot != i && !isBestWeapon(is)) {
                if (!mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getHasStack()) {
                    swap(i, pickaxeSlot - 36);
                    timer.reset();
                    if (delay.get() > 0) {
                        return;
                    }
                } else if (!isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack())) {
                    swap(i, pickaxeSlot - 36);
                    timer.reset();
                    if (delay.get() > 0) {
                        return;
                    }
                }
            }
            ++i;
        }
    }

    private void getGapple() {
        for(int i = 0;i < 8; ++i) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if(slot.getHasStack()) {
                Item is = slot.getStack().getItem();
                if(i != 4 && is instanceof ItemAppleGold)
                    swap(i,gappleSlot - 36);
            }
        }
        for(int i = 9; i < 45; ++i) {
            if(mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                Item item = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                if(item instanceof ItemAppleGold) {
                    swap(i, gappleSlot - 36);
                    timer.reset();
                    if (delay.get() > 0) {
                        return;
                    }
                }
            }
        }
    }


    private void getBestShovel() {
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isBestShovel(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) && shovelSlot != i && !isBestWeapon(is)) {
                if (!mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getHasStack()) {
                    swap(i, shovelSlot - 36);
                    timer.reset();
                    if (delay.get() > 0) {
                        return;
                    }
                } else if (!isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack())) {
                    swap(i, shovelSlot - 36);
                    timer.reset();
                    if (delay.get() > 0) {
                        return;
                    }
                }
            }
            ++i;
        }
    }

    private void getBestAxe() {
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && isBestAxe(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) && axeSlot != i && !isBestWeapon(is)) {
                if (!mc.thePlayer.inventoryContainer.getSlot(axeSlot).getHasStack()) {
                    swap(i, axeSlot - 36);
                    timer.reset();
                    if (delay.get() > 0) {
                        return;
                    }
                } else if (!isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack())) {
                    swap(i, axeSlot - 36);
                    timer.reset();
                    if (delay.get() > 0) {
                        return;
                    }
                }
            }
            ++i;
        }
    }

    private boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe)) {
            return false;
        }
        float value = getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && getToolEffect(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > value && is.getItem() instanceof ItemPickaxe) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private boolean isBestShovel(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemSpade)) {
            return false;
        }
        float value = getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && getToolEffect(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > value && is.getItem() instanceof ItemSpade) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private boolean isBestAxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemAxe)) {
            return false;
        }
        float value = getToolEffect(stack);
        int i = 9;
        while (i < 45) {
            ItemStack is;
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && getToolEffect(is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                return false;
            }
            ++i;
        }
        return true;
    }

    private float getToolEffect(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemTool)) {
            return 0.0f;
        }
        String name = item.getUnlocalizedName();
        ItemTool tool = (ItemTool)item;
        float value;
        if (item instanceof ItemPickaxe) {
            value = tool.getStrVsBlock(stack, Blocks.stone);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        } else if (item instanceof ItemSpade) {
            value = tool.getStrVsBlock(stack, Blocks.dirt);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        } else if (item instanceof ItemAxe) {
            value = tool.getStrVsBlock(stack, Blocks.log);
            if (name.toLowerCase().contains("gold")) {
                value -= 5.0f;
            }
        } else {
            return 1.0f;
        }
        value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075);
        value = (float)((double)value + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100.0);
        return value;
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion)stack.getItem();
            if (potion.getEffects(stack) == null) {
                return true;
            }
            for (PotionEffect o : potion.getEffects(stack)) {
                if (o.getPotionID() != Potion.poison.getId() && o.getPotionID() != Potion.harm.getId() && o.getPotionID() != Potion.moveSlowdown.getId() && o.getPotionID() != Potion.weakness.getId()) continue;
                return true;
            }
        }
        return false;
    }

}
