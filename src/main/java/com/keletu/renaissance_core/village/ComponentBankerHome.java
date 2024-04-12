// Decompiled with: CFR 0.152
// Class Version: 6
package com.keletu.renaissance_core.village;

import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

public class ComponentBankerHome extends StructureVillagePieces.Village {
    private boolean isTallHouse;
    private int tablePosition;

    public ComponentBankerHome() {
    }

    public ComponentBankerHome(StructureVillagePieces.Start p_i2101_1_, int p_i2101_2_, Random p_i2101_3_, StructureBoundingBox p_i2101_4_, EnumFacing base) {
        super(p_i2101_1_, p_i2101_2_);
        this.setCoordBaseMode(base);
        this.boundingBox = p_i2101_4_;
        this.isTallHouse = p_i2101_3_.nextBoolean();
        this.tablePosition = p_i2101_3_.nextInt(3);
    }

    protected void writeStructureToNBT(NBTTagCompound p_143012_1_) {
        super.writeStructureToNBT(p_143012_1_);
        p_143012_1_.setInteger("T", this.tablePosition);
        p_143012_1_.setBoolean("C", this.isTallHouse);
    }

    protected void readStructureFromNBT(NBTTagCompound tagCompound, TemplateManager manager) {
        super.readStructureFromNBT(tagCompound, manager);
        this.tablePosition = tagCompound.getInteger("T");
        this.isTallHouse = tagCompound.getBoolean("C");
    }

    public static Object buildComponent(StructureVillagePieces.Start p_74908_0_, List p_74908_1_, Random p_74908_2_, int p_74908_3_, int p_74908_4_, int p_74908_5_, EnumFacing p_74908_6_, int p_74908_7_) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p_74908_3_, p_74908_4_, p_74908_5_, 0, 0, 0, 4, 6, 5, p_74908_6_);
        return ComponentBankerHome.canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(p_74908_1_, structureboundingbox) == null ? new ComponentBankerHome(p_74908_0_, p_74908_7_, p_74908_2_, structureboundingbox, p_74908_6_) : null;
    }

    public boolean addComponentParts(World world, Random rand, StructureBoundingBox bbox) {
        if (this.averageGroundLvl < 0) {
            this.averageGroundLvl = this.getAverageGroundLevel(world, bbox);
            if (this.averageGroundLvl < 0) {
                return true;
            }
            this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.maxY + 6 - 1, 0);
        }
        this.fillWithBlocks(world, bbox, 1, 1, 1, 3, 5, 4, Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 0, 0, 0, 3, 0, 4, Blocks.COBBLESTONE.getDefaultState(), Blocks.COBBLESTONE.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 1, 0, 1, 2, 0, 3, Blocks.DIRT.getDefaultState(), Blocks.DIRT.getDefaultState(), false);
        if (this.isTallHouse) {
            this.fillWithBlocks(world, bbox, 1, 4, 1, 2, 4, 3, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        } else {
            this.fillWithBlocks(world, bbox, 1, 5, 1, 2, 5, 3, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        }
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 1, 4, 0, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 2, 4, 0, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 1, 4, 4, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 2, 4, 4, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 4, 1, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 4, 2, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 0, 4, 3, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 3, 4, 1, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 3, 4, 2, bbox);
        this.setBlockState(world, Blocks.LOG.getDefaultState(), 3, 4, 3, bbox);
        this.fillWithBlocks(world, bbox, 0, 1, 0, 0, 3, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 3, 1, 0, 3, 3, 0, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 0, 1, 4, 0, 3, 4, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 3, 1, 4, 3, 3, 4, Blocks.LOG.getDefaultState(), Blocks.LOG.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 0, 1, 1, 0, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 3, 1, 1, 3, 3, 3, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 1, 1, 0, 2, 3, 0, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.fillWithBlocks(world, bbox, 1, 1, 4, 2, 3, 4, Blocks.PLANKS.getDefaultState(), Blocks.PLANKS.getDefaultState(), false);
        this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 0, 2, 2, bbox);
        this.setBlockState(world, Blocks.IRON_BARS.getDefaultState(), 3, 2, 2, bbox);
        if (this.tablePosition > 0) {
            this.setBlockState(world, Blocks.COBBLESTONE_WALL.getDefaultState(), this.tablePosition, 1, 3, bbox);
            this.setBlockState(world, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), this.tablePosition, 2, 3, bbox);
        }
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 1, 0, bbox);
        this.setBlockState(world, Blocks.AIR.getDefaultState(), 1, 2, 0, bbox);
        this.createVillageDoor(world, bbox, rand, 3, 1, 1, EnumFacing.NORTH);
        if (this.getBlockStateFromPos(world, 1, 0, -1, bbox).getMaterial() == Material.AIR && this.getBlockStateFromPos(world, 1, -1, -1, bbox).getMaterial() != Material.AIR) {
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMeta(3), 1, 0, -1, bbox);
        }
        for (int i = 0; i < 5; ++i) {
            for (int j = 0; j < 4; ++j) {
                this.clearCurrentPositionBlocksUpwards(world, j, 6, i, bbox);
                this.replaceAirAndLiquidDownwards(world, Blocks.COBBLESTONE.getDefaultState(), j, -1, i, bbox);
            }
        }
        this.spawnVillagers(world, bbox, 1, 1, 2, 1);
        return true;
    }

    protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof)
    {
        return VillageBankerManager.prof;
    }
}