package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.ConfigsRC;
import com.keletu.renaissance_core.RenaissanceCore;
import com.keletu.renaissance_core.blocks.BlockHexOfPredictability;
import com.keletu.renaissance_core.blocks.RCBlocks;
import com.keletu.renaissance_core.blocks.TileHexOfPredictability;
import com.keletu.renaissance_core.capability.*;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.items.ItemPontifexRobe;
import com.keletu.renaissance_core.packet.PacketSyncCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.lib.SoundsTC;
import thecodex6824.thaumicaugmentation.api.tile.CapabilityRiftJar;
import thecodex6824.thaumicaugmentation.api.tile.IRiftJar;
import thecodex6824.thaumicaugmentation.common.tile.TileRiftJar;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber(modid = RenaissanceCore.MODID)
public class EventHandlerEntity {

    public static final HashMap<EntityPlayer, Boolean> etherealsClient = new HashMap<>();
    public static final HashMap<EntityPlayer, Boolean> etherealsServer = new HashMap<>();

    @SubscribeEvent
    public static void playerUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            ICapConcilium capabilities = ICapConcilium.get(player);

            HashMap<EntityPlayer, Boolean> ethereals = !player.world.isRemote ? etherealsServer : etherealsClient;

            if (capabilities.isEthereal()) {
                player.noClip = true;
                if (!player.isSneaking() || (!player.isSneaking() && !player.capabilities.allowFlying)) {
                    player.motionY = 0;
                }
            } else if (ethereals.getOrDefault(player, false)) {
                player.noClip = false;
            }
            ethereals.put(player, capabilities.isEthereal());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerInteract(PlayerInteractEvent e) {
        ICapConcilium capabilities = ICapConcilium.get(e.getEntityPlayer());
        if (capabilities != null) {
            if (capabilities.getChainedTime() != 0) e.setCanceled(true);
        }

        ItemStack i = e.getEntityPlayer().getHeldItem(e.getHand());

        if (i != null && !i.isEmpty()) {
            handleCasterInteract(e.getWorld(), e.getPos(), e.getEntityPlayer(), i);
        }
    }

