package net.kyka.complicated_odm.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.kyka.complicated_odm.Complicated_odm;
import net.kyka.complicated_odm.item.GasTankItem;
import net.kyka.complicated_odm.item.ModItems;
import net.kyka.complicated_odm.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GasPumpBlockEntity extends BlockEntity {

    public static final int FILL_TICKS = 160; // 8 seconds

    private int fillProgress = 0;
    private boolean filling = false;
    private PlayerEntity currentPlayer = null;

    public static BlockEntityType<GasPumpBlockEntity> GAS_PUMP_BLOCK_ENTITY;

    public static void register() {
        GAS_PUMP_BLOCK_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(Complicated_odm.MOD_ID, "gas_pump"),
                FabricBlockEntityTypeBuilder.create(GasPumpBlockEntity::new, ModBlocks.GAS_PUMP).build()
        );
    }

    public GasPumpBlockEntity(BlockPos pos, BlockState state) {
        super(GAS_PUMP_BLOCK_ENTITY, pos, state);
    }

    public void startFilling(PlayerEntity player) {
        if (filling && currentPlayer == player) return;
        filling = true;
        currentPlayer = player;
        fillProgress = 0;
    }

    public void stopFilling() {
        filling = false;
        currentPlayer = null;
        fillProgress = 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, GasPumpBlockEntity be) {
        if (world.isClient()) return;
        if (!be.filling || be.currentPlayer == null) return;

        ServerWorld serverWorld = (ServerWorld) world;
        PlayerEntity player = be.currentPlayer;

        // Stop if player too far or removed
        if (player.isRemoved() || player.getBlockPos().getSquaredDistance(pos) > 25) {
            be.stopFilling();
            return;
        }

        // Stop if no longer holding a gas tank
        ItemStack held = player.getMainHandStack();
        if (!held.isOf(ModItems.GAS_TANK)) {
            be.stopFilling();
            return;
        }

        // Stop if already full
        if (GasTankItem.isFull(held)) {
            player.sendMessage(Text.literal("Gas tank is already full!"), true);
            be.stopFilling();
            return;
        }

        be.fillProgress++;

        // Spawn POOF particles between player and pump every 5 ticks
        if (be.fillProgress % 5 == 0) {
            Vec3d pumpCenter = Vec3d.ofCenter(pos).add(0, 0.5, 0);
            Vec3d playerPos = player.getEyePos();

            for (int i = 0; i <= 4; i++) {
                double t = i / 4.0;
                double x = pumpCenter.x + (playerPos.x - pumpCenter.x) * t;
                double y = pumpCenter.y + (playerPos.y - pumpCenter.y) * t;
                double z = pumpCenter.z + (playerPos.z - pumpCenter.z) * t;
                serverWorld.spawnParticles(ParticleTypes.POOF, x, y, z, 1, 0.05, 0.05, 0.05, 0.01);
            }

            // Play custom hiss sound every 20 ticks
            if (be.fillProgress % 20 == 0) {
                serverWorld.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                        Registries.SOUND_EVENT.getEntry(Registries.SOUND_EVENT.getId(ModSounds.GAS_HISS)).get(),
                        SoundCategory.BLOCKS, 0.5f, 1.0f, 0L);
            }
        }

        // Show progress
        int percent = (be.fillProgress * 100) / FILL_TICKS;
        player.sendMessage(Text.literal("Filling... " + percent + "%"), true);

        // Done filling
        if (be.fillProgress >= FILL_TICKS) {
            GasTankItem.setGas(held, GasTankItem.MAX_GAS);
            player.sendMessage(Text.literal("Gas tank filled!"), true);
            serverWorld.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                    Registries.SOUND_EVENT.getEntry(Registries.SOUND_EVENT.getId(ModSounds.FILLED)).get(),
                    SoundCategory.BLOCKS, 1.0f, 1.0f, 0L);
            be.stopFilling();
        }
    }
}