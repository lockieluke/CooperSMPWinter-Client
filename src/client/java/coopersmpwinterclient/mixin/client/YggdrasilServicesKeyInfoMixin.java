package coopersmpwinterclient.mixin.client;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import coopersmpwinterclient.Config;
import me.shedaniel.autoconfig.AutoConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = YggdrasilServicesKeyInfo.class, remap = false)
public class YggdrasilServicesKeyInfoMixin {

    @Unique
    private Config getConfig() {
        try {
            return AutoConfig.getConfigHolder(Config.class).getConfig();
        } catch (Exception ignored) {
        }

        return null;
    }

    @Inject(method = "validateProperty", at = @At("HEAD"), cancellable = true)
    private void validateProperty(final Property property, CallbackInfoReturnable<Boolean> cir) {
        if (Objects.requireNonNull(getConfig()).getSkipTextureVerification())
            cir.setReturnValue(true);
    }

}
