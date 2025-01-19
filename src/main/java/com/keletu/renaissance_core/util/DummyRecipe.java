package com.keletu.renaissance_core.util;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/*
 * This class was created by <TeamGregtechCEu>. It's distributed as
 * part of the GregtechCE: Unofficial Mod. Get the Source Code in github:
 * https://github.com/GregTechCEu/GregTech
 *
 * GregtechCE: Unofficial is Open Source and distributed under the
 * GPLv3.0 License: https://www.gnu.org/licenses/lgpl-3.0.en.html
 */
public class DummyRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    @Override
    public boolean matches(@Nonnull final InventoryCrafting inv, @Nonnull final World worldIn) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull final InventoryCrafting inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(final int width, final int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    public static void removeRecipeByName(@Nonnull ResourceLocation location) {
        ForgeRegistries.RECIPES.register(new DummyRecipe().setRegistryName(location));
    }
}