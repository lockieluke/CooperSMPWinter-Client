package coopersmpwinterclient.mixin.client;

import coopersmpwinterclient.AudioEngine;
import coopersmpwinterclient.Log;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Unique
    private float previousYaw = 0;
    @Unique
    private float previousPitch = 0;

    @Inject(at = @At("HEAD"), method = "tickMovement")
    private void onPlayerTickMovement(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        final Vec3d prevPos = player.getPos();
        final Vec3d newPos = player.getPos().add(player.getVelocity());

        if (prevPos.distanceTo(newPos) >= 0.1) {
            AudioEngine.Companion.getShared().adjustVolumes(newPos);
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void onPlayerTick(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;

        float yaw = player.getYaw();
        float pitch = player.getPitch();

        Vec3d heading = player.getRotationVec(1.0F);

        if (yaw != this.previousYaw || pitch != this.previousPitch) {
            Log.Companion.info(String.format("Yaw: %f, Pitch: %f, Heading: %s", yaw, pitch, heading.toString()));
        }

        this.previousYaw = yaw;
        this.previousPitch = pitch;
    }
}