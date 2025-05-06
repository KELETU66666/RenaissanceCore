package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.capability.ICapConcilium;
import com.keletu.renaissance_core.client.model.ModelPontifexRobe;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;

public class ItemPontifexRobe extends ItemArmor implements IGoggles, IRevealer, IVisDiscountGear, IWarpingGear {
    final static ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("TCPONTIFEXROBE", "", 30, new int[]{4, 7, 8, 4}, 25, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1);
    static ModelPontifexRobe model = null;

    public static String chest = RenaissanceCore.MODID + ":textures/models/armor/pontifex_robe.png";

    public ItemPontifexRobe(int j, EntityEquipmentSlot k) {
        super(MATERIAL, j, k);
        //this.setCreativeTab(ThaumicConcilium.tabTC);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped modelBiped) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ItemPontifexRobe) {
            if (model == null) {
                model = new ModelPontifexRobe();
            }
            model.bipedHead.showModel = armorSlot == EntityEquipmentSlot.HEAD;
            model.armor.showModel = armorSlot == EntityEquipmentSlot.CHEST;
            model.cloth.showModel = armorSlot == EntityEquipmentSlot.LEGS;
            model.bipedRightArm.showModel = armorSlot == EntityEquipmentSlot.CHEST;
            model.bipedLeftArm.showModel = armorSlot == EntityEquipmentSlot.CHEST;
            model.Shape45RL.showModel = armorSlot == EntityEquipmentSlot.LEGS;
            model.Shape50LL.showModel = armorSlot == EntityEquipmentSlot.LEGS;
            model.leftBoot.showModel = armorSlot == EntityEquipmentSlot.FEET;
            model.rightBoot.showModel = armorSlot == EntityEquipmentSlot.FEET;
            model.isSneak = entityLiving.isSneaking();
            model.isRiding = entityLiving.isRiding();
            model.isChild = entityLiving.isChild();
            ItemStack itemstack = entityLiving.getHeldItemMainhand();
            ItemStack itemstack2 = entityLiving.getHeldItemOffhand();
            ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
            ModelBiped.ArmPose modelbiped$armpose2 = ModelBiped.ArmPose.EMPTY;
            if (!itemstack.isEmpty()) {
                modelbiped$armpose = ModelBiped.ArmPose.ITEM;
                if (entityLiving.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();
                    if (enumaction == EnumAction.BLOCK) {
                        modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                    }
                    else if (enumaction == EnumAction.BOW) {
                        modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }
            if (!itemstack2.isEmpty()) {
                modelbiped$armpose2 = ModelBiped.ArmPose.ITEM;
                if (entityLiving.getItemInUseCount() > 0) {
                    EnumAction enumaction2 = itemstack2.getItemUseAction();
                    if (enumaction2 == EnumAction.BLOCK) {
                        modelbiped$armpose2 = ModelBiped.ArmPose.BLOCK;
                    }
                }
            }
            if (entityLiving.getPrimaryHand() == EnumHandSide.RIGHT) {
                model.rightArmPose = modelbiped$armpose;
                model.leftArmPose = modelbiped$armpose2;
            } else {
                model.rightArmPose = modelbiped$armpose2;
                model.leftArmPose = modelbiped$armpose;
            }
        }
        return model;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return chest;
    }

    public static boolean isFullSet(EntityPlayer player) {
        return  !player.inventory.armorInventory.get(0).isEmpty() && player.inventory.armorInventory.get(0).getItem() instanceof ItemPontifexRobe &&
                !player.inventory.armorInventory.get(1).isEmpty() && player.inventory.armorInventory.get(1).getItem() instanceof ItemPontifexRobe &&
                !player.inventory.armorInventory.get(2).isEmpty() && player.inventory.armorInventory.get(2).getItem() instanceof ItemPontifexRobe &&
                !player.inventory.armorInventory.get(3).isEmpty() && player.inventory.armorInventory.get(3).getItem() instanceof ItemPontifexRobe;
    }


    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.EPIC;
    }


    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            stack.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 5));
            items.add(stack);
        }
    }

    @Override
    public boolean showIngamePopups(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
        if (!world.isRemote && player != null && isFullSet(player)) {
            ICapConcilium capabilities = ICapConcilium.get(player);
            if (capabilities == null) return;
            if (!capabilities.getPontifexRobeToggle()) return;
            capabilities.setChainedTime(100);
            capabilities.sync();
        }
    }

    @Override
    public boolean showNodes(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return 5;
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 3;
    }
}