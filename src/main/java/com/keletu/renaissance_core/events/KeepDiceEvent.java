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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerDeathHigh(PlayerDropsEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        this.diceMap.remove(player.getUniqueID());
        ItemStack stack = getBackpackStacks(player);
        if(stack.getItem() instanceof ItemDice12)
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.CHARM.getValidSlots()[0];
            handler.setStackInSlot(index, ItemStack.EMPTY);
            this.diceMap.put(player.getUniqueID(), stack);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void playerDeathLow(PlayerDropsEvent event)
    {
        EntityPlayer player = event.getEntityPlayer();
        if(this.diceMap.containsKey(player.getUniqueID()))
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.CHARM.getValidSlots()[0];
            handler.setStackInSlot(index, this.diceMap.get(player.getUniqueID()));
            this.diceMap.remove(player.getUniqueID());
        }
    }

    public static ItemStack getBackpackStack(EntityPlayer player)
    {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        ItemStack stack = handler.getStackInSlot(BaubleType.CHARM.getValidSlots()[0]);
        if(!stack.isEmpty() && stack.getItem() instanceof ItemDice12)
        {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static void setBackpackStack(EntityPlayer player, ItemStack stack)
    {
        if(stack.getItem() instanceof ItemDice12)
        {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int index = BaubleType.CHARM.getValidSlots()[0];
            ItemStack remainder = handler.insertItem(index, stack.copy(), true);
            if(remainder.getCount() < stack.getCount())
            {
                handler.insertItem(index, stack.copy(), false);
            }
        }
    }

    public static ItemStack getBackpackStacks(EntityPlayer player)
    {
        AtomicReference<ItemStack> backpack = new AtomicReference<>(ItemStack.EMPTY);
        backpack.set(KeepDiceEvent.getBackpackStack(player));
        return backpack.get();
    }
}