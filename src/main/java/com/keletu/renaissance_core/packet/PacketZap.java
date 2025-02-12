package com.keletu.renaissance_core.packet;

import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.events.CursedEvents;
import static com.keletu.renaissance_core.items.ItemDice12.randomDouble;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import thaumcraft.common.lib.SoundsTC;

import java.util.List;

public class PacketZap implements IMessage {
    private boolean pressed;

    public PacketZap() {
    }

    public PacketZap(boolean pressed) {
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

    public static class Handler implements IMessageHandler<PacketZap, IMessage> {

        public IMessage onMessage(PacketZap message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            if (CursedEvents.hasThaumiumCursed(player)) {
                double dx = player.posX + randomDouble() * 16;
                double dy = player.posY + randomDouble() * 16;
                double dz = player.posZ + randomDouble() * 16;

                f:
                for (int i = 1; i <= 16; ++i) {
                    Vec3d lookVec = player.getLookVec();
                    double px = lookVec.x * i + player.posX;
                    double py = lookVec.y * i + player.posY + player.getEyeHeight();
                    double pz = lookVec.z * i + player.posZ;
                    AxisAlignedBB aabb = new AxisAlignedBB(px - 0.5D, py - 0.5D, pz - 0.5D, px + 0.5D, py + 0.5D, pz + 0.5D);
                    List<EntityLivingBase> mobs = player.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
                    for (EntityLivingBase e : mobs) {
                        if (e == player)
                            continue;

                        if (e.isDead)
                            continue;

                        if (e.hurtTime > 0)
                            continue;

                        e.attackEntityFrom(DamageSource.causePlayerDamage(player), 6);
                        RenaissanceCore.packetInstance.sendToAllAround(new PacketZapParticle(player.posX, player.posY, player.posZ, e.posX, e.posY, e.posZ), new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 64));

                        player.world.playSound(player.posX, player.posY, player.posZ, SoundsTC.jacobs, SoundCategory.PLAYERS, 1, player.world.rand.nextFloat() * 2, false);
                        player.world.playSound(e.posX, e.posY, e.posZ, SoundsTC.jacobs, SoundCategory.PLAYERS, 1, player.world.rand.nextFloat() * 2, false);

                        break f;
                    }
                }
            }

            return null;
        }
    }

}