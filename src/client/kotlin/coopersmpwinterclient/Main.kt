package coopersmpwinterclient

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents

object Main : ClientModInitializer {
	private val communicationEngine = CommunicationEngine()

	override fun onInitializeClient() {
		ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { handler, sender, client ->
			communicationEngine.registerChannel()
		})

		ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { handler, client ->
			communicationEngine.unregisterChannel()
		})

		AutoConfig.register(coopersmpwinterclient.Config::class.java
		) { definition: Config?, configClass: Class<coopersmpwinterclient.Config?>? ->
			JanksonConfigSerializer(
				definition,
				configClass
			)
		}
	}
}