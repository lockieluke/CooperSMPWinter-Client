package coopersmpwinterclient.mixin.client;

import coopersmpwinterclient.AudioEngine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(at = @At("HEAD"), method = "tickMovement")
    private void onPlayerTickMovement(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        final Vec3d prevPos = player.getPos();
        final Vec3d newPos = player.getPos().add(player.getVelocity());

        if (prevPos.distanceTo(newPos) >= 0.1) {
            AudioEngine.Companion.getShared().adjustVolumes(newPos);
        }
    }
}