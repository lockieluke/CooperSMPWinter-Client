package coopersmpwinterclient.mixin.client;

import coopersmpwinterclient.AudioEngine;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Unique
    private Boolean firstTitleScreen = false;

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(@Nullable Screen screen, CallbackInfo info) {
        if (screen instanceof TitleScreen && !firstTitleScreen) {
            AudioEngine.Companion.getShared().setScale(MinecraftClient.getInstance().options.getSoundVolume(SoundCategory.RECORDS));
            firstTitleScreen = true;
        }
    }

}
