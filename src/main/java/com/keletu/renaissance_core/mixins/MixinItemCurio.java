package com.keletu.renaissance_core.mixins;

import com.keletu.renaissance_core.entity.EntityCrimsonAnnales;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.items.curios.ItemCurio;

import javax.annotation.Nullable;

@Mixin(ItemCurio.class)
public class MixinItemCurio extends Item {
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return stack.getMetadata() == 6;
    }

    @Override
    @Nullable
    public Entity createEntity(World world, Entity location, ItemStack stack) {
        EntityCrimsonAnnales item = new EntityCrimsonAnnales(world, location.posX, location.posY, location.posZ, stack);
        item.setDefaultPickupDelay();
        item.setNoDespawn();
        item.motionX = location.motionX;
        item.motionY = location.motionY;
        item.motionZ = location.motionZ;
        if ((location) instanceof EntityItem) {
            item.setThrower(((EntityItem) location).getThrower());
            item.setOwner(((EntityItem) location).getOwner());
        }

        return item;
    }
}
