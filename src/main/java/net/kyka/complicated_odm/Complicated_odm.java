package net.kyka.complicated_odm;

import net.fabricmc.api.ModInitializer;
import net.kyka.complicated_odm.item.ModItems;
import net.kyka.complicated_odm.block.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Complicated_odm implements ModInitializer {
    public static final String MOD_ID = "complicated_odm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModBlocks.registerBlocks();
        LOGGER.info("Complicated ODM Gear Initialized!");
    }
}