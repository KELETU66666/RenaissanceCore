package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.blocks.TileDestabilizedCrystal;
import com.keletu.renaissance_core.client.particle.FXColoredLightning;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.Random;

public class ItemVisConductor extends Item {
    private static final Random rand = new Random(System.currentTimeMillis());
    long soundDelay = 0L;

    public ItemVisConductor() {
        this.maxStackSize = 1;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        {
            playerIn.setActiveHand(handIn);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        }
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase p, int time) {
        NBTTagCompound fociTag = stack.getTagCompound();
        if (fociTag == null)
            fociTag = new NBTTagCompound();

        RayTraceResult pos = BlockUtils.getTargetBlock(p.world, p, false);
        if (fociTag.hasKey("blockX")) {
            int x = fociTag.getInteger("blockX");
            int y = fociTag.getInteger("blockY");
            int z = fociTag.getInteger("blockZ");
            BlockPos position = new BlockPos(x, y, z);
            if (p.getDistanceSq(x, y, z) <= 64.0) {

                TileDestabilizedCrystal crystal = (TileDestabilizedCrystal) p.world.getTileEntity(position);
                if (crystal != null) {
                    if (!crystal.aspect.equalsIgnoreCase(fociTag.getString("aspect"))) p.stopActiveHand();
                    if (!p.world.isRemote && this.soundDelay < System.currentTimeMillis()) {
                        p.world.playSound(null, p.getPosition(), SoundsTC.jacobs, SoundCategory.PLAYERS, 0.5F, 1.0F + (rand.nextFloat() - rand.nextFloat()) * 0.2F);
                        this.soundDelay = System.currentTimeMillis() + 500L;
                    }
                    int potency = 3;
                    if (time % 3 == 0) {
                        if (!p.world.isRemote) {
                            crystal.capacity = MathHelper.clamp(crystal.capacity - (1 + potency), 0, Integer.MAX_VALUE);
                            p.world.notifyBlockUpdate(position, p.world.getBlockState(position), p.world.getBlockState(position), 3);
                            crystal.markDirty();
                        }
                    }
                    if (p.world.isRemote) {
                        int rgb = Aspect.aspects.get(crystal.aspect).getColor();
                        shootLightning(p.world, p, (float) x + 0.5f + (-0.5f + rand.nextFloat()), (float) y + 0.5f + (-0.5f + rand.nextFloat()), z + 0.5f + (-0.5f + rand.nextFloat()), rgb);
                    }
                }
            } else {
                p.stopActiveHand();
            }
        } else if (pos != null) {
            if (p.world.getTileEntity(pos.getBlockPos()) instanceof TileDestabilizedCrystal) {
                TileDestabilizedCrystal crystal = (TileDestabilizedCrystal) p.world.getTileEntity(pos.getBlockPos());
                if (crystal.aspect != null && crystal.capacity > 0) {
                    p.world.notifyBlockUpdate(pos.getBlockPos(), p.world.getBlockState(pos.getBlockPos()), p.world.getBlockState(pos.getBlockPos()), 3);
                    crystal.markDirty();
                    fociTag.setInteger("blockX", pos.getBlockPos().getX());
                    fociTag.setInteger("blockY", pos.getBlockPos().getY());
                    fociTag.setInteger("blockZ", pos.getBlockPos().getZ());
                    fociTag.setInteger("amount", crystal.capacity);
                    fociTag.setString("aspect", crystal.aspect);
                    stack.setTagCompound(fociTag);
                }
            }
        }

    }

    @Override
    public void onPlayerStoppedUsing(ItemStack wandstack, World world, EntityLivingBase player, int count) {
        NBTTagCompound fociTag = wandstack.getTagCompound();
        if (fociTag == null)
            fociTag = new NBTTagCompound();

        if (fociTag.hasKey("blockX")) {
            int blockX = fociTag.getInteger("blockX");
            int blockY = fociTag.getInteger("blockY");
            int blockZ = fociTag.getInteger("blockZ");
            BlockPos pos = new BlockPos(blockX, blockY, blockZ);
            if (player.world.getTileEntity(pos) instanceof TileDestabilizedCrystal) {
                TileDestabilizedCrystal crystal = (TileDestabilizedCrystal) player.world.getTileEntity(pos);
                final Entity look = EntityUtils.getPointedEntity(player.world, player, 0.0D, 32.0D, 1.1F, true);
                if (look == null) {
                    if (!world.isRemote) {
                        player.world.setBlockToAir(pos);
                        player.world.removeTileEntity(pos);
                        boolean var2 = player.world.getGameRules().getBoolean("mobGriefing");
                        player.world.createExplosion(null, blockX, blockY, blockZ, 2.0F, var2);
                    }
                }
                player.world.notifyBlockUpdate(crystal.getPos(), world.getBlockState(pos), world.getBlockState(pos), 3);
                crystal.markDirty();
            }
            fociTag.removeTag("blockX");
            fociTag.removeTag("blockY");
            fociTag.removeTag("blockZ");
            fociTag.removeTag("amount");
            fociTag.removeTag("aspect");

        }
    }

    public static void shootLightning(World world, EntityLivingBase entityplayer, double xx, double yy, double zz, int rgb) {
        double px = entityplayer.posX;
        double py = entityplayer.posY;
        double pz = entityplayer.posZ;

       // if (entityplayer.getEntityId() != FMLClientHandler.instance().getClient().player.getEntityId()) {
            py = entityplayer.getEntityBoundingBox().minY + (double) (entityplayer.height / 2.0F) + 0.25;
      //  }

        px += -MathHelper.cos(entityplayer.rotationYaw / 180.0F * 3.141593F) * 0.06F;
        py += -0.05999999865889549;
        pz += -MathHelper.sin(entityplayer.rotationYaw / 180.0F * 3.141593F) * 0.06F;
      //  if (entityplayer.getEntityId() != FMLClientHandler.instance().getClient().player.getEntityId()) {
            py = entityplayer.getEntityBoundingBox().minY + (double) (entityplayer.height / 2.0F) + 0.25;
      //  }

        Vec3d vec3d = entityplayer.getLook(1.0F);
        px += vec3d.x * 0.3;
        py += vec3d.y * 0.3;
        pz += vec3d.z * 0.3;

        FXColoredLightning bolt = new FXColoredLightning(world, px, py, pz, xx, yy, zz, world.rand.nextLong(), 6, 0.5F, 8);
        bolt.defaultFractal();
        bolt.setType(10);
        bolt.setWidth(0.125F);

        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;

        bolt.setRBGColorF(red / 255.0F, green / 255.0F, blue / 255.0F);
        bolt.finalizeBolt();
    }

}