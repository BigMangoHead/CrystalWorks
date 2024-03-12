package net.bigmangohead.crystalworks.screen.machine;

import net.bigmangohead.crystalworks.registery.ModMenuTypes;
import net.bigmangohead.crystalworks.screen.abstraction.InventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class BasicGeneratorMenu extends InventoryMenu {
    public <T extends AbstractContainerMenu> BasicGeneratorMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(pContainerId, inv, extraData, 1, ModMenuTypes.BASIC_GENERATOR_MENU.get());
    }

    public <T extends AbstractContainerMenu> BasicGeneratorMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(pContainerId, inv, entity, data, 1, ModMenuTypes.BASIC_GENERATOR_MENU.get());
    }

    public boolean isCrafting() {
        return this.data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 22; // This is the height in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    @Override
    protected void addBlockEntityInventory(BlockEntity entity) {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inventory -> {
            //Input Slot
            this.addSlot(new SlotItemHandler(inventory, 0, 80, 11));
        });
    }
}
