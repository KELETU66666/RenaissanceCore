package com.keletu.renaissance_core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.client.fx.FXDispatcher;

public class PacketZapParticle implements IMessage {
	
	private double x;
	private double y;
	private double z;

	public PacketZapParticle() {
	}

	public PacketZapParticle(double x, double y, double z) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
	  }

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
	}

	public static class Handler implements IMessageHandler<PacketZapParticle, IMessage> {
		@Override
		public IMessage onMessage(PacketZapParticle message, MessageContext ctx) {
			EntityPlayerSP player = Minecraft.getMinecraft().player;

			new FXDispatcher().arcLightning(player.posX, player.posY - 1, player.posZ, message.x, message.y, message.z, 0.2F, 0.5F, 1, 1);

			return null;
		}
	}
}