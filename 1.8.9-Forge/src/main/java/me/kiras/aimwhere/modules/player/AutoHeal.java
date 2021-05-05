package me.kiras.aimwhere.modules.player;

import me.kiras.aimwhere.utils.timer.TimerUtil;
import net.ccbluex.liquidbounce.event.EventState;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.Rotation;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Mouse;
@ModuleInfo(
        name = "AutoHeal",
        description = "Regen for yourself.",
        category = ModuleCategory.PLAYER
)
public class AutoHeal extends Module {
    private int slot = -1;
    private int currentslot = -1;
    public static boolean healing = false;
    public static boolean doSoup;
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil timer3 = new TimerUtil();
    private boolean eatingApple;
    private int switched = -1;
    public static boolean doingStuff;
    public static boolean potting;
    private final TimerUtil timer2 = new TimerUtil();
    private final BoolValue shouldOnGroundValue = new BoolValue("ShouldOnGround", true);
    private final BoolValue potions = new BoolValue("Potions", true);
    private final BoolValue soup = new BoolValue("Soup", false);
    private final BoolValue jumpPotions = new BoolValue("JumpPotions", false);
    private final BoolValue rangepots = new BoolValue("RegenPotions", true);
    private final BoolValue speedpots = new BoolValue("SpeedPotions", true);
    private final BoolValue eatApples = new BoolValue("Apples", true);
    public BoolValue eatHeads = new BoolValue("Heads", false);
    private final IntegerValue health = new IntegerValue("Health", 10, 1, 20);
    private final IntegerValue delay = new IntegerValue("Delay", 5, 5, 20);
    @Override
    public void onEnable() {
        doingStuff = false;
        eatingApple = false;
        switched = -1;
        timer2.reset();
    }

    @Override
    public void onDisable() {
        currentslot = -1;
        doSoup = false;
        healing = false;
        doingStuff = false;
        if (eatingApple) {
            repairItemPress();
            repairItemSwitch();
        }
    }

