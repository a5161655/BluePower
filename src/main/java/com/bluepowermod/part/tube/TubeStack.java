package com.bluepowermod.part.tube;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.bluepowermod.api.Refs;
import com.bluepowermod.api.tube.IPneumaticTube.TubeColor;
import com.bluepowermod.api.vec.Vector3Cube;
import com.bluepowermod.client.renderers.RenderHelper;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 
 * @author MineMaarten
 */

public class TubeStack {
    
    public ItemStack           stack;
    public final TubeColor     color;
    public double              progress;                               //0 at the start, 0.5 on an intersection, 1 at the end.
    public double              oldProgress;
    public ForgeDirection      heading;
    public boolean             enabled        = true;                  //will be disabled when the client sided stack is at an intersection, at which point it needs to wait for server input. This just serves a visual purpose.
    private TileEntity         target;                                 //only should have a value when retrieving items. this is the target the item wants to go to.
    private int                targetX, targetY, targetZ;
    private ForgeDirection     targetEntryDir = ForgeDirection.UNKNOWN; //Which side should this item make its entry.
    public static final double ITEM_SPEED     = 0.04;
    private double             speed          = ITEM_SPEED;
    
    @SideOnly(Side.CLIENT)
    private static RenderItem  customRenderItem;
    private static EntityItem  renderedItem;
    
    public TubeStack(ItemStack stack, ForgeDirection from) {
    
        this(stack, from, TubeColor.NONE);
    }
    
    public TubeStack(ItemStack stack, ForgeDirection from, TubeColor color) {
    
        heading = from;
        this.stack = stack;
        this.color = color;
    }
    
    public void setSpeed(double speed) {
    
        this.speed = speed;
    }
    
    /**
     * Updates the movement by the given m/tick.
     * @return true if the stack has gone past the center, meaning logic needs to be triggered.
     */
    public boolean update() {
    
        oldProgress = progress;
        if (enabled) {
            boolean isEntering = progress < 0.5;
            progress += speed;
            return progress >= 0.5 && isEntering;
        } else {
            return false;
        }
    }
    
    public TileEntity getTarget(World world) {
    
        if (target == null && (targetX != 0 || targetY != 0 || targetZ != 0)) {
            target = world.getTileEntity(targetX, targetY, targetZ);
        }
        return target;
    }
    
    public ForgeDirection getTargetEntryDir() {
    
        return targetEntryDir;
    }
    
    public void setTarget(TileEntity tileEntity, ForgeDirection targetEntryDir) {
    
        this.targetEntryDir = targetEntryDir;
        target = tileEntity;
        if (target != null) {
            targetX = target.xCoord;
            targetY = target.yCoord;
            targetZ = target.zCoord;
        } else {
            targetX = 0;
            targetY = 0;
            targetZ = 0;
        }
    }
    
    public TubeStack copy() {
    
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return loadFromNBT(tag);
    }
    
    public void writeToNBT(NBTTagCompound tag) {
    
        stack.writeToNBT(tag);
        tag.setByte("color", (byte) color.ordinal());
        tag.setByte("heading", (byte) heading.ordinal());
        tag.setDouble("progress", progress);
        tag.setDouble("speed", speed);
        tag.setInteger("targetX", targetX);
        tag.setInteger("targetY", targetY);
        tag.setInteger("targetZ", targetZ);
        tag.setByte("targetEntryDir", (byte) targetEntryDir.ordinal());
    }
    
    public static TubeStack loadFromNBT(NBTTagCompound tag) {
    
        TubeStack stack = new TubeStack(ItemStack.loadItemStackFromNBT(tag), ForgeDirection.getOrientation(tag.getByte("heading")), TubeColor.values()[tag.getByte("color")]);
        stack.progress = tag.getDouble("progress");
        stack.speed = tag.getDouble("speed");
        stack.targetX = tag.getInteger("targetX");
        stack.targetY = tag.getInteger("targetY");
        stack.targetZ = tag.getInteger("targetZ");
        stack.targetEntryDir = ForgeDirection.getOrientation(tag.getByte("targetEntryDir"));
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    public void render(float partialTick) {
    
        if (customRenderItem == null) {
            customRenderItem = new RenderItem() {
                
                @Override
                public boolean shouldBob() {
                
                    return false;
                };
            };
            customRenderItem.setRenderManager(RenderManager.instance);
            
            renderedItem = new EntityItem(FMLClientHandler.instance().getWorldClient());
            renderedItem.hoverStart = 0.0F;
        }
        
        renderedItem.setEntityItemStack(stack);
        
        double renderProgress = (oldProgress + (progress - oldProgress) * partialTick) * 2 - 1;
        
        GL11.glPushMatrix();
        GL11.glTranslated(heading.offsetX * renderProgress * 0.5, heading.offsetY * renderProgress * 0.5, heading.offsetZ * renderProgress * 0.5);
        customRenderItem.doRender(renderedItem, 0, 0, 0, 0, 0);
        
        if (color != TubeColor.NONE) {
            
            float size = 0.2F;
            
            int colorInt = ItemDye.field_150922_c[color.ordinal()];
            float red = (colorInt >> 16) / 256F;
            float green = (colorInt >> 8 & 255) / 256F;
            float blue = (colorInt & 255) / 256F;
            
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glColor3f(red, green, blue);
            Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(Refs.MODID, "textures/blocks/tubes/inside_color_border.png"));
            RenderHelper.drawTesselatedTexturedCube(new Vector3Cube(-size, -size, -size, size, size, size));
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
        
        GL11.glPopMatrix();
    }
}
