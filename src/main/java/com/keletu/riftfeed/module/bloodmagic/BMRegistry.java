package com.keletu.riftfeed.module.bloodmagic;

import WayofTime.bloodmagic.ConfigHandler;
import WayofTime.bloodmagic.altar.AltarTier;
import WayofTime.bloodmagic.altar.ComponentType;
import WayofTime.bloodmagic.api.BloodMagicPlugin;
import WayofTime.bloodmagic.api.IBloodMagicAPI;
import WayofTime.bloodmagic.api.IBloodMagicPlugin;
import WayofTime.bloodmagic.api.IBloodMagicRecipeRegistrar;
import WayofTime.bloodmagic.block.BlockDecorative;
import WayofTime.bloodmagic.block.enums.EnumDecorative;
import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks;
import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.core.registry.AltarRecipeRegistry;
import WayofTime.bloodmagic.item.ItemSlate;
import com.keletu.riftfeed.blocks.RFBlocks;
import com.keletu.riftfeed.items.RFItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.oredict.OreIngredient;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

@BloodMagicPlugin
public class BMRegistry implements IBloodMagicPlugin {
    private static IBloodMagicAPI api;

    @Override
    public void register(IBloodMagicAPI api) {
        BMRegistry.api = api;

        setAltarComponent(RFBlocks.thaumic_rune, "BLOODRUNE");
        setAltarComponent(RFBlocks.mana_rune, "BLOODRUNE");
        setAltarComponent(RFBlocks.bloody_thaumium, "GLOWSTONE");
        setAltarComponent(RFBlocks.bloody_void, "BLOODSTONE");

        removeAltarComponent(Blocks.GLOWSTONE, "GLOWSTONE");
        removeAltarComponent(Blocks.SEA_LANTERN, "GLOWSTONE");
        BlockDecorative decorative = (BlockDecorative) RegistrarBloodMagicBlocks.DECORATIVE_BRICK;
        api.unregisterAltarComponent(decorative.getDefaultState().withProperty(decorative.getProperty(), EnumDecorative.BLOODSTONE_BRICK), ComponentType.BLOODSTONE.name());
        api.unregisterAltarComponent(decorative.getDefaultState().withProperty(decorative.getProperty(), EnumDecorative.BLOODSTONE_TILE), ComponentType.BLOODSTONE.name());

        if (ConfigHandler.general.enableTierSixEvenThoughThereIsNoContent) {
            setAltarComponent(RFBlocks.bloody_ichorium, "CRYSTAL");
            api.unregisterAltarComponent(decorative.getDefaultState().withProperty(decorative.getProperty(), EnumDecorative.CRYSTAL_BRICK), ComponentType.CRYSTAL.name());
            api.unregisterAltarComponent(decorative.getDefaultState().withProperty(decorative.getProperty(), EnumDecorative.CRYSTAL_TILE), ComponentType.CRYSTAL.name());
        }
    }

    private static void setAltarComponent(Block block, String componentType) {
            for (IBlockState state : block.getBlockState().getValidStates())
                api.registerAltarComponent(state, componentType);
    }

    private static void removeAltarComponent(Block block, String componentType) {
        for (IBlockState state : block.getBlockState().getValidStates())
            api.unregisterAltarComponent(state, componentType);
    }

    @Override
    public void registerRecipes(IBloodMagicRecipeRegistrar recipeRegistrar) {
        recipeRegistrar.addBloodAltar(new OreIngredient("blockThaumium"), new ItemStack(RFBlocks.bloody_thaumium), AltarTier.TWO.ordinal(), 10000, 50, 25);
        recipeRegistrar.addBloodAltar(new OreIngredient("blockVoid"),  new ItemStack(RFBlocks.bloody_void), AltarTier.THREE.ordinal(), 30000, 150, 100);

        recipeRegistrar.addBloodAltar(Ingredient.fromItem(RFItems.thaumic_slate), ItemSlate.SlateType.REINFORCED.getStack(), AltarTier.TWO.ordinal(), 1500, 20, 20);
        recipeRegistrar.addBloodAltar(Ingredient.fromItem(RFItems.mana_slate), ItemSlate.SlateType.REINFORCED.getStack(), AltarTier.TWO.ordinal(), 1500, 20, 20);
    }
}