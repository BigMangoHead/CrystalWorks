package net.bigmangohead.crystalworks.screen.menu;

import net.bigmangohead.crystalworks.block.abstraction.CWItemStackHandler;
import net.bigmangohead.crystalworks.block.entity.CrusherBlockEntity;
import net.bigmangohead.crystalworks.registery.ModMenuTypes;
import net.bigmangohead.crystalworks.screen.menu.abstraction.CWSlotItemHandler;
import net.bigmangohead.crystalworks.screen.menu.abstraction.InventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class CrusherMenu extends InventoryMenu {

    //Client Constructor
    public CrusherMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(pContainerId, inv, extraData, CrusherBlockEntity.DataIndex.AMOUNT_OF_VALUES, 2, ModMenuTypes.CRUSHER_MENU.get());
    }

    //Server Constructor
    public CrusherMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(pContainerId, inv, entity, data, 2, ModMenuTypes.CRUSHER_MENU.get());
    }

    public boolean isCrafting() {
        return this.data.get(CrusherBlockEntity.DataIndex.PROGRESS) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(CrusherBlockEntity.DataIndex.PROGRESS);
        int maxProgress = this.data.get(CrusherBlockEntity.DataIndex.MAX_PROGRESS);
        int progressArrowSize = 22; // This is the height in pixels of your arrow

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    protected void addBlockEntityInventory(BlockEntity blockEntity) {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inventory -> {
            //Input Slot
            this.addSlot(new CWSlotItemHandler((CWItemStackHandler) inventory, 0, 80, 11));

            //Output Slot
            this.addSlot(new CWSlotItemHandler((CWItemStackHandler) inventory, 1, 80, 59){
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }
            });
        });
    }
}