    @EventTarget
    public void onPre(MotionEvent event) {
        if(shouldOnGroundValue.get() && !mc.thePlayer.onGround)
            return;
        if(event.getEventState() == EventState.PRE) {
            if (soup.get()) {
                int soupSlot = getSoupSlot();
                if (timer.hasReached(delay.get()) && (double) mc.thePlayer.getHealth() < health.get() && soupSlot != -1) {
                    doSoup = true;
                }
            }
            int eatables = 0;
            if (potions.get()) {
                eatables += getPotionCount();
                if (rangepots.get()) {
                    eatables += getRegenCount();
                }
            }
            if (eatables > 0) {
                boolean up = jumpPotions.get() && mc.thePlayer.onGround;
                update();
                if (shouldHeal()) {
                    healing = false;
                    return;
                }
                if (slot == -1) {
                    healing = false;
                    return;
                }
                if (mc.thePlayer.inventory.mainInventory[slot] != null && mc.thePlayer.inventory.mainInventory[slot].getItem() == Items.potionitem) {
                    if (up) {
                        mc.thePlayer.jump();
                    }
                    RotationUtils.setTargetRotation(new Rotation(mc.thePlayer.rotationYaw, up ? -90.0F : 90.0F));
                }
                healing = true;
            } else {
                healing = false;
            }
            if (eatApples.get() || eatHeads.get()) {
                if (mc.thePlayer == null) {
                    return;
                }
                InventoryPlayer inventory = mc.thePlayer.inventory;
                if (inventory == null) {
                    return;
                }
                doingStuff = false;
                if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
                    KeyBinding useItem = mc.gameSettings.keyBindUseItem;
                    if (!timer2.hasReached(delay.get() * 50)) {
                        eatingApple = false;
                        repairItemPress();
                        repairItemSwitch();
                        return;
                    }
                    if (mc.thePlayer.capabilities.isCreativeMode || mc.thePlayer.isPotionActive(Potion.regeneration) || (double) mc.thePlayer.getHealth() >= health.get()) {
                        timer.reset();
                        if (eatingApple) {
                            eatingApple = false;
                            repairItemPress();
                            repairItemSwitch();
                        }
                        return;
                    }
                    for (int i = 0; i < 2; ++i) {
                        boolean doEatHeads = i != 0;
                        int slot;
                        if (doEatHeads) {
                            if (!eatHeads.get()) {
                                continue;
                            }
                        } else if (!eatApples.get()) {
                            eatingApple = false;
                            repairItemPress();
                            repairItemSwitch();
                            continue;
                        }
                        if ((slot = doEatHeads ? getItemFromHotbar(397) : getItemFromHotbar(322)) == -1)
                            continue;
                        int tempSlot = inventory.currentItem;
                        doingStuff = true;
                        if (doEatHeads) {
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(inventory.getCurrentItem()));
                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(tempSlot));
                            timer.reset();
                            continue;
                        }
                        inventory.currentItem = slot;
                        useItem.pressed = true;
                        if (eatingApple) continue;
                        eatingApple = true;
                        switched = tempSlot;
                    }
                }
            }
        } else {
            int[] pots = new int[]{-1, -1, -1};
            if (speedpots.get()) {
                pots[1] = 1;
            }
            if (timer.hasReached(200) && potting) {
                potting = false;
            }
            int spoofSlot = getBestSpoofSlot();
            if (speedpots.get()) {
                for (int pot : pots) {
                    if (pot != 1 || !timer3.hasReached(1000L) || mc.thePlayer.isPotionActive(pot)) continue;
                    getBestPot(spoofSlot, pot);
                }
            }
            if (soup.get()) {
                int soupSlot = getSoupSlot();
                if (doSoup) {
                    doSoup = false;
                    swap(soupSlot, 5);
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(5));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.DROP_ALL_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
            }
            int eatables = 0;
            if (potions.get()) {
                eatables += getPotionCount();
                if (rangepots.get()) {
                    eatables += getRegenCount();
                }
            }
            if (eatables > 0) {
                if (slot == -1) {
                    return;
                }
                if (!healing) {
                    return;
                }
                if (shouldHeal()) {
                    return;
                }
                if (mc.thePlayer.inventory.mainInventory[slot] == null) {
                    return;
                }
                int packetSlot = slot;
                if (slot < 9) {
                    currentslot = mc.thePlayer.inventory.currentItem;
                    highlight(slot);
                    mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    highlight(mc.thePlayer.inventory.currentItem);
                    highlight(currentslot);
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(8));
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 8, 2, mc.thePlayer);
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.mainInventory[slot]));
                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, packetSlot, 8, 2, mc.thePlayer);
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                }
                timer.reset();
            }
        }
    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                spoofSlot = i - 36;
                break;
            }
            if (!(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion)) continue;
            spoofSlot = i - 36;
            break;
        }
        return spoofSlot;
    }

    void getBestPot(int hotbarSlot, int potID) {
        for (int i = 9; i < 45; ++i) {
            boolean up;
            ItemStack is;
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory) || !((is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemPotion)) continue;
            ItemPotion pot = (ItemPotion)is.getItem();
            if (pot.getEffects(is).isEmpty()) {
                return;
            }
            PotionEffect effect = pot.getEffects(is).get(0);
            int potionID = effect.getPotionID();
            if (potionID != potID || !ItemPotion.isSplash(is.getItemDamage()) || !isBestPot(pot, is)) continue;
            if (36 + hotbarSlot != i) {
                swap(i, hotbarSlot);
            }
            timer3.reset();
            int oldSlot = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(hotbarSlot));
            up = jumpPotions.get() && mc.thePlayer.onGround;
            if (up) {
                mc.thePlayer.jump();
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, up ? -90.0f : 90.0f, mc.thePlayer.onGround));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
            potting = true;
            break;
        }
    }

    boolean isBestPot(ItemPotion potion, ItemStack stack) {
        if (potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1) {
            return false;
        }
        PotionEffect effect = potion.getEffects(stack).get(0);
        int potionID = effect.getPotionID();
        int amplifier = effect.getAmplifier();
        int duration = effect.getDuration();
        for (int i = 9; i < 45; ++i) {
            ItemStack is;
            ItemPotion pot;
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !((is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemPotion) || (pot = (ItemPotion)is.getItem()).getEffects(is) == null) continue;
            for (PotionEffect potionEffect : pot.getEffects(is)) {
                int id = potionEffect.getPotionID();
                int ampl = potionEffect.getAmplifier();
                int dur = potionEffect.getDuration();
                if (id != potionID || !ItemPotion.isSplash(is.getItemDamage())) continue;
                if (ampl > amplifier) {
                    return false;
                }
                if (ampl != amplifier || dur <= duration) continue;
                return false;
            }
        }
        return true;
    }

    private void update() {
        for (int i = 0; i < 36; ++i) {
            ItemPotion potion;
            Item item;
            ItemStack is;
            if (mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.inventory.mainInventory[i] == null || !((item = (is = mc.thePlayer.inventory.mainInventory[i]).getItem()) instanceof ItemPotion) || !potions.get() || (potion = (ItemPotion)item).getEffects(is) == null) continue;
            for (PotionEffect o : potion.getEffects(is)) {
                if (o.getPotionID() != Potion.heal.id && (o.getPotionID() != Potion.regeneration.id || !rangepots.get() || mc.thePlayer.isPotionActive(Potion.regeneration) || !ItemPotion.isSplash(is.getItemDamage()))) continue;
                slot = i;
            }
        }
    }

    private void swap(int slot, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hotbarSlot, 2, mc.thePlayer);
    }
    private int getSoupSlot() {
        int itemSlot = -1;
        for (int i = 9; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !(mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemSoup)) continue;
            itemSlot = i;
        }
        return itemSlot;
    }

    private int getPotionCount() {
        int count = 0;
        for (Slot s : mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack is;
            if (!s.getHasStack() || !((is = s.getStack()).getItem() instanceof ItemPotion)) continue;
            ItemPotion ip = (ItemPotion)is.getItem();
            if (!ItemPotion.isSplash(is.getMetadata())) continue;
            boolean hasHealing = false;
            for (PotionEffect pe : ip.getEffects(is)) {
                if (pe.getPotionID() != Potion.heal.getId()) continue;
                hasHealing = true;
                break;
            }
            if (!hasHealing) continue;
            ++count;
        }
        return count;
    }

    private int getRegenCount() {
        int count = 0;
        for (Slot s : mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack is;
            if (!s.getHasStack() || !((is = s.getStack()).getItem() instanceof ItemPotion)) continue;
            ItemPotion ip = (ItemPotion)is.getItem();
            if (!ItemPotion.isSplash(is.getMetadata())) continue;
            boolean hasHealing = false;
            for (PotionEffect pe : ip.getEffects(is)) {
                if (pe.getPotionID() != Potion.regeneration.getId()) continue;
                hasHealing = true;
                break;
            }
            if (!hasHealing) continue;
            ++count;
        }
        return count;
    }

    private void highlight(int slot) {
        mc.thePlayer.inventory.currentItem = slot;
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
    }

    private boolean shouldHeal() {
        return !((double) mc.thePlayer.getHealth() <= health.get()) || !timer.hasReached(delay.get() * 50);
    }

    private void repairItemPress() {
        KeyBinding keyBindUseItem;
        if (mc.gameSettings != null && (keyBindUseItem = mc.gameSettings.keyBindUseItem) != null) {
            keyBindUseItem.pressed = false;
        }
    }

    private void repairItemSwitch() {
        EntityPlayerSP p = mc.thePlayer;
        if (p == null) {
            return;
        }
        InventoryPlayer inventory = p.inventory;
        if (inventory == null) {
            return;
        }
        int switched = this.switched;
        if (switched == -1) {
            return;
        }
        inventory.currentItem = switched;
    }

    private int getItemFromHotbar(int id) {
        for (int i = 0; i < 9; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] == null || Item.getIdFromItem(mc.thePlayer.inventory.mainInventory[i].getItem()) != id) continue;
            return i;
        }
        return -1;
    }

    static {
        doingStuff = false;
    }
}

