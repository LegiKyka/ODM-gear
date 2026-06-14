package net.kyka.complicated_odm.block;

import net.kyka.complicated_odm.item.GasTankItem;
import net.kyka.complicated_odm.item.ModItems;
import net.kyka.complicated_odm.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GasPumpBlock extends Block implements BlockEntityProvider {

    public GasPumpBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GasPumpBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient()) return null;
        return (w, pos, s, be) -> {
            if (be instanceof GasPumpBlockEntity pump) {
                GasPumpBlockEntity.tick(w, pos, s, pump);
            }
        };
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
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof GasPumpBlockEntity pump) {
                    pump.startFilling(player);
                }
            }
            return ActionResult.SUCCESS;
        }

        player.sendMessage(Text.literal("Hold a gas tank to fill it!"), true);
        return ActionResult.PASS;
    }

    @Override
    public void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        ItemStack held = player.getMainHandStack();
        String itemId = held.getItem().getTranslationKey();
        if (itemId.contains("sword") || itemId.contains("axe")) {
            triggerWindExplosion(world, pos, player);
        }
    }

    private void triggerWindExplosion(World world, BlockPos pos, PlayerEntity attacker) {
        if (world.isClient()) return;

        // Break the block and drop it
        world.breakBlock(pos, false, attacker);

        ServerWorld serverWorld = (ServerWorld) world;
        Vec3d center = Vec3d.ofCenter(pos);
        double radius = 5.0;

        // Knock back nearby entities
        List<Entity> entities = world.getEntitiesByClass(Entity.class,
                new Box(pos).expand(radius),
                e -> e instanceof LivingEntity && e != attacker);

        for (Entity entity : entities) {
            Vec3d entityPos = entity.getEyePos();
            Vec3d direction = entityPos.subtract(center).normalize();
            double distance = entityPos.distanceTo(center);
            double strength = Math.max(0, (radius - distance) / radius) * 10.0;
            entity.setVelocity(
                    direction.x * strength,
                    direction.y * strength + 0.5,
                    direction.z * strength
            );
            entity.velocityDirty = true;
        }

        // Knock back attacker
        Vec3d attackerDir = attacker.getEyePos().subtract(center).normalize();
        attacker.setVelocity(attackerDir.x * 6.0, 1.0, attackerDir.z * 6.0);
        attacker.velocityDirty = true;

        // Sync velocity to attacker's client
        if (attacker instanceof ServerPlayerEntity serverPlayer) {
            serverPlayer.networkHandler.sendPacket(
                    new EntityVelocityUpdateS2CPacket(attacker)
            );
        }

        // Spawn POOF particles
        for (int i = 0; i < 60; i++) {
            double angle1 = world.random.nextDouble() * Math.PI * 2;
            double angle2 = world.random.nextDouble() * Math.PI * 2;
            double r = world.random.nextDouble() * radius;
            double x = center.x + r * Math.cos(angle1) * Math.sin(angle2);
            double y = center.y + r * Math.cos(angle2);
            double z = center.z + r * Math.sin(angle1) * Math.sin(angle2);
            serverWorld.spawnParticles(ParticleTypes.POOF, x, y, z, 1, 0, 0, 0, 0.05);
        }

        // Play custom boom sound
        serverWorld.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                Registries.SOUND_EVENT.getEntry(Registries.SOUND_EVENT.getId(ModSounds.BOOM)).get(),
                SoundCategory.BLOCKS, 2.0f, 1.0f, 0L);
    }
}