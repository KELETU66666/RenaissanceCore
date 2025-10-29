package com.keletu.renaissance_core;

import fermiumbooter.FermiumRegistryAPI;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.Name("RenaissanceCore")
public class RenaissanceCorePlugin implements IFMLLoadingPlugin {

	public RenaissanceCorePlugin() {
		//False for Vanilla/Coremod mixins, true for regular mod mixins
		FermiumRegistryAPI.enqueueMixin(false, "mixins.rc_early.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.renaissance_core.json");
		FermiumRegistryAPI.enqueueMixin(true, "mixins.rc_research_change.json", () -> !Loader.isModLoaded("oldresearch"));
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[0];
	}
	
	@Override
	public String getModContainerClass()
	{
		return null;
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) { }
	
	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}