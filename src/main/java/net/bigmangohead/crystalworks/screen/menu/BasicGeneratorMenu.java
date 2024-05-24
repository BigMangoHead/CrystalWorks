package net.bigmangohead.crystalworks.screen.menu;

import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.block.entity.machine.BasicGeneratorBlockEntity;
import net.bigmangohead.crystalworks.registery.ModMenuTypes;
import net.bigmangohead.crystalworks.screen.menu.abstraction.InventoryMenu;
import net.bigmangohead.crystalworks.screen.menu.slot.CustomFuelSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class BasicGeneratorMenu extends InventoryMenu {
    public BasicGeneratorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(pContainerId, inv, extraData, BasicGeneratorBlockEntity.DataIndex.AMOUNT_OF_VALUES, 1, ModMenuTypes.BASIC_GENERATOR_MENU.get());
    }

    public BasicGeneratorMenu(int pContainerId, Inventory inv, CWBlockEntity entity, ContainerData data) {
        super(pContainerId, inv, entity, data, 1, ModMenuTypes.BASIC_GENERATOR_MENU.get());
    }

    @Override
    protected void addBlockEntityInventory(BlockEntity entity) {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inventory -> {
            //Input Slot
            this.addSlot(new CustomFuelSlot(inventory, 0, 80, 11));
        });
    }

    public int getEnergy() {
        return this.data.get(BasicGeneratorBlockEntity.DataIndex.ENERGY);
    }

    public int getMaxEnergy() {
        return this.data.get(BasicGeneratorBlockEntity.DataIndex.MAX_ENERGY);
    }

    public int getBurnTime() {
        return this.data.get(BasicGeneratorBlockEntity.DataIndex.BURN_TIME);
    }

    public int getMaxBurnTime() {
        return this.data.get(BasicGeneratorBlockEntity.DataIndex.MAX_BURN_TIME);
    }

    public int getEnergyStoredScaled() {
        return (int) (((float) getEnergy() / (float) getMaxEnergy()) * 38);
    }
}
