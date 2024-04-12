package com.keletu.renaissance_core.items;

import com.keletu.renaissance_core.ConfigsRF;
import com.keletu.renaissance_core.blocks.RFBlocks;
import com.keletu.renaissance_core.blocks.tile.TileManaPod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.config.ConfigItems;

import java.util.Random;

public class ItemManaBean extends ItemFood implements IEssentiaContainerItem {
  public final int itemUseDuration;
  
  Random rand;
  
  public ItemManaBean() {
    super(1, 0.5F, true);
    this.rand = new Random();
    this.itemUseDuration = 10;
    setMaxStackSize(64);
    setHasSubtypes(true);
    setMaxDamage(0);
    setCreativeTab(ConfigItems.TABTC);
    setAlwaysEdible();
  }
  
  public int getMaxItemUseDuration(ItemStack par1ItemStack) {
    return this.itemUseDuration;
  }

  protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
    if (!world.isRemote) {
      Potion p = ForgeRegistries.POTIONS.getValues().get(world.rand.nextInt(ForgeRegistries.POTIONS.getValues().size()));
      if (p != null)
        if (p.isInstant()) {
          p.affectEntity(player, player, player, 2, 3.0D);
        } else {
          player.addPotionEffect(new PotionEffect(p, 160 + world.rand.nextInt(80), 0));
        }
    } 
  }

  static Aspect[] displayAspects = (Aspect[])Aspect.aspects.values().toArray((Object[])new Aspect[0]);
  
  @SideOnly(Side.CLIENT)
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
    if(tab == ConfigItems.TABTC)
      items.add(new ItemStack(this, 1, 0));
  }
  
  //public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
  //  AspectList aspects = getAspects(stack);
  //  if (aspects != null && aspects.size() > 0)
  //    for (Aspect tag : aspects.getAspects()) {
  //      tooltip.add(tag.getName() + " x" + aspects.getAmount(tag));
  //    }
  //  super.addInformation(stack, worldIn, tooltip, flagIn);
  //}

  @SideOnly(Side.CLIENT)
  public int getColor(ItemStack stack, int par2) {
    if (getAspects(stack) != null)
      return getAspects(stack).getAspects()[0].getColor(); 
    int idx = (int)(System.currentTimeMillis() / 500L % displayAspects.length);
    return displayAspects[idx].getColor();
  }
  
  public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5) {
    if (!par2World.isRemote && !par1ItemStack.hasTagCompound())
      setAspects(par1ItemStack, (new AspectList()).add(displayAspects[this.rand.nextInt(displayAspects.length)], ConfigsRF.podAspect));
    super.onUpdate(par1ItemStack, par2World, par3Entity, par4, par5);
  }

  @Override
  public void onCreated(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
    if (!par1ItemStack.hasTagCompound())
      setAspects(par1ItemStack, (new AspectList()).add(displayAspects[this.rand.nextInt(displayAspects.length)], ConfigsRF.podAspect));
  }
  
  public AspectList getAspects(ItemStack itemstack) {
    if (itemstack.hasTagCompound()) {
      AspectList aspects = new AspectList();
      aspects.readFromNBT(itemstack.getTagCompound());
      return (aspects.size() > 0) ? aspects : null;
    } 
    return null;
  }
  
  public void setAspects(ItemStack itemstack, AspectList aspects) {
    if (!itemstack.hasTagCompound())
      itemstack.setTagCompound(new NBTTagCompound());
    aspects.writeToNBT(itemstack.getTagCompound());
  }

  @Override
  public boolean ignoreContainedAspects() {
    return false;
  }

  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    int par5 = pos.getY();
    if (!player.canPlayerEdit(pos, facing, player.getHeldItem(hand)) || facing.getIndex() != 0)
      return EnumActionResult.FAIL;
    Biome biome = world.getBiome(pos);
    boolean magicBiome = false;
    if (biome != null)
      magicBiome = BiomeDictionary.hasType(biome, BiomeDictionary.Type.MAGICAL);
    if (!magicBiome)
      return EnumActionResult.FAIL;
    Block i1 = world.getBlockState(pos).getBlock();
    if (i1 instanceof BlockLog) {
      BlockPos pos1 = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
      if (world.isAirBlock(pos1)) {
        IBlockState k1 = RFBlocks.mana_pod.getStateForPlacement(world, pos1, facing, hitX, hitY, hitZ, 0, player);
        world.setBlockState(pos1, k1, 2);
        TileEntity tile = world.getTileEntity(pos1);
        if (tile != null && tile instanceof TileManaPod && getAspects(player.getHeldItem(hand)) != null && getAspects(player.getHeldItem(hand)).size() > 0)
          ((TileManaPod)tile).aspect = getAspects(player.getHeldItem(hand)).getAspects()[0];
        if (!player.capabilities.isCreativeMode)
          player.getHeldItem(hand).setCount(player.getHeldItem(hand).getCount()-1);
      } 
      return EnumActionResult.SUCCESS;
    } 
    return EnumActionResult.SUCCESS;
  }
}