    @SubscribeEvent
    public static void onUsingDisable(LivingEntityUseItemEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            ICapConcilium capabilities = ICapConcilium.get((EntityPlayer) event.getEntityLiving());
            if (capabilities != null) {
                if (capabilities.getChainedTime() != 0) event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onAttackDisable(AttackEntityEvent event) {
        ICapConcilium capabilities = ICapConcilium.get(event.getEntityPlayer());
        if (capabilities != null) {
            if (capabilities.getChainedTime() != 0) event.setCanceled(true);
        }
    }

    public static void handleCasterInteract(World world, BlockPos pos, EntityPlayer player, ItemStack i) {
        if (!world.isRemote) {
            if (world.getBlockState(pos).getBlock() == Blocks.BEDROCK && i.getItem() == ItemsTC.salisMundus) {
                if (!ThaumcraftCapabilities.knowsResearch(player, "HEXOFPREDICTABILITY") || !BlockHexOfPredictability.checkStructure(world, pos))
                    return;
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
                        //PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockArc(pos.add(xx, 0, zz), -9999), new NetworkRegistry.TargetPoint(world.provider.getDimension(), (double) pos.getX() + xx, (double) pos.getY(), (double)pos.getZ() + zz, 32.0));
                        if (xx == 0 && zz == 0) {
                            TileHexOfPredictability tile = new TileHexOfPredictability();
                            tile.isMaster = true;
                            world.setTileEntity(pos.add(xx, 0, zz), tile);
                            world.notifyBlockUpdate(pos.add(xx, 0, zz), world.getBlockState(pos.add(xx, 0, zz)), world.getBlockState(pos.add(xx, 0, zz)), 3);
                            tile.markDirty();
                        }
                        world.setBlockState(pos.add(xx, 0, zz), RCBlocks.hex_of_predictability.getDefaultState());
                    }
                }
                i.shrink(1);
                world.playSound(null, pos.add(0.5, 0.5, 0.5), SoundsTC.wand, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            if (world.getTileEntity(pos) instanceof TileRiftJar && i.getItem() instanceof ICaster) {
                TileRiftJar riftJar = (TileRiftJar) world.getTileEntity(pos);
                TileEntity tile = world.getTileEntity(pos.add(0, -1, 0));
                IRiftJar jar = riftJar.getCapability(CapabilityRiftJar.RIFT_JAR, null);
                if (jar != null && jar.hasRift() && tile instanceof TileHexOfPredictability && ((TileHexOfPredictability) tile).isMaster && !((TileHexOfPredictability) tile).hasRift) {
                    //List<EntityHexRift> peelers = world.getEntitiesWithinAABB(MaterialPeeler.class, AxisAlignedBB.getBoundingBox(tile.xCoord - 1.0, tile.yCoord, tile.zCoord - 1.0, tile.xCoord + 1.0, tile.yCoord + 4.0, tile.zCoord + 1.0));
                    //if (peelers.isEmpty()) {
                    world.setBlockToAir(pos);
                    world.removeTileEntity(pos);
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    EntityHexRift.constructRift(world, pos.getX(), pos.getY() + 1, pos.getZ(), 25);
                    //}
                }
            }
        }
    }

    @SubscribeEvent
    public static void tickHandler(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        RenaissanceCore.proxy.sendLocalMovementData(player);
        ICapConcilium capabilities = ICapConcilium.get(event.player);
        if (capabilities != null) {
            if (!player.world.isRemote) {
                if (capabilities.getPontifexRobeToggle() && !ItemPontifexRobe.isFullSet(player)) {
                    capabilities.setEthereal(false);
                    capabilities.setPontifexRobeToggle(false);
                    capabilities.sync();
                }
                if (capabilities.getChainedTime() > 0) {
                    capabilities.setChainedTime(capabilities.getChainedTime() - 1);
                    capabilities.sync();
                }
            }
        }

        if (event.phase == TickEvent.Phase.END || event.player.world.isRemote) return;

        IT12Capability it12 = IT12Capability.get(event.player);
        if (it12 != null) {
            it12.setLocationCorrect();
            syncToClientT12(event.player);
        }

        ICapConcilium iconcilium = ICapConcilium.get(event.player);
        if (iconcilium != null) {
            iconcilium.setLocationCorrect();
            syncToClientConcilium(event.player);
        }

    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityBrainyZombie || event.getEntity() instanceof EntityGiantBrainyZombie) {
            if (event.getWorld().rand.nextInt(100) > ConfigsRC.madThaumaturgeReplacesBrainyZombieChance) {
                if (!event.getWorld().isRemote) {
                    EntityMadThaumaturge madThaumaturge = new EntityMadThaumaturge(event.getWorld());
                    madThaumaturge.setLocationAndAngles(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, event.getWorld().rand.nextFloat() * 360.0F, 0.0F);
                    madThaumaturge.onInitialSpawn(event.getWorld().getDifficultyForLocation(event.getEntity().getPosition()), null);
                    event.getEntity().setDead();
                    event.getWorld().spawnEntity(madThaumaturge);
                }
            }
        }
        if (event.getEntity() instanceof EntityCultistKnight) {
            if (event.getWorld().rand.nextInt(100) > ConfigsRC.crimsonPaladinReplacesCultistWarriorChance) {
                if (!event.getWorld().isRemote) {
                    EntityCrimsonPaladin paladin = new EntityCrimsonPaladin(event.getWorld());
                    paladin.setLocationAndAngles(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, event.getWorld().rand.nextFloat() * 360.0F, 0.0F);
                    paladin.onInitialSpawn(event.getWorld().getDifficultyForLocation(event.getEntity().getPosition()), null);
                    event.getEntity().setDead();
                    event.getWorld().spawnEntity(paladin);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        if (!event.player.world.isRemote) {
            ICapConcilium capabilities = ICapConcilium.get(event.player);
            //if (capabilities.isEthereal()) {
            //    if (capabilities.fleshAmount >= event.player.getHealth()) {
            //        capabilities.fleshAmount = MathHelper.floor_float(event.player.getHealth() - 2);
            //    }
            //}
            capabilities.setPontifexRobeToggle(false);
            capabilities.setEthereal(false);
            capabilities.sync();
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(
                    new ResourceLocation(RenaissanceCore.MODID, "canpickoff_t12"),
                    new T12Capability.Provider((EntityPlayer) event.getObject())
            );
            event.addCapability(
                    new ResourceLocation(RenaissanceCore.MODID, "thaumic_concilium"),
                    new CapThaumicConcilium.Provider((EntityPlayer) event.getObject())
            );
        }
    }

    @SubscribeEvent
    public static void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
        if (event.getEntityLiving() instanceof EntityCultist && event.getTarget() instanceof EntityPlayer) {
            if (ItemPontifexRobe.isFullSet((EntityPlayer) event.getTarget())) {
                ((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        //CapThaumicConcilium capabilities = CapThaumicConcilium.get(event.getEntityPlayer());
        //if (capabilities != null) {
        //    if (capabilities.getChainedTime() != 0) event.setCanceled(true);
        //    if (!event.getEntityPlayer().world.isRemote) {
        //        if (!event.getEntityPlayer().capabilities.isCreativeMode && capabilities.isEthereal()) {
        //            ItemStack stack = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
        //            if (stack.isEmpty()) {
        //                capabilities.fleshAmount++;
        //                capabilities.sync();
        //            } else if (!(stack.getItem() instanceof ICaster)) {
        //                capabilities.fleshAmount++;
        //                capabilities.sync();
        //            }
        //        }
        //    }
        //}

        if (!event.getEntityPlayer().world.isRemote && event.getTarget() != null) {
            if (ItemPontifexRobe.isFullSet(event.getEntityPlayer())) {
                List<EntityCultist> list = event.getEntityPlayer().world.getEntitiesWithinAABB(EntityCultist.class, event.getEntityPlayer().getEntityBoundingBox().expand(32, 32, 32).expand(-32, -32, -32));
                if (!list.isEmpty()) {
                    //int life = Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(event.entityPlayer.getCommandSenderName(), Aspect.HUNGER);
                    //int heal = Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(event.entityPlayer.getCommandSenderName(), Aspect.HEAL);
                    int potency = MathHelper.clamp(1, 1, 200);
                    int regen = MathHelper.clamp(1, 1, 200);
                    for (EntityCultist cultist : list) {
                        if (event.getTarget() instanceof EntityLivingBase && cultist.isNonBoss() && !(event.getTarget() instanceof EntityCultist)) {
                            cultist.setAttackTarget((EntityLivingBase) event.getTarget());
                            cultist.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, potency));
                            cultist.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100, regen));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.player.world.isRemote) {
            syncToClientT12(event.player);
            syncToClientConcilium(event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntity();
        if (player.world.isRemote) return;

        IT12Capability capability = player.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);
        if (capability != null) {
            NBTTagCompound data = capability.serializeNBT();
            player.getEntityData().setTag("CanPickOffT12", data);
        }

        ICapConcilium capability1 = player.getCapability(RCCapabilities.CONCILIUM, null);
        if (capability1 != null) {
            NBTTagCompound data = capability1.serializeNBT();
            player.getEntityData().setTag("ThaumicConcilium", data);
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        EntityLivingBase ent = event.getEntityLiving();
        if (ent instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) ent;
            ICapConcilium capabilities = ICapConcilium.get(player);
            if (capabilities != null) {
                if (capabilities.isEthereal()) {
                    event.setCanceled(true);
                }
            }

        }
    }


    /*@SubscribeEvent
    public static void onLivingEntityUseItem(LivingEntityUseItemEvent.Start event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            CapThaumicConcilium capabilities = CapThaumicConcilium.get(player);
            if (capabilities != null) {
                if (capabilities.getChainedTime() != 0) event.setCanceled(true);
                if (!player.world.isRemote) {
                    if (!player.capabilities.isCreativeMode && capabilities.isEthereal()) {
                        if (event.getItem().isEmpty()) {
                            capabilities.fleshAmount++;
                            capabilities.sync();
                        } else if (!(event.getItem().getItem() instanceof ICaster)) {
                            capabilities.fleshAmount++;
                            capabilities.sync();
                        }
                    }
                }
            }

        }
    }*/

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer original = event.getOriginal();
        EntityPlayer player = event.getEntityPlayer();

        if (player.world.isRemote) return;

        // Handle both death and dimension change cloning
        IT12Capability oldCap = original.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);
        IT12Capability newCap = player.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);

        if (oldCap != null && newCap != null) {
            if (event.isWasDeath()) {
                NBTTagCompound data = original.getEntityData().getCompoundTag("CanPickOffT12");
                newCap.deserializeNBT(data);
            } else {
                NBTTagCompound data = oldCap.serializeNBT();
                newCap.deserializeNBT(data);
            }
            syncToClientT12(player);
        }

        ICapConcilium oldCapc = original.getCapability(RCCapabilities.CONCILIUM, null);
        ICapConcilium newCapc = player.getCapability(RCCapabilities.CONCILIUM, null);

        if (oldCapc != null && newCapc != null) {
            if (event.isWasDeath()) {
                NBTTagCompound data = original.getEntityData().getCompoundTag("ThaumicConcilium");
                newCapc.deserializeNBT(data);
            } else {
                NBTTagCompound data = oldCapc.serializeNBT();
                newCapc.deserializeNBT(data);
            }
            syncToClientConcilium(player);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityDissolved) {
            if (!event.getSource().damageType.equals("outOfWorld") && !event.getSource().damageType.equals("inWall")) {
                event.setCanceled(true);
            }
        }

        if (event.getEntityLiving() instanceof EntityStrayedMirror) {
            if (event.getSource().damageType.equals("magic")) {
                event.setCanceled(true);
            }
        }

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();

            ICapConcilium capabilities = ICapConcilium.get(player);
            if (capabilities != null) {
                if (!player.capabilities.isCreativeMode && capabilities.isEthereal()) {
                    //if (event.getSource().isMagicDamage() || event.getSource().damageType.equals("outOfWorld")) {
                    //    capabilities.fleshAmount++;
                    //    capabilities.sync();
                    //}
                    event.setCanceled(true);
                }
            }
        }
    }

    /*
     * Codes from deeper depths mod
     * https://github.com/SmileycorpMC/Deeper-Depths/blob/main/src/main/java/com/deeperdepths/common/DeeperDepthsEventHandler.java
     * Those codes under LGPLv2.1 License: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
     * */
    private static void deflectProjectile(Entity projectile) {
        double bounceStrength = -1.0D;
        projectile.motionX *= bounceStrength;
        projectile.motionY *= bounceStrength;
        projectile.motionZ *= bounceStrength;
        projectile.velocityChanged = true;

        projectile.world.playSound(null, projectile.getPosition(), SoundsTC.fly, SoundCategory.HOSTILE, 1, 1);
    }

    /*
     * Codes from deeper depths mod
     * https://github.com/SmileycorpMC/Deeper-Depths/blob/main/src/main/java/com/deeperdepths/common/DeeperDepthsEventHandler.java
     * Those codes under LGPLv2.1 License: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
     * */

    /**
     * Events have to be used for Projectile Reflection, as `attackEntityFrom` is called within onImpact for most projectiles, which breaks this behavior!
     */
    @SubscribeEvent
    public static void reflectArrowEvent(ProjectileImpactEvent.Arrow event) {
        final EntityArrow projectile = event.getArrow();

        if (projectile.getEntityWorld().isRemote) return;
        Entity entity = event.getRayTraceResult().entityHit;

        if (event.getEntity() != null && entity instanceof EntityStrayedMirror) {
            deflectProjectile(projectile);
            projectile.shootingEntity = entity;

            event.setCanceled(true);
        }
    }

    /*
     * Codes from deeper depths mod
     * https://github.com/SmileycorpMC/Deeper-Depths/blob/main/src/main/java/com/deeperdepths/common/DeeperDepthsEventHandler.java
     * Those codes under LGPLv2.1 License: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
     * */
    @SubscribeEvent
    public static void reflectFireballEvent(ProjectileImpactEvent.Fireball event) {
        final EntityFireball projectile = event.getFireball();

        if (projectile.getEntityWorld().isRemote) return;
        Entity entity = event.getRayTraceResult().entityHit;

        if (event.getEntity() != null && entity instanceof EntityStrayedMirror) {
            deflectProjectile(projectile);
            double bounceStrength = -1.0D;
            projectile.accelerationX *= bounceStrength;
            projectile.accelerationY *= bounceStrength;
            projectile.accelerationZ *= bounceStrength;
            projectile.shootingEntity = (EntityLivingBase) entity;

            event.setCanceled(true);
        }
    }

    /*
     * Codes from deeper depths mod
     * https://github.com/SmileycorpMC/Deeper-Depths/blob/main/src/main/java/com/deeperdepths/common/DeeperDepthsEventHandler.java
     * Those codes under LGPLv2.1 License: https://www.gnu.org/licenses/old-licenses/lgpl-2.1.en.html
     * */
    @SubscribeEvent
    public static void reflectThrowableEvent(ProjectileImpactEvent.Throwable event) {
        final EntityThrowable projectile = event.getThrowable();

        if (projectile.getEntityWorld().isRemote) return;
        Entity entity = event.getRayTraceResult().entityHit;

        if (event.getEntity() != null && entity instanceof EntityStrayedMirror) {
            deflectProjectile(projectile);
            //projectile.thrower = entityBlocking;
            event.setCanceled(true);
        }
    }

    private static void syncToClientT12(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            IT12Capability capability = player.getCapability(RCCapabilities.PICK_OFF_T12_CAP, null);
            if (capability != null) {
                NBTTagCompound data = capability.serializeNBT();
                RenaissanceCore.packetInstance.sendTo(new PacketSyncCapability(data, 0), (EntityPlayerMP) player);
            }
        }
    }

    public static void syncToClientConcilium(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            ICapConcilium capability = player.getCapability(RCCapabilities.CONCILIUM, null);
            if (capability != null) {
                NBTTagCompound data = capability.serializeNBT();
                RenaissanceCore.packetInstance.sendTo(new PacketSyncCapability(data, 1), (EntityPlayerMP) player);
            }
        }
    }
}