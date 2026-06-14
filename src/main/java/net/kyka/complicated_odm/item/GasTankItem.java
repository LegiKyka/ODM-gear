package net.kyka.complicated_odm.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class GasTankItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final int MAX_GAS = 1000;

    public GasTankItem(Settings settings) {
        super(settings);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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

    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int gas = getGas(stack);
        tooltip.add(Text.literal("Gas: " + gas + "/" + MAX_GAS).formatted(Formatting.GRAY));
    }
    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private software.bernie.geckolib.renderer.GeoItemRenderer<GasTankItem> renderer;

            @Override
            public software.bernie.geckolib.renderer.GeoItemRenderer<GasTankItem> getGeoItemRenderer() {
                if (this.renderer == null) {
                    try {
                        this.renderer = (software.bernie.geckolib.renderer.GeoItemRenderer<GasTankItem>)
                                Class.forName("net.kyka.complicated_odm.client.renderer.GasTankItemRenderer")
                                        .getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return this.renderer;
            }
        });
    }
}