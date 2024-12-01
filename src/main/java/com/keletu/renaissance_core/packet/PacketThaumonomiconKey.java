package com.keletu.renaissance_core.packet;

import com.keletu.renaissance_core.events.CursedEvents;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.SoundsTC;

/**
 * Packet for opening Ender Chest inventory to a player.
 * @author Integral
 */

public class PacketThaumonomiconKey implements IMessage {
	private boolean pressed;

	public PacketThaumonomiconKey() {
	}

	public PacketThaumonomiconKey(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.pressed = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.pressed);
	}

	public static class Handler implements IMessageHandler<PacketThaumonomiconKey, IMessage> {

		public IMessage onMessage(PacketThaumonomiconKey message, MessageContext ctx) {
			EntityPlayerMP playerServ = ctx.getServerHandler().player;

			if (CursedEvents.hasThaumiumCursed(playerServ)) {
				playerServ.world.playSound(null, playerServ.getPosition(), SoundsTC.page, SoundCategory.PLAYERS, 1.0F, (float) (0.8F + (Math.random() * 0.2)));
			}

			return null;
		}
	}

}