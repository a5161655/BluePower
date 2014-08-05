package com.bluepowermod.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.bluepowermod.api.Refs;
import com.bluepowermod.containers.ContainerCPU;
import com.bluepowermod.tileentities.tier3.TileCPU;
public class GuiCPU extends GuiBase {
	private final TileCPU cpu;
	private static final ResourceLocation resLoc = new ResourceLocation(Refs.MODID+":textures/gui/cpugui.png");
	
	public GuiCPU (InventoryPlayer invPlayer, TileCPU cpu) {
		super(new ContainerCPU(invPlayer, cpu), resLoc);
		this.cpu = cpu;
	}
}
