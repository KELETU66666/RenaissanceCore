package com.keletu.renaissance_core.entity;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.packet.PacketMakeHole;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.casters.foci.FocusEffectRift;

public class EntityUpcomingHole extends Entity {
    public EntityUpcomingHole(World w) {
        super(w);
        this.setInvisible(true);
        this.motionX = 0;
        this.motionY = 0;
        this.motionZ = 0;
    }

    @Override
    protected void entityInit() {

    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(!this.world.isRemote) {
            if (ticksExisted >= 30) {
                FocusEffectRift.createHole(world, new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY) - 1, MathHelper.floor(posZ)), EnumFacing.UP, (byte) 33, 120);
                RenaissanceCore.packetInstance.sendToAllAround(new PacketMakeHole(posX, posY, posZ), new NetworkRegistry.TargetPoint(world.provider.getDimension(), posX, posY, posZ, 32.0F));
                this.setDead();
            }
        } else {
            FXDispatcher.INSTANCE.sparkle((float)posX + this.world.rand.nextFloat(), (float)posY+0.1F, (float)posZ + this.world.rand.nextFloat(), 2, 0, 0);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {

    }

}