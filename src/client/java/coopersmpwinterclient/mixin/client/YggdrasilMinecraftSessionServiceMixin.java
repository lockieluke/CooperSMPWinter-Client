package coopersmpwinterclient.mixin.client;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import coopersmpwinterclient.Config;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = YggdrasilMinecraftSessionService.class, remap = false)
public class YggdrasilMinecraftSessionServiceMixin {

    @Unique
    private Config getConfig() {
        try {
            return AutoConfig.getConfigHolder(Config.class).getConfig();
        } catch (Exception ignored) {
        }

        return null;
    }

    @Inject(method = "getSecurePropertyValue", at = @At("HEAD"), cancellable = true)
    public void getSecurePropertyValue(Property property, CallbackInfoReturnable<String> cir) {
        if (Objects.requireNonNull(getConfig()).getSkipTextureVerification())
            cir.setReturnValue(property.value());
    }

}
