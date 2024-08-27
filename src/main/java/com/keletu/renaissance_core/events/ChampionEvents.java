package com.keletu.renaissance_core.events;

import com.keletu.renaissance_core.ConfigsRF;
import com.keletu.renaissance_core.RenaissanceCore;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.mods.ChampionModTainted;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import static thaumcraft.common.lib.utils.EntityUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = RenaissanceCore.MODID)
public class ChampionEvents {


    public static List<String> infernalMobs = new ArrayList<>();

    public static void infernalMobList() {
        infernalMobs.add("Zombie");
        infernalMobs.add("Spider");
        infernalMobs.add("Blaze");
        infernalMobs.add("Enderman");
        infernalMobs.add("Skeleton");
        infernalMobs.add("Witch");
        infernalMobs.add("Thaumcraft:EldritchCrab");
        infernalMobs.add("Thaumcraft:Taintacle");
        infernalMobs.add("Thaumcraft:BrainyZombie");
    }

    @SubscribeEvent
    public static void entitySpawns(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote) {
            if (event.getEntity() instanceof EntityCreature && ((EntityCreature)event.getEntity()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD) != null && ((EntityCreature)event.getEntity()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() == 13.0) {
                IAttributeInstance modai = ((EntityCreature)event.getEntity()).getEntityAttribute(ChampionModTainted.TAINTED_MOD);
                modai.removeModifier(new AttributeModifier(UUID.fromString("2cb22137-a9d8-4417-ae06-de0e70f11b4c"), "istainted", 1.0, 0));
                modai.applyModifier(new AttributeModifier(UUID.fromString("2cb22137-a9d8-4417-ae06-de0e70f11b4c"), "istainted", 0.0, 0));
            }
            if (event.getEntity() instanceof EntityMob) {
                EntityMob mob = (EntityMob)event.getEntity();
                if(infernalMobs.contains(mob.getName()) && event.getWorld().rand.nextInt(100) <= ConfigsRF.championChance - 1)
                        makeChampion(mob, false);
            }
        }
    }

    public static void makeChampion(EntityMob entity, boolean persist) {
        int type = entity.world.rand.nextInt(ChampionModifier.mods.length);
        if (entity instanceof EntityCreeper) {
            type = 0;
        }

        IAttributeInstance modai = entity.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD);
        modai.removeModifier(ChampionModifier.mods[type].attributeMod);
        modai.applyModifier(ChampionModifier.mods[type].attributeMod);
        IAttributeInstance sai;
        IAttributeInstance mai;
        if (!(entity instanceof EntityThaumcraftBoss)) {
            sai = entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
            sai.removeModifier(CHAMPION_HEALTH);
            sai.applyModifier(CHAMPION_HEALTH);
            mai = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
            mai.removeModifier(CHAMPION_DAMAGE);
            mai.applyModifier(CHAMPION_DAMAGE);
            entity.heal(25.0F);
            entity.setCustomNameTag(ChampionModifier.mods[type].getModNameLocalized() + " " + entity.getName());
        } else {
            ((EntityThaumcraftBoss)entity).generateName();
        }

        if (persist) {
            entity.enablePersistence();
        }

        switch (type) {
            case 0:
                sai = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
                sai.removeModifier(BOLDBUFF);
                sai.applyModifier(BOLDBUFF);
                break;
            case 3:
                mai = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                mai.removeModifier(MIGHTYBUFF);
                mai.applyModifier(MIGHTYBUFF);
                break;
            case 5:
                int bh = (int)entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() / 2;
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() + (float)bh);
        }

    }
}
