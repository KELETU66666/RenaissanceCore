package com.keletu.riftfeed.mixins;

/*
 * @Author : youyihj
 * From mod HerodotusUtil
 * Under APLv2 License: https://www.apache.org/licenses/LICENSE-2.0.html
 */
import com.keletu.riftfeed.items.RFItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFluxRift;

@Pseudo
@Mixin(value={EntityFluxRift.class})
public abstract class MixinEntityFluxRift
extends Entity {
    //private static final ItemDropSupplier PRIMORDIAL_GRAIN = ItemDropSupplier.of(() -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("thaumicwonders:primordial_grain"))));
    @Shadow(remap=false)
    int maxSize;

    public MixinEntityFluxRift(World worldIn) {
        super(worldIn);
    }

    @Shadow(remap=false)
    public abstract int getRiftSize();

    @Shadow(remap=false)
    public abstract void setRiftSize(int var1);

    @Shadow(remap=false)
    public abstract void setCollapse(boolean var1);

    @Shadow(remap=false)
    public abstract float getRiftStability();

    @Shadow(remap=false)
    public abstract void setRiftStability(float var1);

    //@Inject(method={"onUpdate"}, at=@At(value="HEAD"))
    //public void detectTag(CallbackInfo ci) {
    //    if (this.getRiftSize() >= 100) {
    //        this.removeTag("fed");
    //    }
    //}

    @Redirect(method={"onUpdate"}, at=@At(value="INVOKE", target="Lnet/minecraft/entity/Entity;setDead()V"))
    public void injectOnUpdate(Entity entity) {
        EntityItem entityItem = (EntityItem)entity;
        ItemStack item = entityItem.getItem();
        if (item.getItem() == RFItems.rift_feed) {
            int newSize;
            int s = 0;
            for (int i = 0; i < item.getCount(); ++i) {
                s += MathHelper.getInt(this.world.rand, 3, 8);
            }
            this.maxSize = newSize = Math.min(100, this.getRiftSize() + item.getCount() + s);
            this.setRiftStability(Math.min(100.0f, this.getRiftStability() + 10.0f * (float)item.getCount()));
            this.setRiftSize(newSize);
            if(!this.getTags().contains("fed"))
                this.addTag("fed");
            if (this.getRiftSize() >= 100) {
                this.setCollapse(true);
            }
        }
        entity.setDead();
    }

    //@Redirect(method={"completeCollapse"}, at=@At(value="INVOKE", target="Lthaumcraft/common/entities/EntityFluxRift;entityDropItem(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/item/EntityItem;", ordinal=0))
    //public EntityItem removePrimordialPearl(EntityFluxRift instance, ItemStack itemStack, float v) {
    //    return null;
    //}
//
    @Inject(method={"completeCollapse"}, at={@At(value="INVOKE", target="Lthaumcraft/common/entities/EntityFluxRift;setDead()V")})
    public void addPrimordialGrain(CallbackInfo ci) {
        if(this.getTags().contains("fed"))
            this.entityDropItem(new ItemStack(ItemsTC.primordialPearl, 1, 7), 0.0f);
    }
}
