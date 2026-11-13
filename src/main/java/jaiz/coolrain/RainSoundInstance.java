package jaiz.coolrain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

/**
 * A rain impact sound whose volume fades out when the rain stops.
 */
public final class RainSoundInstance extends AbstractTickableSoundInstance {

    private static final int FADE_TICKS = 40;

    private final ClientLevel level;
    private int fadeTicks;

    private RainSoundInstance(
            ClientLevel level,
            double x,
            double y,
            double z,
            SoundEvent sound,
            float volume,
            float pitch,
            RandomSource random
    ) {
        super(sound, SoundSource.WEATHER, random);
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
            SoundEvent event,
            float volume,
            float pitch,
            boolean distanceDelay
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        RainSoundInstance sound = new RainSoundInstance(
                level, x, y, z, event, volume, pitch, level.getRandom()
        );

        if (distanceDelay) {
            Vec3 camera = minecraft.gameRenderer.mainCamera().position();
            double distance = Math.sqrt(camera.distanceToSqr(x, y, z));
            // Preserve vanilla's approximate distance-based delay for distant impacts.
            if (distance > 10.0) {
                int delay = (int) Math.round(distance / 40.0 * 20.0);
                minecraft.getSoundManager().playDelayed(sound, delay);
                return;
            }
        }

        minecraft.getSoundManager().play(sound);
    }

    @Override
    public void tick() {
        if (level.isRaining()) {
            // If rain resumes during the fade, smoothly restore the sound.
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
