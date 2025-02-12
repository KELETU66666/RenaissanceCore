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
	private double x1;
	private double y1;
	private double z1;

	public PacketZapParticle() {
	}

	public PacketZapParticle(double x, double y, double z, double x1, double y1, double z1) {
	    this.x = x;
	    this.y = y;
	    this.z = z;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
	  }

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		x1 = buf.readDouble();
		y1 = buf.readDouble();
		z1 = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(this.x);
		buf.writeDouble(this.y);
		buf.writeDouble(this.z);
		buf.writeDouble(this.x1);
		buf.writeDouble(this.y1);
		buf.writeDouble(this.z1);
	}

	public static class Handler implements IMessageHandler<PacketZapParticle, IMessage> {
		@Override
		public IMessage onMessage(PacketZapParticle message, MessageContext ctx) {
			new FXDispatcher().arcLightning(message.x, message.y - 1, message.z, message.x1, message.y1, message.z1, 0.2F, 0.5F, 1, 1);

			return null;
		}
	}
}