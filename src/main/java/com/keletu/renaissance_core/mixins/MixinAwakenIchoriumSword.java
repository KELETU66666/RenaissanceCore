package com.keletu.renaissance_core.mixins;

import com.nekokittygames.thaumictinkerer.common.items.Kami.Tools.IchoriumSwordAdv;
import com.nekokittygames.thaumictinkerer.common.utils.SoulHeartHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.SoundsTC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(IchoriumSwordAdv.class)
public abstract class MixinAwakenIchoriumSword extends ItemSword {

    @Unique
    private static final float BONUS_THRESHOLD = 10;
    @Unique
    private static final float BONUS_DAMAGE = 20f;

    public MixinAwakenIchoriumSword(ToolMaterial material) {
        super(material);
    }

    @Inject(method = {"onLeftClickEntity"}, at = {@At(value = "HEAD")}, cancellable = true, remap = false)
    public void mixinOnLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        boolean ret = super.onLeftClickEntity(stack, player, entity);
        if (entity instanceof EntityLivingBase && entity.hurtResistantTime == 0 && !entity.isDead) {
            if (stack.getTagCompound() != null && stack.getTagCompound().getInteger("awaken") == 1 && entity.isEntityAlive() && entity instanceof IMob) {
                attack(player, new ArrayList<>(), (EntityLivingBase) entity);
            } else if (stack.getTagCompound() != null && stack.getTagCompound().getInteger("awaken") == 2) {
                player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 1, 1));
                SoulHeartHandler.addHearts(player);
            }
        }

        cir.setReturnValue(ret);
    }

    //From TBU by keletu.
    @Unique
    private static void attack(EntityPlayer attacker, List<EntityLivingBase> doNotAttack, EntityLivingBase attacked) {
        AxisAlignedBB aabb = new AxisAlignedBB(attacked.posX - 1, attacked.posY - 1, attacked.posZ - 1, attacked.posX + 1, attacked.posY + 1, attacked.posZ + 1).grow(6, 6, 6);

        List<EntityLivingBase> mobs = attacked.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);

        Random rnd = attacker.world.rand;

        mobs.removeAll(doNotAttack);

        if (!mobs.isEmpty()) {
            while (!mobs.isEmpty()) {
                int index = rnd.nextInt(mobs.size());
                if (mobs.get(index) != null && mobs.get(index).isEntityAlive() && mobs.get(index) instanceof IMob && !(mobs.get(index) instanceof EntityPlayer)) {
                    performPlayerAttackAt(attacker, mobs.get(index));

                    attacker.world.playSound(null, attacked.getPosition(), SoundsTC.zap, SoundCategory.AMBIENT, 1F, 0.8F);

                    doNotAttack.add(mobs.get(index));

                    attack(attacker, doNotAttack, mobs.get(index));

                    break;

                }
                mobs.remove(index);
                continue;
            }
        }

    }

    //From TBU by keletu.
    @Unique
    private static void performPlayerAttackAt(EntityPlayer attacker, Entity entityLivingBase) {
        if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(attacker, entityLivingBase))) {
            return;
        }
        if (entityLivingBase.canBeAttackedWithItem()) {
            if (!entityLivingBase.hitByEntity(attacker)) {
                float f = (float) attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
                int i = 0;
                float f1 = 0.0f;
                if (entityLivingBase instanceof EntityLivingBase) {
                    f1 = EnchantmentHelper.getModifierForCreature(attacker.getHeldItem(EnumHand.MAIN_HAND), ((EntityLivingBase) entityLivingBase).getCreatureAttribute());

                } else {
                    f1 = EnchantmentHelper.getModifierForCreature(attacker.getHeldItem(EnumHand.MAIN_HAND), EnumCreatureAttribute.UNDEFINED);
                }
                if (attacker.isSprinting()) {
                    i++;
                }
                if (f > 0.0f || f1 > 0.0f) {
                    boolean flag = attacker.fallDistance > 0.0f && !attacker.onGround && !attacker.isOnLadder() && !attacker.isInWater() && !attacker.isPotionActive(MobEffects.BLINDNESS) && attacker.getRidingEntity() == null && entityLivingBase instanceof EntityLivingBase;
                    if (flag && f > 0.0f) {
                        f *= 1.5f;
                    }
                    f += f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(attacker);
                    if (entityLivingBase instanceof EntityLivingBase && j > 0 && !attacker.isBurning()) {
                        flag1 = true;
                        entityLivingBase.setFire(1);
                    }
                    boolean flag2 = entityLivingBase.attackEntityFrom(DamageSource.causePlayerDamage(attacker), f);
                    if (flag2) {
                        if (i > 0) {
                            entityLivingBase.addVelocity(-MathHelper.sin(attacker.rotationYaw * (float) Math.PI / 180.0F) * i * 0.5F, 0.1D, MathHelper.cos(attacker.rotationYaw * (float) Math.PI / 180.0F) * i * 0.5F);
                            attacker.motionX *= 0.6d;
                            attacker.motionZ *= 0.6d;
                            attacker.setSprinting(false);
                        }
                        if (flag) {
                            attacker.onCriticalHit(entityLivingBase);
                        }
                        if (f1 > 0.0f) {
                            attacker.onEnchantmentCritical(entityLivingBase);
                        }
                        attacker.setLastAttackedEntity(entityLivingBase);
                        if (entityLivingBase instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase) entityLivingBase, attacker);
                        }
                        EnchantmentHelper.applyArthropodEnchantments(attacker, entityLivingBase);
                        ItemStack is = attacker.getHeldItemMainhand();
                        Object object = entityLivingBase;

                        if (entityLivingBase instanceof EntityDragon) {
                            IEntityMultiPart entityMultiPart = (IEntityMultiPart) ((EntityDragon) entityLivingBase).dragonPartBody;


                            object = entityMultiPart;

                        }
                        if (!is.isEmpty() && object instanceof EntityLivingBase) {
                            is.hitEntity((EntityLivingBase) object, attacker);

                        }
                        if (entityLivingBase instanceof EntityLivingBase) {
                            attacker.addStat(StatList.DAMAGE_DEALT, Math.round(f * 10.0f));
                            if (j > 0) {
                                attacker.setFire(j * 4);
                            }
                        } else if (flag1) {
                            attacker.extinguish();
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (stack.getTagCompound() == null || (stack.getTagCompound().getInteger("awaken") != 1 && stack.getTagCompound().getInteger("awaken") != 2)) {
            float aura = AuraHelper.getVis(target.world, new BlockPos(target));
            float baseaura = AuraHelper.getAuraBase(target.world, new BlockPos(target));
            if (baseaura - aura >= BONUS_THRESHOLD) {
                target.hurtResistantTime = 0;
                target.hurtTime = 0;
                target.attackEntityFrom(DamageSource.MAGIC, BONUS_DAMAGE);
            }
        }
        return super.hitEntity(stack, target, attacker);
    }
}
