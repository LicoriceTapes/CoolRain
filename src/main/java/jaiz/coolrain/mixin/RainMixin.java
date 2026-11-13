package jaiz.coolrain.mixin;

import jaiz.coolrain.CoolRainSounds;
import jaiz.coolrain.config.CoolRainConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public abstract class RainMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private boolean coolrain$handlingRainSound;

    @Inject(
        method = "playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void coolrain$replaceRainSound(
        double x,
        double y,
        double z,
        SoundEvent sound,
        SoundSource category,
        float volume,
        float pitch,
        boolean distanceDelay,
        CallbackInfo ci
    ) {
        if (coolrain$handlingRainSound
                || category != SoundSource.WEATHER
                || (sound != SoundEvents.WEATHER_RAIN && sound != SoundEvents.WEATHER_RAIN_ABOVE)) {
            return;
        }

        ClientLevel world = (ClientLevel) (Object) this;
        BlockPos pos = BlockPos.containing(x, y, z);

        Vec3 camera = minecraft.gameRenderer.mainCamera().position();
        BlockPos cameraPos = BlockPos.containing(camera);

        Block block = world.getBlockState(pos).getBlock();
        Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);

        // Vanilla's MOTION_BLOCKING heightmap can ignore carpets, so the
        // rain splash position may refer to the solid block underneath one.
        // In that case, use the carpet directly above the sampled position.
        Block aboveBlock = world.getBlockState(pos.above()).getBlock();
        if (Blocks.CARPET.asList().contains(aboveBlock)
                || Blocks.WOOL_STAIRS.asList().contains(aboveBlock)
                || Blocks.WOOL_SLAB.asList().contains(aboveBlock)) {
            blockId = BuiltInRegistries.BLOCK.getKey(aboveBlock);
        }

        boolean isAbove = y > camera.y + 1.0
                && world.getHeightmapPos(
                    Heightmap.Types.MOTION_BLOCKING,
                    cameraPos
                ).getY() > Mth.floor(camera.y);

        ci.cancel();
        coolrain$handlingRainSound = true;
        try {
            if (isAbove) {
                if (CoolRainConfig.METAL_BLOCKS.contains(blockId) && CoolRainConfig.metalSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_METAL, CoolRainConfig.metalBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, true);
                } else if (CoolRainConfig.FABRIC_BLOCKS.contains(blockId) && CoolRainConfig.fabricSounds) {
                    jaiz.coolrain.FabricRainSoundInstance.play(world, x, y, z, CoolRainConfig.fabricBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, true);
                } else if (CoolRainConfig.GLASS_BLOCKS.contains(blockId) && CoolRainConfig.glassSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_GLASS, CoolRainConfig.glassBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, true);
                } else {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, SoundEvents.WEATHER_RAIN_ABOVE, CoolRainConfig.vanillaRainVolume / 2, 0.75F, false);
                }
            } else {
                if (CoolRainConfig.METAL_BLOCKS.contains(blockId) && CoolRainConfig.metalSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_METAL, CoolRainConfig.metalBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, true);
                } else if (CoolRainConfig.FABRIC_BLOCKS.contains(blockId) && CoolRainConfig.fabricSounds) {
                    jaiz.coolrain.FabricRainSoundInstance.play(world, x, y, z, CoolRainConfig.fabricBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, true);
                } else if (CoolRainConfig.GLASS_BLOCKS.contains(blockId) && CoolRainConfig.glassSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_GLASS, CoolRainConfig.glassBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, true);
                } else if (CoolRainConfig.FOLIAGE_BLOCKS.contains(blockId) && CoolRainConfig.foliageSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_FOLIAGE, CoolRainConfig.foliageBlocksVolume, 1.0f, true);
                } else if (CoolRainConfig.WATER_BLOCKS.contains(blockId) && CoolRainConfig.waterSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_WATER, CoolRainConfig.waterBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, true);
                } else if (CoolRainConfig.CRYSTAL_BLOCKS.contains(blockId) && CoolRainConfig.crystalSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_GLASS, CoolRainConfig.glassBlocksVolume, 1.0F, false);
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_AMETHYST, CoolRainConfig.crystalBlocksVolume, world.getRandom().nextIntBetweenInclusive(6, 10) * 0.1f, false);
                } else if (CoolRainConfig.MUSICAL_BLOCKS.contains(blockId) && CoolRainConfig.musicalSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, SoundEvents.WEATHER_RAIN, CoolRainConfig.vanillaRainVolume, 1.0F, false);
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_NOTEBLOCK, CoolRainConfig.musicalBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, false);
                } else if (CoolRainConfig.BELL_BLOCKS.contains(blockId) && CoolRainConfig.bellSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_METAL, CoolRainConfig.metalBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, true);
                    if (world.getRandom().nextInt(5) == 0) {
                        world.playLocalSound(x, y, z, SoundEvents.BELL_BLOCK, SoundSource.WEATHER, CoolRainConfig.bellBlocksVolume, world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f, false);
                    }
                } else if (CoolRainConfig.EVAPORATIVE_BLOCKS.contains(blockId) && CoolRainConfig.evaporativeSounds) {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, CoolRainSounds.RAIN_SOUNDS_LAVA, CoolRainConfig.evaporativeBlocksVolume, 1.0F, true);
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, SoundEvents.WEATHER_RAIN, CoolRainConfig.vanillaRainVolume, 1.0F, false);
                } else {
                    jaiz.coolrain.RainSoundInstance.play(world, x, y, z, SoundEvents.WEATHER_RAIN, CoolRainConfig.vanillaRainVolume, 1.0F, false);
                }
            }
        } finally {
            coolrain$handlingRainSound = false;
        }
    }

    @Inject(at = @At("TAIL"), method = "tickWeatherEffects")
    private void stormSoundEffects(CallbackInfo ci) {
        ClientLevel world = (ClientLevel) (Object) this;
        Vec3 camera = minecraft.gameRenderer.mainCamera().position();
        BlockPos cameraPos = BlockPos.containing(camera);

        if (world.isThundering()
                && camera.y > 64
                && world.isRainingAt(cameraPos)) {

            if (CoolRainConfig.thunderstormWinds
                    && world.getRandom().nextInt(CoolRainConfig.thunderstormWindsFrequency) == 0) {
                world.playLocalSound(
                    cameraPos.getX() + world.getRandom().nextIntBetweenInclusive(-10, 10),
                    cameraPos.getY(),
                    cameraPos.getZ() + world.getRandom().nextIntBetweenInclusive(-10, 10),
                    CoolRainSounds.WIND_SOUNDS,
                    SoundSource.WEATHER,
                    CoolRainConfig.thunderstormWindsVolume,
                    world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f,
                    true
                );
            }

            if (CoolRainConfig.additionalThunderSounds
                    && world.getRandom().nextInt(CoolRainConfig.additionalThunderFrequency) == 0) {
                world.playLocalSound(
                    cameraPos.getX() + world.getRandom().nextIntBetweenInclusive(-50, 50),
                    cameraPos.getY() + 30,
                    cameraPos.getZ() + world.getRandom().nextIntBetweenInclusive(-50, 50),
                    CoolRainSounds.ROLLING_THUNDER,
                    SoundSource.WEATHER,
                    CoolRainConfig.additionalThunderVolume,
                    world.getRandom().nextIntBetweenInclusive(8, 12) * 0.1f,
                    true
                );
            }
        }
    }
}
