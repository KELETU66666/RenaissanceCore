package com.keletu.renaissance_core.events;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.keletu.renaissance_core.items.ItemDice12;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author: MrCrayfish
 */
public class KeepDiceEvent
{
    private Map<UUID, ItemStack> diceMap = new HashMap<>();
    private Map<UUID, Integer> slotMap = new HashMap<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerDeathHigh(PlayerDropsEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        this.diceMap.remove(player.getUniqueID());
        this.slotMap.remove(player.getUniqueID());
        ItemStack stack = getBackpackStacks(player);
        int slot = getDice12Slot(player);
        if(stack.getItem() instanceof ItemDice12 && slot != -1)
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            handler.setStackInSlot(slot, ItemStack.EMPTY);
            this.diceMap.put(player.getUniqueID(), stack);
            this.slotMap.put(player.getUniqueID(), slot);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathLow(PlayerDropsEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if(this.diceMap.containsKey(player.getUniqueID()) && this.slotMap.containsKey(player.getUniqueID()))
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            handler.setStackInSlot(this.slotMap.get(player.getUniqueID()), this.diceMap.get(player.getUniqueID()));
            this.diceMap.remove(player.getUniqueID());
            this.slotMap.remove(player.getUniqueID());
        }
    }

    public static ItemStack getBackpackStack(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int x : BaubleType.TRINKET.getValidSlots()) {
            ItemStack stack = handler.getStackInSlot(BaubleType.TRINKET.getValidSlots()[x]);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemDice12) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static int getDice12Slot(EntityPlayer player) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        for (int x : BaubleType.TRINKET.getValidSlots()) {
            ItemStack stack = handler.getStackInSlot(BaubleType.TRINKET.getValidSlots()[x]);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemDice12) {
                return x;
            }
        }
        return -1;
    }

    public static ItemStack getBackpackStacks(EntityPlayer player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        backpack.set(KeepDiceEvent.getBackpackStack(player));
        return backpack.get();
    }
}