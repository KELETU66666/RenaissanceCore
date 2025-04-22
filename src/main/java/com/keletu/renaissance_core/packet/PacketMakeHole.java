package com.keletu.renaissance_core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.casters.foci.FocusEffectRift;

public class PacketMakeHole implements IMessage, IMessageHandler<PacketMakeHole, IMessage> {
    private double x,y,z;
    public PacketMakeHole(){

    }
    public PacketMakeHole(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
    }

    public void fromBytes(ByteBuf buffer) {
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
    }

    public IMessage onMessage(PacketMakeHole message, MessageContext ctx) {
        FocusEffectRift.createHole(Thaumcraft.proxy.getClientWorld(), new BlockPos(MathHelper.floor(message.x), MathHelper.floor(message.y)-1, MathHelper.floor(message.z)), EnumFacing.UP, (byte) 33, 120);
        return null;
    }
}