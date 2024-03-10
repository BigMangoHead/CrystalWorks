package net.bigmangohead.crystalworks.datagen.loot;

import net.bigmangohead.crystalworks.block.CrystalBlocks;
import net.bigmangohead.crystalworks.item.CrystalItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(CrystalBlocks.SAPPHIRE_BLOCK.get());

        this.add(CrystalBlocks.SAPPHIRE_ORE.get(),
                block -> createBasicOreDrops(CrystalBlocks.SAPPHIRE_ORE.get(), CrystalItems.SAPPHIRE.get(), 2.0f, 5.0f));
        this.add(CrystalBlocks.DEEPSLATE_SAPPHIRE_ORE.get(),
                block -> createBasicOreDrops(CrystalBlocks.DEEPSLATE_SAPPHIRE_ORE.get(), CrystalItems.SAPPHIRE.get(), 2.0f, 5.0f));

        this.dropSelf(CrystalBlocks.CRUSHER.get());
    }

    private LootTable.Builder createBasicOreDrops(Block pBlock, Item pItem, float minDrops, float maxDrops) {
        return createSilkTouchDispatchTable(pBlock, (LootPoolEntryContainer.Builder)
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(pItem)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));

    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return CrystalBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
