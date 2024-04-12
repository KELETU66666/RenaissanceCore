package com.keletu.renaissance_core.village;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

public class ComponentWizardTower extends StructureVillagePieces.Village {
    private int averageGroundLevel = -1;

    public ComponentWizardTower(StructureVillagePieces.Start par1ComponentVillageStartPiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, EnumFacing par5) {
        super(par1ComponentVillageStartPiece, par2);
        this.setCoordBaseMode(par5);
        this.boundingBox = par4StructureBoundingBox;
    }

    public static Object buildComponent(StructureVillagePieces.Start startPiece, List pieces, Random random, int par3, int par4, int par5, EnumFacing par6, int par7) {
        StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 5, 12, 5, par6);
        return (!canVillageGoDeeper(box)) || (StructureComponent.findIntersecting(pieces, box) != null) ? null : new ComponentWizardTower(startPiece, par7, random, box, par6);
    }

    public boolean addComponentParts(World world, Random par2Random, StructureBoundingBox bb) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, bb);
            if (this.averageGroundLevel < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 12 - 1, 0);
        }
        this.fillWithBlocks(world, bb, 2, 1, 2, 4, 11, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 2, 0, 2, 4, 0, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 2, 5, 2, 4, 5, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 2, 10, 2, 4, 10, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 1, 0, 2, 1, 11, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 2, 0, 1, 4, 11, 1, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 5, 0, 2, 5, 11, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 2, 0, 5, 4, 11, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(world, bb, 2, 0, 5, 4, 11, 5, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 1, 0, 1, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 1, 0, 5, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 5, 0, 1, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 5, 0, 5, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 1, 5, 1, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 1, 5, 5, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 5, 5, 1, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 5, 5, 5, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 1, 10, 1, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 1, 10, 5, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 5, 10, 1, bb);
        this.setBlockState(world, Blocks.COBBLESTONE.getStateFromMeta(3), 5, 10, 5, bb);
        this.setBlockState(world, Blocks.GLASS_PANE.getStateFromMeta(0), 3, 7, 1, bb);
        this.setBlockState(world, Blocks.GLASS_PANE.getStateFromMeta(0), 3, 8, 1, bb);
        this.setBlockState(world, Blocks.GLASS_PANE.getStateFromMeta(0), 3, 7, 5, bb);
        this.setBlockState(world, Blocks.GLASS_PANE.getStateFromMeta(0), 3, 8, 5, bb);
        this.setBlockState(world, Blocks.GLASS_PANE.getStateFromMeta(0), 3, 2, 5, bb);
        this.setBlockState(world, Blocks.GLASS_PANE.getStateFromMeta(0), 3, 3, 5, bb);
        int var4 = Blocks.LADDER.getMetaFromState(Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.WEST));

        for (int var5 = 1; var5 <= 9; ++var5) {
            this.setBlockState(world, Blocks.LADDER.getStateFromMeta(var4), 4, var5, 3, bb);
        }
        this.setBlockState(world, Blocks.TRAPDOOR.getStateFromMeta(var4), 4, 10, 3, bb);
        this.setBlockState(world, Blocks.GLOWSTONE.getDefaultState(), 3, 5, 3, bb);
        //ChestGenHooks cgh = new ChestGenHooks("towerChestContents", towerChestContents, 4, 9);
        this.generateChest(world, bb, par2Random, 2, 6, 2, RFLoots.TE_TOWER);
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 3, 1, 1, bb);
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 3, 2, 1, bb);
        this.createVillageDoor(world, bb, par2Random, 3, 1, 1, EnumFacing.NORTH);
        if (Block.isEqualTo(this.getBlockStateFromPos(world, 3, 0, 0, bb).getBlock(), Blocks.AIR) && !Block.isEqualTo(this.getBlockStateFromPos(world, 3, -1, 0, bb).getBlock(), Blocks.AIR)) {
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMeta(3), 3, 0, 0, bb);
        }
        for (int var5 = 0; var5 < 7; ++var5) {
            for (int var6 = 0; var6 < 7; ++var6) {
                this.clearCurrentPositionBlocksUpwards(world, var6, 12, var5, bb);
                this.replaceAirAndLiquidDownwards(world, Blocks.COBBLESTONE.getDefaultState(), var6, -1, var5, bb);
            }
        }
        this.spawnVillagers(world, bb, 7, 1, 1, 1);
        return true;
    }

    protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof)
    {
        return VillageWizardManager.prof;
    }
}
