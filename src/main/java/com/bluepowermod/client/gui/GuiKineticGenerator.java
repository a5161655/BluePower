/*
 * This file is part of Blue Power.
 *
 *     Blue Power is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Blue Power is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Blue Power.  If not, see <http://www.gnu.org/licenses/>
 *
 *     @author Quetzi
 */

package com.bluepowermod.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import com.bluepowermod.container.ContainerKineticGenerator;
import com.bluepowermod.reference.Refs;
import com.bluepowermod.tile.tier3.TileKineticGenerator;

public class GuiKineticGenerator extends GuiContainerBaseBP {

    private static final ResourceLocation resLoc = new ResourceLocation(Refs.MODID, "textures/gui/kinetic_generator.png");
    private final TileKineticGenerator kinect;

    public GuiKineticGenerator(InventoryPlayer invPlayer, TileKineticGenerator kinect) {

        super(new ContainerKineticGenerator(invPlayer, kinect), resLoc);
        this.kinect = kinect;
        ySize = 165;
    }
}