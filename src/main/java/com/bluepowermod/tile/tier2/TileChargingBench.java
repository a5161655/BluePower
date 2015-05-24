package com.bluepowermod.tile.tier2;

import com.bluepowermod.api.power.IPowerBase;
import com.bluepowermod.api.power.IPowered;
import com.bluepowermod.api.power.IRechargeable;
import com.bluepowermod.init.BPBlocks;
import com.bluepowermod.tile.TileBluePowerBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import uk.co.qmunity.lib.network.annotation.DescSynced;
import uk.co.qmunity.lib.network.annotation.GuiSynced;

/**
 * @author Koen Beckers (K-4U)
 */
public class TileChargingBench extends TileBluePowerBase implements IPowered, IInventory {

    private final ItemStack[] inventory = new ItemStack[24];

    @DescSynced
    private int textureIndex;

    @GuiSynced
    private final IPowerBase powerBase     = getPowerHandler(ForgeDirection.UNKNOWN);
    private final int        powerTransfer = 2;

    @GuiSynced
    private int energyBuffer;

    public static final int MAX_ENERGY_BUFFER = 100;

    @Override
    public void updateEntity() {

        super.updateEntity();

        if (!getWorldObj().isRemote) {
            if (powerBase.getVoltage() < 0.9 * powerBase.getMaxVoltage() && energyBuffer > 0) {
                energyBuffer--;
                powerBase.addEnergy(1, false);
            } else if (powerBase.getVoltage() > 0.95 * powerBase.getMaxVoltage() && energyBuffer < MAX_ENERGY_BUFFER) {
                energyBuffer++;
                powerBase.addEnergy(-1, false);
            }

            for(int i = 0; i < inventory.length; i++) {
                if (inventory[i] != null && inventory[i].getItem() instanceof IRechargeable) {
                    IRechargeable battery = (IRechargeable) inventory[i].getItem();
                    energyBuffer -= battery.addEnergy(inventory[i], Math.min(powerTransfer, energyBuffer));
                }
            }
            if (worldObj.getWorldTime() % 20 == 0)
                recalculateTextureIndex();
        }

        if (worldObj.getWorldTime() % 20 == 0)
            recalculateTextureIndex();
    }

    private void recalculateTextureIndex() {

        textureIndex = (int) Math.floor((double) energyBuffer / MAX_ENERGY_BUFFER * 4.0);
    }


    public int getTextureIndex() {

        return textureIndex;
    }

    public double getBufferPercentage() {
        return (double) energyBuffer / MAX_ENERGY_BUFFER;
    }

    @Override
    public int getSizeInventory() {

        return 24;
    }

    @Override
    public ItemStack getStackInSlot(int index) {

        return inventory[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int amount) {

        ItemStack itemStack = getStackInSlot(index);
        if (itemStack != null) {
            if (itemStack.stackSize <= amount) {
                setInventorySlotContents(index, null);
            } else {
                itemStack = itemStack.splitStack(amount);
                if (itemStack.stackSize == 0) {
                    setInventorySlotContents(index, null);
                }
            }
        }

        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {

        ItemStack itemStack = getStackInSlot(index);
        if (itemStack != null) {
            setInventorySlotContents(index, null);
        }
        return itemStack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack toSet) {

        inventory[index] = toSet;
    }

    @Override
    public String getInventoryName() {

        return BPBlocks.chargingBench.getUnlocalizedName();
    }

    @Override
    public boolean hasCustomInventoryName() {

        return true;
    }

    @Override
    public int getInventoryStackLimit() {

        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {

        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemToTest) {

        return itemToTest != null && itemToTest.getItem() instanceof IRechargeable;
    }

    /**
     * This function gets called whenever the world/chunk loads
     */
    @Override
    public void readFromNBT(NBTTagCompound tCompound) {

        super.readFromNBT(tCompound);

        for (int i = 0; i < 24; i++) {
            NBTTagCompound tc = tCompound.getCompoundTag("inventory" + i);
            inventory[i] = ItemStack.loadItemStackFromNBT(tc);
        }
        energyBuffer = tCompound.getInteger("energyBuffer");
    }

    /**
     * This function gets called whenever the world/chunk is saved
     */
    @Override
    public void writeToNBT(NBTTagCompound tCompound) {

        super.writeToNBT(tCompound);

        for (int i = 0; i < 24; i++) {
            if (inventory[i] != null) {
                NBTTagCompound tc = new NBTTagCompound();
                inventory[i].writeToNBT(tc);
                tCompound.setTag("inventory" + i, tc);
            }
        }
        tCompound.setInteger("energyBuffer", energyBuffer);
    }
}