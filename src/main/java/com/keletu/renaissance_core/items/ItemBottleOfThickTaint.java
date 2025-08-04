package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.entity.EntityBottleOfThickTaint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBottleOfThickTaint extends Item {

    public ItemBottleOfThickTaint() {
        this.maxStackSize = 8;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!player.capabilities.isCreativeMode) {
            player.getHeldItem(hand).shrink(1);
        }

        player.playSound(SoundEvents.ENTITY_EGG_THROW, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote) {
            EntityBottleOfThickTaint entityBottle = new EntityBottleOfThickTaint(world, player);
            entityBottle.shoot(player, player.rotationPitch, player.rotationYaw, -5.0F, 0.66F, 1.0F);
            world.spawnEntity(entityBottle);
        }

        return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
