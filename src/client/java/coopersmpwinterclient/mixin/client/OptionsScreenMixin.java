package coopersmpwinterclient.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import coopersmpwinterclient.Config;
import coopersmpwinterclient.Log;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin {

    @Shadow
    protected abstract ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier);

    @Unique
    private final Config config = AutoConfig.getConfigHolder(Config.class).getConfig();

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;ILnet/minecraft/client/gui/widget/Positioner;)Lnet/minecraft/client/gui/widget/Widget;", shift = At.Shift.BEFORE))
    private void init(CallbackInfo ci, @Local GridWidget.Adder localRef) {
        OptionsScreen optionsScreen = (OptionsScreen) (Object) this;

        localRef.add(this.createButton(Text.of(config.getSkipTextureVerification() ? "Enable Texture Verification" : "Skip Texture Verification"), () -> {
            config.setSkipTextureVerification(!config.getSkipTextureVerification());
            AutoConfig.getConfigHolder(Config.class).save();
            return optionsScreen;
        }));
    }

}
