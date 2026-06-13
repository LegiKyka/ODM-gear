package net.kyka.complicated_odm.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.kyka.complicated_odm.Complicated_odm;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {

    private static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Complicated_odm.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        return Registry.register(Registries.ITEM, itemKey, item);
    }

    public static final Item GAS_TANK = register("gas_tank", GasTankItem::new,
            new Item.Settings().maxCount(1));

    public static void registerItems() {
        Complicated_odm.LOGGER.info("Registering items for " + Complicated_odm.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(net.minecraft.registry.RegistryKey.of(
                net.minecraft.registry.RegistryKeys.ITEM_GROUP,
                net.minecraft.util.Identifier.of("minecraft", "combat")
        )).register(entries -> {
            entries.add(GAS_TANK);
        });
    }
}