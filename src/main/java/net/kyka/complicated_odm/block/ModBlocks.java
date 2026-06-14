package net.kyka.complicated_odm.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.kyka.complicated_odm.Complicated_odm;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {

    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean registerItem) {
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(Complicated_odm.MOD_ID, name));
        Block block = blockFactory.apply(settings.registryKey(blockKey));
        Registry.register(Registries.BLOCK, blockKey, block);

        if (registerItem) {
            RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Complicated_odm.MOD_ID, name));
            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return block;
    }

    public static final Block GAS_PUMP = register("gas_pump", GasPumpBlock::new,
            AbstractBlock.Settings.create().strength(2.0f).requiresTool().nonOpaque(), true);

    public static void registerBlocks() {
        Complicated_odm.LOGGER.info("Registering blocks for " + Complicated_odm.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(net.minecraft.registry.RegistryKey.of(
                net.minecraft.registry.RegistryKeys.ITEM_GROUP,
                net.minecraft.util.Identifier.of("minecraft", "functional_blocks")
        )).register(entries -> {
            entries.add(GAS_PUMP);
        });
    }
}
