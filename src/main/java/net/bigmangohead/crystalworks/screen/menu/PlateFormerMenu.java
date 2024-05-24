package net.bigmangohead.crystalworks.screen.menu;

import net.bigmangohead.crystalworks.block.entity.abstraction.CWBlockEntity;
import net.bigmangohead.crystalworks.block.entity.machine.PlateFormerBlockEntity;
import net.bigmangohead.crystalworks.registery.ModMenuTypes;
import net.bigmangohead.crystalworks.screen.menu.slot.CWSlotItemHandler;
import net.bigmangohead.crystalworks.screen.menu.abstraction.InventoryMenu;
import net.bigmangohead.crystalworks.util.item.CWItemStackHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.jetbrains.annotations.NotNull;

public class PlateFormerMenu extends InventoryMenu {


    public PlateFormerMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        super(pContainerId, inv, extraData, PlateFormerBlockEntity.DataIndex.AMOUNT_OF_VALUES, PlateFormerBlockEntity.SlotIndex.SLOT_COUNT, ModMenuTypes.PLATE_FORMER_MENU.get());
    }

    public PlateFormerMenu(int pContainerId, Inventory inv, CWBlockEntity entity, ContainerData data) {
        super(pContainerId, inv, entity, data, 2, ModMenuTypes.PLATE_FORMER_MENU.get());
    }

    @Override
    protected void addBlockEntityInventory(BlockEntity entity) {
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inventory -> {
            //Input Slot
            this.addSlot(new CWSlotItemHandler((CWItemStackHandler) inventory, PlateFormerBlockEntity.SlotIndex.INPUT_SLOT, 80, 11));

            //Output Slot
            this.addSlot(new CWSlotItemHandler((CWItemStackHandler) inventory, PlateFormerBlockEntity.SlotIndex.OUTPUT_SLOT, 80, 59){
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }
            });
        });
    }
}
