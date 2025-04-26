package com.keletu.renaissance_core.packet;

import com.keletu.renaissance_core.capability.ThaumicConciliumCap;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;

public class PacketSyncPlayer implements IMessage, IMessageHandler<PacketSyncPlayer, IMessage> {

    private NBTTagCompound data;

    public PacketSyncPlayer(){

    }
    public PacketSyncPlayer(NBTTagCompound data){
        this.data = data;
    }

    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeTag(buffer, this.data);
    }

    public void fromBytes(ByteBuf buffer) {
        this.data = ByteBufUtils.readTag(buffer);
    }

    public IMessage onMessage(PacketSyncPlayer message, MessageContext ctx) {
        World world = Thaumcraft.proxy.getClientWorld();
        if (world != null) {
                EntityPlayer player = world.getPlayerEntityByName(message.data.getString("SyncName"));
                if (player != null) {
                    ThaumicConciliumCap capabilities = ThaumicConciliumCap.get(player);
                    if (capabilities != null) {
                        capabilities.deserializeNBT(message.data);
                    }
                }
            }

        return null;
    }
}