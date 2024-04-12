package com.keletu.renaissance_core.village;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import thaumcraft.api.items.ItemsTC;

import java.util.List;
import java.util.Random;

public class VillageBankerManager implements VillagerRegistry.IVillageCreationHandler {

    public static final VillagerRegistry.VillagerProfession prof = new VillagerRegistry.VillagerProfession(
            "renaissance_core:banker",
            "renaissance_core:textures/models/entity/banker.png",
            "minecraft:textures/entity/zombie_villager/zombie_villager.png");

    public static void registerUselessVillager() {


        GameRegistry.findRegistry(VillagerRegistry.VillagerProfession.class).register(prof);

        new VillagerRegistry.VillagerCareer(prof, "banker").addTrade(1, new ItemForItems());

    }

    public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
        return new StructureVillagePieces.PieceWeight(ComponentWizardTower.class, 25, MathHelper.getInt(random, i, 1 + i));
    }

    public Class<?> getComponentClass() {
        return ComponentBankerHome.class;
    }


    public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing enumFacing, int p5) {
        return (StructureVillagePieces.Village) ComponentBankerHome.buildComponent(startPiece, pieces, random, p1, p2, p3, enumFacing, p5);
    }

    public static class ItemForItems implements EntityVillager.ITradeList {
        public ItemForItems() {
        }

        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
        //    if (merchant.getProfession() == 190) {
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 20 + random.nextInt(3)), new ItemStack(Items.EMERALD)));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 2 + random.nextInt(2)), Items.ARROW));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 6 + random.nextInt(3)), Item.getItemFromBlock(Blocks.WOOL)));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 3 + random.nextInt(2)), Items.PAPER));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 7 + random.nextInt(3)), Items.BOOK));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 16 + random.nextInt(5)), Items.EXPERIENCE_BOTTLE));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 9 + random.nextInt(4)), Item.getItemFromBlock(Blocks.GLOWSTONE)));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 2 + random.nextInt(2)), Items.COAL));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 22 + random.nextInt(3)), Items.DIAMOND));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 6 + random.nextInt(3)), Items.IRON_INGOT));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 10 + random.nextInt(3)), new ItemStack(ItemsTC.ingots, 1, 0)));
            recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 25 + random.nextInt(8)), Items.SADDLE));
            }
    //    }
    }
}