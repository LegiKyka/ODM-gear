package net.kyka.complicated_odm.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class GasTankItem extends Item {

    public static final int MAX_GAS = 1000;

    public GasTankItem(Settings settings) {
        super(settings);
    }

    public static int getGas(ItemStack stack) {
        return stack.getDamage();
    }

    public static void setGas(ItemStack stack, int amount) {
        stack.setDamage(Math.max(0, Math.min(MAX_GAS, amount)));
    }

    public static boolean isEmpty(ItemStack stack) {
        return getGas(stack) <= 0;
    }

    public static boolean isFull(ItemStack stack) {
        return getGas(stack) >= MAX_GAS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int gas = getGas(stack);
        tooltip.add(Text.literal("Gas: " + gas + "/" + MAX_GAS).formatted(Formatting.GRAY));
    }
}