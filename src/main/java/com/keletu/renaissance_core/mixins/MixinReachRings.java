package com.keletu.renaissance_core.mixins;

import baubles.api.BaublesApi;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.equipment.bauble.ItemBaubleModifier;
import vazkii.botania.common.item.equipment.bauble.ItemReachRing;

@Mixin(ItemReachRing.class)
public abstract class MixinReachRings extends ItemBaubleModifier {

    public MixinReachRings(String name) {
        super(name);
    }

    public boolean canEquip(ItemStack stack, EntityLivingBase player) {
        return BaublesApi.isBaubleEquipped((EntityPlayer) player, ModItems.reachRing) == -1;
    }
}
