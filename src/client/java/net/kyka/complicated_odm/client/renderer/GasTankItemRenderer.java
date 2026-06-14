package net.kyka.complicated_odm.client.renderer;

import net.kyka.complicated_odm.item.GasTankItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class GasTankItemRenderer extends GeoItemRenderer<GasTankItem> {

    public GasTankItemRenderer() {
        super(new GeoModel<GasTankItem>() {
            @Override
            public Identifier getModelResource(GeoRenderState renderState) {
                return Identifier.of("complicated_odm", "gas_tank");
            }

            @Override
            public Identifier getTextureResource(GeoRenderState renderState) {
                return Identifier.of("complicated_odm", "textures/item/gas_tank.png");
            }

            @Override
            public Identifier getAnimationResource(GasTankItem animatable) {
                return Identifier.of("complicated_odm", "gas_tank");
            }
        });
    }
}