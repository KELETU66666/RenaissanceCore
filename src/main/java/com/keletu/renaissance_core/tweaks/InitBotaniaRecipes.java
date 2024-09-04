package com.keletu.renaissance_core.tweaks;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.util.DummyRecipe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.block.ModFluffBlocks;
import vazkii.botania.common.item.ModItems;

public class InitBotaniaRecipes {

    public static void replaceWithVanillaRecipes() {

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "fertilizer_powder"));
        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "fertilizer_dye"));

        int[] meta = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        for (int i : meta) {
            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(RenaissanceCore.MODID, "mushroom_to_flower" + i), new CrucibleRecipe(
                    "BASICBOTABY",
                    new ItemStack(ModBlocks.flower, 1, i),
                    new ItemStack(ModBlocks.mushroom, 1, i),
                    new AspectList().add(Aspect.EXCHANGE, 2).add(Aspect.PLANT, 2)));

            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(RenaissanceCore.MODID, "flower_to_mushroom" + i), new CrucibleRecipe(
                    "BOTANYDECREASE",
                    new ItemStack(ModBlocks.mushroom, 1, i),
                    new ItemStack(ModBlocks.flower, 1, i),
                    new AspectList().add(Aspect.DARKNESS, 2)));

            ThaumcraftApi.addCrucibleRecipe(new ResourceLocation(RenaissanceCore.MODID, "flower_dupe" + i), new CrucibleRecipe(
                    "FLOWERDUPE",
                    new ItemStack(ModBlocks.flower, 2, i),
                    new ItemStack(ModBlocks.flower, 1, i),
                    new AspectList().add(Aspect.SENSES, 5).add(Aspect.PLANT, 5).add(Aspect.LIFE, 5)));

            for (EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
                ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "flower_exchange" + i + "_" + enumdyecolor.getMetadata()), new InfusionRecipe("FLOWEREXCHANGE", new ItemStack(ModBlocks.flower, 1, i),
                        7,
                        new AspectList().add(Aspect.PLANT, 10).add(Aspect.SENSES, 10).add(Aspect.LIFE, 3),
                        new ItemStack(ModBlocks.flower, 1, enumdyecolor.getMetadata()),
                        new ItemStack(Items.DYE, 1, Math.max(-i + 15, 0)),
                        new ItemStack(Items.DYE, 1, Math.max(-i + 15, 0)),
                        new ItemStack(Items.DYE, 1, Math.max(-i + 15, 0)),
                        new ItemStack(Items.DYE, 1, Math.max(-i + 15, 0))
                ));
            }
        }

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "brewery"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "botany_brewery"), new InfusionRecipe("PLANTBREWERY", new ItemStack(ModBlocks.brewery),
                7,
                new AspectList().add(Aspect.MAN, 30).add(Aspect.MECHANISM, 50).add(Aspect.ENTROPY, 30),
                Items.BREWING_STAND,
                "ingotGold",
                new ItemStack(ModBlocks.manaGlass),
                new ItemStack(ModFluffBlocks.livingrockSlab),
                new ItemStack(ModFluffBlocks.livingrockSlab),
                new ItemStack(ModFluffBlocks.livingrockSlab),
                new ItemStack(ModBlocks.manaGlass),
                "ingotGold",
                new ItemStack(ModBlocks.manaGlass),
                new ItemStack(ModFluffBlocks.livingrockSlab),
                new ItemStack(ModFluffBlocks.livingrockSlab),
                new ItemStack(ModFluffBlocks.livingrockSlab),
                new ItemStack(ModBlocks.manaGlass)
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "virus_0"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "virus_0"), new ShapelessArcaneRecipe(new ResourceLocation(""),
                "HORSEVIRUS",
                50,
                new AspectList().add(Aspect.WATER, 2).add(Aspect.ENTROPY, 2),
                new ItemStack(ModItems.virus, 1, 0),
                new Object[]{
                        new ItemStack(Items.SKULL, 1, 2),
                        ModItems.vineBall,
                        new ItemStack(BlocksTC.crystalTaint),
                        "manaPearl",
                        Items.MAGMA_CREAM,
                        Items.FERMENTED_SPIDER_EYE
                })
        );
        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "virus_1"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "virus_1"), new ShapelessArcaneRecipe(new ResourceLocation(""),
                "HORSEVIRUS",
                50,
                new AspectList().add(Aspect.WATER, 2).add(Aspect.ENTROPY, 2),
                new ItemStack(ModItems.virus, 1, 1),
                new Object[]{
                        new ItemStack(Items.SKULL, 1, 0),
                        ModItems.vineBall,
                        new ItemStack(BlocksTC.crystalTaint),
                        "manaPearl",
                        Items.MAGMA_CREAM,
                        Items.FERMENTED_SPIDER_EYE
                })
        );

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "vial_1"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "vial_1"), new ShapedArcaneRecipe(new ResourceLocation(""),
                "LARGERVIAL",
                75,
                new AspectList().add(Aspect.WATER, 2),
                new ItemStack(ModItems.vial, 1, 1),
                "GBG",
                "G G",
                "GGG",
                'B', new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation("thaumicaugmentation", "warded_button_greatwood"))),
                'G', new ItemStack(ModBlocks.elfGlass)
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "runealtar"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "rune_altar"), new InfusionRecipe("BOTANYALTAR@3", new ItemStack(ModBlocks.runeAltar),
                8,
                new AspectList().add(Aspect.AURA, 60).add(Aspect.CRAFT, 60).add(Aspect.PLANT, 100).add(Aspect.EXCHANGE, 30),
                new ItemStack(BlocksTC.infusionMatrix),
                "livingrock",
                "ingotManasteel",
                "gemPrismarine",
                "manaPearl",
                "livingrock",
                "ingotManasteel",
                "gemPrismarine",
                "manaPearl"
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "alchemycatalyst"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "alchemycatalyst"), new ShapedArcaneRecipe(new ResourceLocation(""),
                "ALCHEMYCATALYST",
                200,
                new AspectList().add(Aspect.ORDER, 2).add(Aspect.WATER, 1).add(Aspect.EARTH, 1),
                new ItemStack(ModBlocks.alchemyCatalyst),
                "LPL",
                "BAB",
                "LSL",
                'L', "livingrock",
                'P', "manaPearl",
                'S', "ingotManasteel",
                'B', Items.BREWING_STAND,
                'A', new ItemStack(BlocksTC.metalAlchemical)
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "terraplate"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "terraplate"), new InfusionRecipe("TERRAPLATE@3", new ItemStack(ModBlocks.terraPlate),
                12,
                new AspectList().add(Aspect.AIR, 125).add(Aspect.WATER, 125).add(Aspect.FIRE, 125).add(Aspect.EARTH, 125).add(Aspect.ENTROPY, 125).add(Aspect.ORDER, 125).add(Aspect.MAGIC, 75),
                new ItemStack(ModBlocks.runeAltar),
                "runeWaterB",
                "runeFireB",
                "runeEarthB",
                "runeAirB",
                "runeSpringB",
                "runeSummerB",
                "runeAutumnB",
                "runeWinterB",
                "runeManaB",
                "runeLustB",
                "runeGluttonyB",
                "runeGreedB",
                "runeSlothB",
                "runeWrathB",
                "runeEnvyB",
                "runePrideB"
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "terrasword"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "terrasword"), new InfusionRecipe("TERRASWORD", new ItemStack(ModItems.terraSword),
                8,
                new AspectList().add(Aspect.EARTH, 110).add(Aspect.EXCHANGE, 75).add(Aspect.LIGHT, 75).add(Aspect.AVERSION, 50),
                ItemsTC.elementalSword,
                "ingotTerrasteel",
                "ingotTerrasteel"
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "terrapick"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "terrapick"), new InfusionRecipe("TERRAPICK", new ItemStack(ModItems.terraPick),
                8,
                new AspectList().add(Aspect.EARTH, 110).add(Aspect.EXCHANGE, 75).add(Aspect.ENTROPY, 75).add(Aspect.TOOL, 50),
                ItemsTC.elementalPick,
                "ingotTerrasteel",
                "ingotTerrasteel"
        ));


        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "terraaxe"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "terraaxe"), new InfusionRecipe("TERRAAXE", new ItemStack(ModItems.terraAxe),
                8,
                new AspectList().add(Aspect.EARTH, 110).add(Aspect.PLANT, 75).add(Aspect.ENTROPY, 75).add(Aspect.TOOL, 50),
                ItemsTC.elementalAxe,
                "ingotTerrasteel",
                "ingotTerrasteel"
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "livingwood_5"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "livingwood_glowing"), new ShapedArcaneRecipe(new ResourceLocation(""),
                "ALFPORTAL",
                100,
                new AspectList().add(Aspect.WATER, 5).add(Aspect.EARTH, 5),
                new ItemStack(ModBlocks.livingwood, 4, 5),
                "LPL",
                "PAP",
                "LPL",
                'L', "ingotTerrasteel",
                'P', "livingwood",
                'A', "glowstone"
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "alfheimportal"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "alfheimportal"), new InfusionRecipe("ALFPORTAL", new ItemStack(ModBlocks.alfPortal),
                16,
                new AspectList().add(Aspect.VOID, 125).add(Aspect.EXCHANGE, 125).add(Aspect.PLANT, 125),
                new ItemStack(ModBlocks.alchemyCatalyst),
                "ingotVoid",
                "ingotTerrasteel",
                new ItemStack(ModBlocks.livingwood, 1, 5),
                new ItemStack(BlocksTC.metalAlchemical),
                "glowstone",
                "ingotVoid",
                "ingotTerrasteel",
                new ItemStack(ModBlocks.livingwood, 1, 5),
                new ItemStack(BlocksTC.metalAlchemical),
                "glowstone"
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "pylon_0"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "pylon_mana"), new ShapedArcaneRecipe(new ResourceLocation(""),
                "MANACRYSTAL",
                100,
                new AspectList().add(Aspect.WATER, 2).add(Aspect.ORDER, 2).add(Aspect.AIR, 2),
                new ItemStack(ModBlocks.pylon, 1, 0),
                "LHL",
                "PAP",
                "LHL",
                'L', new ItemStack(BlocksTC.crystalWater),
                'H', new ItemStack(ModBlocks.storage, 1, 0),
                'P', "ingotGold",
                'A', new ItemStack(ModBlocks.storage, 1, 3)
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "pylon_1"));
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "pylon_alf"), new ShapedArcaneRecipe(new ResourceLocation(""),
                "ALFCRYSTAL",
                100,
                new AspectList().add(Aspect.WATER, 5).add(Aspect.ORDER, 10).add(Aspect.AIR, 5),
                new ItemStack(ModBlocks.pylon, 1, 1),
                "LPL",
                "PAP",
                "LPL",
                'L', new ItemStack(BlocksTC.crystalEarth),
                'P', "ingotTerrasteel",
                'A', new ItemStack(ModBlocks.pylon, 1, 0)
        ));

        DummyRecipe.removeRecipeByName(new ResourceLocation("botania", "pylon_2"));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation(RenaissanceCore.MODID, "pylon_boss"), new InfusionRecipe("WORLDOFGAIA", new ItemStack(ModBlocks.pylon, 1, 2),
                66,
                new AspectList().add(Aspect.DARKNESS, 66).add(Aspect.EXCHANGE, 66).add(Aspect.TRAP, 66).add(Aspect.CRYSTAL, 66).add(Aspect.PLANT, 66),
                new ItemStack(ModBlocks.pylon, 1, 0),
                new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumicaugmentation", "material")), 1, 5),
                "elvenDragonstone",
                new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumicaugmentation", "material")), 1, 5),
                "elvenDragonstone",
                new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumicaugmentation", "material")), 1, 5),
                "elvenDragonstone"
        ));
    }

}
