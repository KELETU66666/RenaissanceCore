package com.keletu.renaissance_core.packet;

import com.keletu.renaissance_core.RenaissanceCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenPackGui implements IMessage {

    public PacketOpenPackGui() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<PacketOpenPackGui, IMessage> {
        @Override
        public IMessage onMessage(PacketOpenPackGui message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                if (!player.world.isRemote) {
                    player.openGui(RenaissanceCore.MODID, 0, player.world,
                            MathHelper.floor(player.posX),
                            MathHelper.floor(player.posY),
                            MathHelper.floor(player.posZ));
                }
            });
            return null;
        }
    }
}