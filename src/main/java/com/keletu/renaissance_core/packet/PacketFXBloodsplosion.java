package com.keletu.renaissance_core.packet;

import com.keletu.renaissance_core.RenaissanceCore;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.Thaumcraft;

public class PacketFXBloodsplosion implements IMessage, IMessageHandler<PacketFXBloodsplosion, IMessage> {
    private double x;
    private double y;
    private double z;

    public PacketFXBloodsplosion() {
    }

    public PacketFXBloodsplosion(double x, double y, double z) {
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

    public IMessage onMessage(PacketFXBloodsplosion message, MessageContext ctx) {
        for (int i = 0; i < 50; i++) {
            RenaissanceCore.proxy.bloodsplosion(Thaumcraft.proxy.getClientWorld(), message.x, message.y, message.z);
        }
        return null;
    }
}