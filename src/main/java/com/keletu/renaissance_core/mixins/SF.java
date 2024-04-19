package com.keletu.renaissance_core.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SF {
    public static void copyKnowledge(Object a, Object b) {
        IPlayerKnowledge aBrains = null, bBrains = null;
        if (a instanceof EntityLivingBase)
            aBrains = ((EntityLivingBase) a).getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        else if (a instanceof TileEntity)
            aBrains = ((TileEntity) a).getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);

        if (b instanceof EntityLivingBase)
            bBrains = ((EntityLivingBase) b).getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        else if (b instanceof TileEntity)
            bBrains = ((TileEntity) b).getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);

        if (aBrains != null && bBrains != null) {
            for (String k : aBrains.getResearchList()) {
                bBrains.addResearch(k);
                bBrains.setResearchStage(k, aBrains.getResearchStage(k));
            }
        }
    }
}