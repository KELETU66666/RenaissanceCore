package com.keletu.renaissance_core.foci;

import com.keletu.renaissance_core.util.ExperienceHelper;
import fr.wind_blade.isorropia.common.IsorropiaAPI;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import static net.minecraft.util.text.TextFormatting.DARK_PURPLE;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.RandomItemChooser;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FocusReflection extends FocusEffect {
    private static final int XP = 10;

    @Override
    public boolean execute(RayTraceResult target, Trajectory trajectory, float finalPower, int i) {
        EntityLivingBase base = this.getPackage().getCaster();
        World world = this.getPackage().world;
        if (target.typeOfHit == RayTraceResult.Type.ENTITY && target.entityHit instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target.entityHit;
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);

            if (i % 20 == 0) {
                this.getPackage().world.playSound(null, player.getPosition(), SoundsTC.whispers, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.rand.nextFloat() * 0.4F + 0.8F));
            }
            if (this.getSettingValue("function") == 1) {
                if (i % 5 == 0) {
                    boolean success = knowledge.addKnowledge(IPlayerKnowledge.EnumKnowledgeType.THEORY, selectCategory(player, knowledge), -5);
                    if (success) {
                        player.sendMessage(new TextComponentString(DARK_PURPLE + I18n.translateToLocal("TC.oblivion_response")));
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, 10 + (this.getSettingValue("power") * 2), IPlayerWarp.EnumWarpType.TEMPORARY);
                    }
                }
            } else {
                if (player.experienceTotal >= Math.max(1, XP - this.getSettingValue("power"))) {
                    ExperienceHelper.drainPlayerXP(player, Math.max(1, XP - this.getSettingValue("power")));
                    ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1 + this.getSettingValue("power"), IPlayerWarp.EnumWarpType.TEMPORARY);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void renderParticleFX(World world, double x, double y, double z, double vx, double vy, double vz) {
        final FXGeneric pp = new FXGeneric(world, x, y, z, vx, vy, vz);
        pp.setMaxAge(9);
        pp.setRBGColorF(0.25f + world.rand.nextFloat() * 0.25F, 0.25F + world.rand.nextFloat() * 0.25F, 0.25F + world.rand.nextFloat() * 0.25F);
        pp.setAlphaF(0.0F, 0.6F, 0.6F, 0.0F);
        pp.setGridSize(64);
        pp.setParticles(448, 9, 1);
        pp.setScale(0.5F, 0.25F);
        pp.setGravity((float) (world.rand.nextGaussian() * 0.009F));
        pp.setRandomMovementScale(0.0025F, 0.0025F, 0.0025F);
        ParticleEngine.addEffect(world, pp);
    }

    @Override
    public NodeSetting[] createSettings() {
        int[] function = new int[]{0, 1};
        String[] functionkDesc = new String[]{"rcfocus.reflection.common", "rcfocus.reflection.oblivion"};
        return new NodeSetting[]{new NodeSetting("power", "rcfocus.reflection.power", new NodeSetting.NodeSettingIntRange(1, 3)), new NodeSetting("function", "rcfocus.reflection.function", new NodeSetting.NodeSettingIntList(function, functionkDesc))};
    }

    @Override
    public int getComplexity() {
        return this.getSettingValue("power") * 15 + (this.getSettingValue("oblivion") == 0 ? 0 : 5);
    }

    @Override
    public Aspect getAspect() {
        return IsorropiaAPI.SLOTH;
    }

    @Override
    public String getKey() {
        return "renaissance_core.REFLECTION";
    }

    @Override
    public String getResearch() {
        return "FOCUSREFLECTION";
    }

    protected static class CategoryEntry implements RandomItemChooser.Item {
        public ResearchCategory category;
        public int weight;

        protected CategoryEntry(ResearchCategory category, int weight) {
            this.category = category;
            this.weight = weight;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }
    }

    @Nullable
    protected ResearchCategory selectCategory(EntityPlayer player, IPlayerKnowledge knowledge) {
        List<RandomItemChooser.Item> selectionList = new ArrayList<RandomItemChooser.Item>();
        for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
            int count = knowledge.getKnowledge(IPlayerKnowledge.EnumKnowledgeType.THEORY, category);
            if (count > 0) {
                selectionList.add(new CategoryEntry(category, count));
            }
        }
        if (selectionList.size() <= 0) {
            return null;
        } else {
            RandomItemChooser ric = new RandomItemChooser();
            CategoryEntry selected = (CategoryEntry) ric.chooseOnWeight(selectionList);
            if (selected == null) {
                return null;
            } else {
                return selected.category;
            }
        }
    }
}