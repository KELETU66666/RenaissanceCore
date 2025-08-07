package com.keletu.renaissance_core.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionWarpWard;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.List;

public class ItemBurdeningAmulet extends Item implements IBauble {
    public ItemBurdeningAmulet() {
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
        return BaubleType.AMULET;
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
                if (player.ticksExisted % 20 == 0) {
                    Entity look = EntityUtils.getPointedEntity(player.world, player, 1.0, 32.0, 0.1F, false);
                    if (look instanceof EntityLivingBase) {
                        MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, look));
                        List<EntityLiving> livings = player.world.getEntitiesWithinAABB(EntityLiving.class, player.getEntityBoundingBox().grow(32, 32, 32));
                        if (!livings.isEmpty()) {
                            for (EntityLiving pet : livings) {
                                if (pet instanceof IEntityOwnable) {
                                    if ((((IEntityOwnable) pet).getOwner() == player) && (look != pet)) {
                                        pet.setAttackTarget((EntityLivingBase) look);
                                    }
                                }
                            }
                        }
                    }
                }
                final int warp = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.TEMPORARY);
                if (warp == 0) return;
                if (!player.isPotionActive(PotionWarpWard.instance)) {
                    PotionEffect pe = new PotionEffect(PotionDeathGaze.instance, 60, Math.min(3, warp / 15), true, false);
                    player.addPotionEffect(pe);
                    if (warp > 0 && player.world.rand.nextInt() % 10 == 0) {
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.TEMPORARY);
                    }
                }
            }
        }
    }

}