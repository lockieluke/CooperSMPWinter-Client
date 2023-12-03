package coopersmpwinterclient.mixin.client;

import coopersmpwinterclient.AudioEngine;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Inject(at = @At("HEAD"), method = "updateSoundVolume")
    private void onUpdateSoundVolume(SoundCategory category, float volume, CallbackInfo info) {
        if (category == SoundCategory.RECORDS) {
            AudioEngine.Companion.getShared().setScale(volume);
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                AudioEngine.Companion.getShared().adjustVolumes(client.player.getPos());
            }
        }
    }
}
