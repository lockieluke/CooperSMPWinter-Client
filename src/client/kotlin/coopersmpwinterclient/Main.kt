package coopersmpwinterclient

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.Toast

object Main : ClientModInitializer {
	private val communicationEngine = CommunicationEngine()

	override fun onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { handler, sender, client ->
			communicationEngine.registerChannel()
		})

		ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { handler, client ->
			communicationEngine.unregisterChannel()
		})
	}
}