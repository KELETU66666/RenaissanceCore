package com.keletu.renaissance_core.packet;

import com.keletu.renaissance_core.entity.EntityVengefulGolem;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketEnslave implements IMessage, IMessageHandler<PacketEnslave, IMessage> {
    boolean action;
    public PacketEnslave(){

    }
    public PacketEnslave(String name, boolean action){
        this.action = action;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        action = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(action);
    }

    @Override
    public IMessage onMessage(PacketEnslave message, MessageContext ctx) {
        EntityVengefulGolem.isEnslaved = message.action;
        return null;
    }
}