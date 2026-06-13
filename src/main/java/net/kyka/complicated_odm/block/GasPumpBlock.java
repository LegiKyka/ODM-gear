package net.kyka.complicated_odm.block;

import net.kyka.complicated_odm.item.GasTankItem;
import net.kyka.complicated_odm.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GasPumpBlock extends Block {

    public GasPumpBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, BlockHitResult hit) {
        ItemStack heldItem = player.getMainHandStack();

        if (heldItem.isOf(ModItems.GAS_TANK)) {
            if (GasTankItem.isFull(heldItem)) {
                player.sendMessage(Text.literal("Gas tank is already full!"), true);
                return ActionResult.SUCCESS;
            }

            if (!world.isClient()) {
                GasTankItem.setGas(heldItem, GasTankItem.MAX_GAS);
                player.sendMessage(Text.literal("Gas tank filled!"), true);
            }
            return ActionResult.SUCCESS;
        }

        player.sendMessage(Text.literal("Hold a gas tank to fill it!"), true);
        return ActionResult.PASS;
    }
}