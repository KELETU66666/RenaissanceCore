package com.keletu.renaissance_core.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.potions.PotionThaumarhia;
import thaumcraft.common.lib.potions.PotionWarpWard;

public class ItemTightBelt extends Item implements IBauble {
    public ItemTightBelt() {
        super();
        this.setMaxDamage(-1);
        this.setMaxStackSize(1);
    }
    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public boolean canEquip(ItemStack arg0, EntityLivingBase arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack arg0, EntityLivingBase arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public BaubleType getBaubleType(ItemStack arg0) {
        // TODO Auto-generated method stub
        return BaubleType.BELT;
    }

    @Override
    public void onEquipped(ItemStack arg0, EntityLivingBase arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUnequipped(ItemStack arg0, EntityLivingBase arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase ent) {
        if (!ent.world.isRemote) {
            if (ent instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) ent;
                if (!player.isPotionActive(PotionWarpWard.instance)) {
                    final int warp = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.TEMPORARY);
                    final int totalWarp = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.TEMPORARY) + ThaumcraftApi.internalMethods.getActualWarp(player);
                    if (warp == 0) return;
                    PotionEffect pe = new PotionEffect(PotionThaumarhia.instance, 60, Math.min(3, totalWarp / 15), true, false);
                    player.addPotionEffect(pe);
                    int x = (int)player.posX;
                    int y = (int)player.posY;
                    int z = (int)player.posZ;

                    if (warp > 0 && player.world.rand.nextInt() % 25 == 0) {
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.TEMPORARY);
                        if(player.world.isAirBlock(player.getPosition().add(0, 1, 0))){
                            ThaumcraftApi.internalMethods.addFlux(player.world, player.getPosition().add(0, 1, 0), 1, true);
                        }
                    }
                }
            }
        }
    }

}