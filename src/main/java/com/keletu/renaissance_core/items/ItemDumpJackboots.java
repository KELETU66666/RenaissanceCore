package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import thaumcraft.api.ThaumcraftMaterials;

public class ItemDumpJackboots extends ItemArmor {

    public static String texture = RenaissanceCore.MODID + ":textures/models/armor/dump_jackboots.png";


    public ItemDumpJackboots() {
        super(ThaumcraftMaterials.ARMORMAT_SPECIAL, 0, EntityEquipmentSlot.FEET);
        this.setMaxStackSize(1);
    }

    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.RARE;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return texture;
    }

    @Override
    public void onArmorTick(World world, EntityPlayer p, ItemStack itemStack) {
        if (p.ticksExisted % 2 == 0) {
            if (p.isSneaking()) {
                ItemStack stack = p.getHeldItemMainhand().getItem() instanceof ItemBlock ? p.getHeldItemMainhand() : p.getHeldItemOffhand();
                if (stack != null) {
                    if (stack.getItem() instanceof ItemBlock) {
                        int x = MathHelper.floor(p.posX);
                        int y = MathHelper.floor(p.posY) - 1;
                        int z = MathHelper.floor(p.posZ);
                        BlockPos pos = new BlockPos(x, y, z);
                        Block block = ((ItemBlock) stack.getItem()).getBlock();
                        if (block.canPlaceBlockAt(p.world, pos) && p.world.getBlockState(pos).getBlock().isReplaceable(p.world, pos)) {
                            p.motionX = 0;
                            p.motionY = 0.5;
                            p.motionZ = 0;
                            p.fallDistance = 0F;
                            if (p instanceof EntityPlayerMP) {
                                resetFloatCounter((EntityPlayerMP) p);
                            }
                        }
                        if (!p.world.isRemote) {
                            if (block.canPlaceBlockAt(p.world, pos) && p.world.getBlockState(pos).getBlock().isReplaceable(p.world, pos)) {
                                p.world.setBlockState(pos, block.getStateFromMeta(stack.getItemDamage()), 3);
                                if (!p.capabilities.isCreativeMode) {
                                    stack.shrink(1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void resetFloatCounter(final EntityPlayerMP player) {
        try {
            ObfuscationReflectionHelper.setPrivateValue(NetHandlerPlayServer.class, player.connection, 0, "floatingTickCount", "field_147365_f");
        }
        catch (final Exception ignore) {}
    }
}