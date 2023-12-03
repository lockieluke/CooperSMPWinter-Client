package coopersmpwinterclient

import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import net.minecraft.text.Text

class ToastHelper(private val client: MinecraftClient) {

    fun show(title: String, description: String) {
        this.client.toastManager.add(SystemToast.create(this.client, SystemToast.Type.TUTORIAL_HINT, Text.literal(title), Text.literal(description)))
    }

}