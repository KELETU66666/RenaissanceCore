package com.keletu.renaissance_core.village;

import com.keletu.renaissance_core.items.RFItems;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;

import java.util.List;
import java.util.Random;

public class VillageWizardManager implements VillagerRegistry.IVillageCreationHandler {

    public static final VillagerRegistry.VillagerProfession prof = new VillagerRegistry.VillagerProfession(
            "renaissance_core:wizard",
            "renaissance_core:textures/models/entity/wizard.png",
            "minecraft:textures/entity/zombie_villager/zombie_villager.png");

    public static void registerUselessVillager() {


        GameRegistry.findRegistry(VillagerRegistry.VillagerProfession.class).register(prof);

        new VillagerRegistry.VillagerCareer(prof, "wizard").addTrade(1, new ItemForItems());

    }

    public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
        return new StructureVillagePieces.PieceWeight(ComponentWizardTower.class, 15, MathHelper.getInt(random, i, 1 + i));
    }

    public Class<?> getComponentClass() {
        return ComponentWizardTower.class;
    }


    public StructureVillagePieces.Village buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing enumFacing, int p5) {
        return (StructureVillagePieces.Village) ComponentWizardTower.buildComponent(startPiece, pieces, random, p1, p2, p3, enumFacing, p5);
    }

    public static class ItemForItems implements EntityVillager.ITradeList {
        public ItemForItems() {
        }

        public void addMerchantRecipe(IMerchant merchant, MerchantRecipeList recipeList, Random random) {
        //    if (merchant.getProfession() == 190) {
                recipeList.add(new MerchantRecipe(new ItemStack(Items.GOLD_NUGGET, 20 + random.nextInt(3)), new ItemStack(Items.EMERALD)));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD), new ItemStack(ItemsTC.curio, 1, 2)));
                recipeList.add(new MerchantRecipe(new ItemStack(ItemsTC.quicksilver, 4 + random.nextInt(3), 0), new ItemStack(Items.EMERALD)));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD), new ItemStack(ItemsTC.alumentum, 1, 0)));
                recipeList.add(new MerchantRecipe(new ItemStack(ItemsTC.amber, 4 + random.nextInt(3), 0), new ItemStack(Items.EMERALD)));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD), new ItemStack(BlocksTC.nitor.get(EnumDyeColor.YELLOW))));
                recipeList.add(new MerchantRecipe(new ItemStack(ItemsTC.chunks, 24 + random.nextInt(8), 1), new ItemStack(Items.EMERALD)));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.BOOK, 4 + random.nextInt(3), 0), new ItemStack(ItemsTC.curio, 1, 2)));
                recipeList.add(new MerchantRecipe(new ItemStack(ItemsTC.chunks, 24 + random.nextInt(8), 0), new ItemStack(Items.EMERALD)));
                //recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD), new ItemStack(ConfigItems.itemShard, 2 + random.nextInt(2), random.nextInt(6))));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD), new ItemStack(RFItems.mana_bean, 1 + random.nextInt(2), 0)));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 5 + random.nextInt(3)), new ItemStack(ItemsTC.bathSalts, 1, 0)));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 5 + random.nextInt(3)), new ItemStack(ItemsTC.baubles, 1, 3)));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 5 + random.nextInt(3)), new ItemStack(ItemsTC.amuletVis, 1, 0)));
                recipeList.add(new MerchantRecipe(new ItemStack(Items.EMERALD, 5 + random.nextInt(3)), new ItemStack(ItemsTC.baubles, 1, 3 + random.nextInt(6))));
            }
    //    }
    }
}