package jaiz.coolrain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * A fabric rain impact whose volume fades out when the rain stops.
 */
public final class FabricRainSoundInstance extends AbstractTickableSoundInstance {

    private static final int FADE_TICKS = 10;

    private final ClientLevel level;
    private int fadeTicks;

    private FabricRainSoundInstance(
            ClientLevel level,
            double x,
            double y,
            double z,
            float volume,
            float pitch,
            RandomSource random
    ) {
        super(CoolRainSounds.RAIN_SOUNDS_FABRIC, SoundSource.WEATHER, random);
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
        this.attenuation = Attenuation.LINEAR;
    }

    public static void play(
            ClientLevel level,
            double x,
            double y,
            double z,
            float volume,
            float pitch,
            boolean distanceDelay
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        FabricRainSoundInstance sound = new FabricRainSoundInstance(
                level, x, y, z, volume, pitch, level.getRandom()
        );

        if (distanceDelay) {
            Vec3 camera = minecraft.gameRenderer.mainCamera().position();
            double distance = Math.sqrt(camera.distanceToSqr(x, y, z));
            int delay = (int) Math.round(distance / 40.0 * 20.0);
            if (delay > 0) {
                minecraft.getSoundManager().playDelayed(sound, delay);
                return;
            }
        }

        minecraft.getSoundManager().play(sound);
    }

    @Override
    public void tick() {
        if (level.isRaining()) {
            fadeTicks = Math.max(0, fadeTicks - 1);
        } else {
            fadeTicks++;
            if (fadeTicks >= FADE_TICKS) {
                stop();
            }
        }
    }

    @Override
    public float getVolume() {
        return volume * (1.0f - (float) fadeTicks / FADE_TICKS);
    }
}
