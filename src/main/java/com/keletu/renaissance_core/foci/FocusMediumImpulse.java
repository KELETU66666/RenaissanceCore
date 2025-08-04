package com.keletu.renaissance_core.foci;

import com.keletu.renaissance_core.entity.EntityCompressedBlast;
import fr.wind_blade.isorropia.common.IsorropiaAPI;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;


public class FocusMediumImpulse extends FocusMedium
{
    @Override
    public String getResearch() {
        return "FOCUSIMPULSE";
    }
    
    @Override
    public String getKey() {
        return "renaissance_core.IMPULSE";
    }
    
    @Override
    public int getComplexity() {
        return 10;
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TARGET, EnumSupplyType.TRAJECTORY };
    }
    
    @Override
    public boolean execute(Trajectory trajectory) {
        FocusPackage p = getRemainingPackage();
        if (p.getCaster() != null) {
            EntityCompressedBlast projectile = new EntityCompressedBlast(this.getPackage().world, p, trajectory);
            return getPackage().getCaster().world.spawnEntity(projectile);
        }
        return false;
    }
    
    @Override
    public boolean hasIntermediary() {
        return true;
    }

    @Override
    public Aspect getAspect() {
        return IsorropiaAPI.WRATH;
    }
}