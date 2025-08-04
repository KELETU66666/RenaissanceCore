package com.keletu.renaissance_core.items;

import baubles.api.BaublesApi;
import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.events.PlayerEvents;
import thaumcraft.common.world.aura.AuraHandler;

import java.util.HashMap;

@Mod.EventBusSubscriber
public class ItemRunicWindings extends ItemArmor implements IVisDiscountGear {
    public static HashMap<Integer, Long> nextTick;
    public static String chest = RenaissanceCore.MODID + ":textures/models/armor/chest_windings.png";
    public static String legs = RenaissanceCore.MODID + ":textures/models/armor/legs_windings.png";

    static HashMap<Integer, Long> nextCycle = new HashMap();
    static HashMap<Integer, Integer> lastCharge = new HashMap();
    static HashMap<Integer, Integer> lastMaxCharge = new HashMap();
    static HashMap<Integer, Integer> runicInfo = new HashMap();

    final static ArmorMaterial MATERIAL = EnumHelper.addArmorMaterial("tcwindings", "TCWINDINGS", 1, new int[]{1, 1, 1, 1}, 30, SoundsTC.page, 0);

    public ItemRunicWindings(int j, EntityEquipmentSlot k) {
        super(MATERIAL, j, k);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return slot == EntityEquipmentSlot.CHEST ? chest : legs;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemstack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public void onArmorTick(final World world, final EntityPlayer player, final ItemStack armor) {
        if (!world.isRemote) {
            handleRunicArmor(player);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if(!stack.hasTagCompound() || (stack.getTagCompound() != null && !stack.getTagCompound().hasKey("TC.RUNIC"))){
            stack.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 100));
        }
    }

    static {
        ItemRunicWindings.nextTick = new HashMap<Integer, Long>();

    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            ItemStack stack = new ItemStack(this);
            stack.setTagInfo("TC.RUNIC", new NBTTagByte((byte) 100));
            items.add(stack);
        }
    }

    @Override
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return 3;
    }

    //@SubscribeEvent
    //public static void livingTick(LivingEvent.LivingUpdateEvent event) {
    //    if (event.getEntity() instanceof EntityPlayer) {
    //        EntityPlayer player = (EntityPlayer)event.getEntity();
    //        if (!player.world.isRemote) {
    //            handleRunicArmor(player);
    //        }
    //    }
//
    //}

    private void handleRunicArmor(EntityPlayer player) {
        int charge;
        if (player.ticksExisted % 20 == 0) {
            int max = 0;

            for (int a = 0; a < 4; ++a) {
                max += PlayerEvents.getRunicCharge(player.inventory.armorInventory.get(a));
            }

            IInventory baubles = BaublesApi.getBaubles(player);

            for (charge = 0; charge < baubles.getSizeInventory(); ++charge) {
                max += PlayerEvents.getRunicCharge(baubles.getStackInSlot(charge));
            }

            if (lastMaxCharge.containsKey(player.getEntityId())) {
                charge = lastMaxCharge.get(player.getEntityId());
                if (charge > max) {
                    player.setAbsorptionAmount(player.getAbsorptionAmount() - (float) (charge - max));
                }

                if (max <= 0) {
                    lastMaxCharge.remove(player.getEntityId());
                }
            }

            if (max > 0) {
                runicInfo.put(player.getEntityId(), max);
                lastMaxCharge.put(player.getEntityId(), max);
            } else {
                runicInfo.remove(player.getEntityId());
            }
        }

        if (runicInfo.containsKey(player.getEntityId())) {
            if (!nextCycle.containsKey(player.getEntityId())) {
                nextCycle.put(player.getEntityId(), 0L);
            }

            long time = System.currentTimeMillis();
            charge = (int) player.getAbsorptionAmount();
            if (charge == 0 && lastCharge.containsKey(player.getEntityId()) && lastCharge.get(player.getEntityId()) > 0) {
                nextCycle.put(player.getEntityId(), time + (long) ModConfig.CONFIG_MISC.shieldWait);
                lastCharge.put(player.getEntityId(), 0);
            }

            if (Math.min(charge + 5, runicInfo.get(player.getEntityId()) - (charge + 5)) < runicInfo.get(player.getEntityId()))
                if (nextCycle.get(player.getEntityId()) < time && !AuraHandler.shouldPreserveAura(player.world, player, player.getPosition()) && AuraHelper.getVis(player.world, new BlockPos(player)) >= (float) ModConfig.CONFIG_MISC.shieldCost) {
                    AuraHandler.drainVis(player.world, new BlockPos(player), (float) ModConfig.CONFIG_MISC.shieldCost, false);
                    nextCycle.put(player.getEntityId(), time + (long) ModConfig.CONFIG_MISC.shieldRecharge);
                    player.setAbsorptionAmount((float) Math.min(charge + 5, runicInfo.get(player.getEntityId())));
                    lastCharge.put(player.getEntityId(), Math.min(charge + 5, runicInfo.get(player.getEntityId())));
                }
        }
    }
}