package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import thaumcraft.api.items.IWarpingGear;

import java.util.List;

public class ItemPontifexHammer extends ItemSword implements IWarpingGear {
    public static ToolMaterial phammer = EnumHelper.addToolMaterial("PHAMMER", 4, 1000, 8F, 14, 20);

    public ItemPontifexHammer() {
        super(phammer);
        this.setTranslationKey("PontifexHammer");
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.onUpdate(stack, world, entity, p_77663_4_, p_77663_5_);
        if (stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
            stack.damageItem(-1, (EntityLivingBase)entity);
        }

    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            super.onUsingTick(stack, player, count);
            if (count % 20 == 0) {
                List<EntityLivingBase> list = player.world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().expand(6.0, 2.0, 6.0).expand(-6.0, -2.0, -6.0));
                list.remove(player);
                if (!list.isEmpty()) {
                    for (EntityLivingBase e : list) {
                        if (!e.isDead) {
                            double x = e.posX;
                            double y = e.posY;
                            double z = e.posZ;
                            if (!player.world.isRemote) {
                                if (e.attackEntityFrom(DamageSource.causePlayerDamage(player).setMagicDamage(), (float) player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue())) {
                                    stack.damageItem(2, player);
                                    player.heal(2.0F);
                                }
                            } else {
                                for (int i = 0; i < 3; i++) {
                                    RenaissanceCore.proxy.lifedrain(player, x, y, z);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 3;
    }
}