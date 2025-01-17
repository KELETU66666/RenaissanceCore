package com.keletu.renaissance_core.proxy;

import com.keletu.renaissance_core.client.render.*;
import com.keletu.renaissance_core.entity.*;
import com.keletu.renaissance_core.items.ItemManaBean;
import com.keletu.renaissance_core.items.RCItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy{

    @Override
    public void regRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityProtectionField.class, RenderEnderCrystal::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintChicken.class, new RenderTaintChicken(0.3F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintRabbit.class, new RenderTaintRabbit());
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintCow.class, new RenderTaintCow(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintCreeper.class, new RenderTaintCreeper());
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintPig.class, new RenderTaintPig(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintSheep.class, new RenderTaintSheep(0.7F));
        RenderingRegistry.registerEntityRenderingHandler(EntityTaintVillager.class, new RenderTaintVillager());
        registerItemColourHandlers();
    }

    @Override
    public void addRenderLayers() {
        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();

        addLayersToSkin(skinMap.get("default"));
        addLayersToSkin(skinMap.get("slim"));
    }

    private static void addLayersToSkin(RenderPlayer renderPlayer) {
        renderPlayer.addLayer(new LayerBackpack(renderPlayer));
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemColourHandlers() {
        IItemColor itemCrystalPlanterColourHandler = (stack, tintIndex) -> {
            Item item = stack.getItem();
            if (item == RCItems.mana_bean) {
                return ((ItemManaBean) item).getColor(stack, tintIndex);
            }
            return 16777215;
        };

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemCrystalPlanterColourHandler, RCItems.mana_bean);

    }
}
