package com.keletu.renaissance_core.packet;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.capability.ICapConcilium;
import com.keletu.renaissance_core.items.PontifexRobe;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTogglePontifexRobe implements IMessage, IMessageHandler<PacketTogglePontifexRobe, IMessage> {
    private int dim;
    private int playerID;

    public PacketTogglePontifexRobe() {
    }

    public PacketTogglePontifexRobe(EntityPlayer player) {
        this.dim = player.world.provider.getDimension();
        this.playerID = player.getEntityId();
    }

    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.dim);
        buffer.writeInt(this.playerID);
    }

    public void fromBytes(ByteBuf buffer) {
        this.dim = buffer.readInt();
        this.playerID = buffer.readInt();
    }

    public IMessage onMessage(PacketTogglePontifexRobe message, MessageContext ctx) {
        World world = DimensionManager.getWorld(message.dim);
        if (world != null && (ctx.getServerHandler().player == null || ctx.getServerHandler().player.getEntityId() == message.playerID)) {
            Entity player = world.getEntityByID(message.playerID);
            if (player instanceof EntityPlayer) {
                if (!PontifexRobe.isFullSet((EntityPlayer) player)) return null;
                ICapConcilium capabilities = ICapConcilium.get((EntityPlayer) player);
                if (capabilities == null) return null;
                if (!capabilities.getPontifexRobeToggle() && capabilities.isEthereal()) return null;
                capabilities.setPontifexRobeToggle(!capabilities.getPontifexRobeToggle());
                capabilities.setEthereal(!capabilities.isEthereal());
                if (capabilities.isEthereal()) {
                    world.playSound(null, player.getPosition(), new SoundEvent(new ResourceLocation(RenaissanceCore.MODID + ":shackles")), SoundCategory.PLAYERS, 0.9F, 1.0F);
                }
                capabilities.sync();
            }
            return null;
        } else {
            return null;
        }
    }
}