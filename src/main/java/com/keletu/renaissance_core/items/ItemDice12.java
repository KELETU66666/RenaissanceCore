package com.keletu.renaissance_core.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import baubles.api.render.IRenderBauble;
import com.keletu.renaissance_core.ConfigsRC;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.events.CursedEvents;
import com.keletu.renaissance_core.util.TCVec3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import thaumcraft.client.lib.obj.IModelCustom;
import thaumcraft.common.blocks.world.taint.ITaintBlock;

public class ItemDice12 extends Item implements IBauble, IVisDiscountGear, IRenderBauble {

    public int rad;
    public int rad1 = 0;
    private IModelCustom model;
    EnumRarity rarityEric = EnumHelper.addRarity("ERIC", TextFormatting.DARK_PURPLE, "Eric");

    public ItemDice12() {
        this.maxStackSize = 1;
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return player instanceof EntityPlayer && !CursedEvents.hasThaumiumCursed((EntityPlayer) player);
    }

    @Override
    public boolean canUnequip(ItemStack stack, EntityLivingBase living) {
        return living instanceof EntityPlayer && ((EntityPlayer) living).isCreative();
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.CHARM;
    }

    @Override
    public int getVisDiscount(ItemStack itemStack, EntityPlayer entityPlayer) {
        return ConfigsRC.cursedVisIncreasePercentage;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return rarityEric;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase livingPlayer) {
        if (livingPlayer.world.isRemote || !(livingPlayer instanceof EntityPlayer))
            return;

        EntityPlayer player = (EntityPlayer) livingPlayer;

        //player.getAttributeMap().applyAttributeModifiers(this.createAttributeMap(player));

        if (player.isCreative() || player.isSpectator())
            return;

        TCVec3 vsource = TCVec3.createVectorHelper((double) player.getPosition().getX() + 0.5, (double) player.getPosition().getY() + 0.5, (double) player.getPosition().getZ() + 0.5);
        for (int q = 1; q < 8; ++q) {
            TCVec3 vtar = TCVec3.createVectorHelper(q, 0.0, 0.0);
            vtar.rotateAroundZ((float) this.rad1 / 180.0f * (float) Math.PI);
            vtar.rotateAroundY((float) this.rad / 180.0f * (float) Math.PI);
            TCVec3 vres1 = vsource.addVector(vtar.xCoord, vtar.yCoord, vtar.zCoord);
            TCVec3 vres2 = vsource.addVector(-vtar.xCoord, -vtar.yCoord, -vtar.zCoord);
            BlockPos t1 = new BlockPos(MathHelper.floor(vres1.xCoord), MathHelper.floor(vres1.yCoord), MathHelper.floor(vres1.zCoord));
            while (player.world.isAirBlock(t1) && t1.getY() > 0) {
                t1 = t1.down();
            }
            BlockPos t2 = new BlockPos(MathHelper.floor(vres2.xCoord), MathHelper.floor(vres2.yCoord), MathHelper.floor(vres2.zCoord));
            while (player.world.isAirBlock(t2) && t2.getY() > 0) {
                t2 = t2.down();
            }
            clearTaintedBlock(player.world, t1);
            clearTaintedBlock(player.world, t2);
        }
    }

    private void clearTaintedBlock(World world, BlockPos p) {
        Block bt = world.getBlockState(p).getBlock();
        if (bt instanceof ITaintBlock) {
            ((ITaintBlock) bt).die(world, p, world.getBlockState(p));
        }
    }

    @Override
    public void onPlayerBaubleRender(ItemStack itemStack, EntityPlayer player, RenderType renderType, float v) {
        float rotateAngleY = (player.ticksExisted + v) / 5.0F;
        if(renderType == RenderType.BODY) {
            GlStateManager.pushMatrix();
            model = AdvancedModelLoader.loadModel(new ResourceLocation(RenaissanceCore.MODID, "textures/models/dice/dice12.obj"));
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(RenaissanceCore.MODID, "textures/models/dice/1221.png"));
            GlStateManager.scale(0.25, 0.25, 0.25);
            GlStateManager.translate(0, -4, 0);
            GlStateManager.rotate(rotateAngleY * (180F / (float) Math.PI), 0, 1, 0);
            model.renderAll();
            GlStateManager.popMatrix();
        }
    }
}