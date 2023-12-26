package coopersmpwinterclient

import kotlinx.serialization.json.Json
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.MinecraftClient
import java.util.*

class CommunicationEngine {

    private val audioEngine = AudioEngine.shared
    private val mc = MinecraftClient.getInstance()
    private val toastHelper = ToastHelper(this.mc)

    fun registerChannel() {
        println("Registering audio channel ${AudioEngine.AUDIO_PLAYBACK_CHANNEL}")
        ClientPlayNetworking.registerReceiver(AudioEngine.AUDIO_PLAYBACK_CHANNEL) { client, handler, buf, responseSender ->
            val payload = buf.toString(Charsets.UTF_8)
            val payloadParts = payload.split(" ")

            val commandWord = payloadParts.first()
            var encodedAudioPacketS = arrayOf<String>()
            if (commandWord == "SEND_AUDIO_STREAM") {
                val audioUUID = UUID.fromString(payloadParts[1])
                val audioPacketIndex = payloadParts[2].toInt()
                val encodedAudioPacket = payloadParts.subList(3, payloadParts.size).joinToString(" ")

//                Log.info("Received audio packet $audioPacketIndex for $audioUUID, audio packet hash is ${audioPacket.contentHashCode()}")
                this.audioEngine.audioPlaybackPackets += AudioPlaybackPacket(
                    audioUUID,
                    audioPacketIndex,
                    encodedAudioPacket
                )
                encodedAudioPacketS += encodedAudioPacket

                val currentAudioDefinition = this.audioEngine.audioDefinitions.first { it.audioUUID == audioUUID.toString() }
                if (this.audioEngine.audioPlaybackPackets.filter { it.audioUUID == audioUUID }.size == currentAudioDefinition.audioPacketsCount) {
                    Log.info("Received all audio packets for $audioUUID, playing audio")
                    this.toastHelper.show("Playing audio from server", currentAudioDefinition.audioName)
                    this.audioEngine.playAudio(this.audioEngine.audioDefinitions.first { it.audioUUID == audioUUID.toString() })
                }
            }

            if (commandWord == "DEFINE_AUDIO_STREAM") {
                val audioStreamDefinition =
                    Json.decodeFromString<AudioStreamDefinition>(payload.replace("$commandWord ", ""))
                this.audioEngine.audioDefinitions += audioStreamDefinition
                Log.info("Received audio stream definition ${audioStreamDefinition.audioName} with ${audioStreamDefinition.audioPacketsCount} packets")
            }

            if (commandWord == "STOP_AUDIO_STREAM") {
                val audioSourceUUID = payload.replace("$commandWord ", "")
                this.audioEngine.audioPlaybackPackets = arrayOf()
                this.audioEngine.stopAudio(audioSourceUUID)
                Log.info("Received stop audio stream command")
            }

            if (commandWord == "NEGOTIATE_AUDIO_SOURCE") {
                val audioSource = Json.decodeFromString<AudioSource>(payload.replace("$commandWord ", ""))
                this.audioEngine.negotiateAudioSource(audioSource)
            }

            if (commandWord == "REMOVE_AUDIO_SOURCE") {
                val audioSource = Json.decodeFromString<AudioSource>(payload.replace("$commandWord ", ""))
                this.audioEngine.audioSources = this.audioEngine.audioSources.filter { it.uuid != audioSource.uuid }.toTypedArray()
                this.audioEngine.streamPlayers[audioSource.uuid]?.stop()
                this.audioEngine.streamPlayers.remove(audioSource.uuid)
                this.audioEngine.audioPlaybackPackets = this.audioEngine.audioPlaybackPackets.filter { it.audioUUID.toString() != audioSource.uuid }.toTypedArray()
                this.audioEngine.audioDefinitions = this.audioEngine.audioDefinitions.filter { it.audioSourceUUID != audioSource.uuid }.toTypedArray()
                Log.info("Received audio source removal for ${audioSource.uuid}")
            }

            if (commandWord == "MAKE_SPEAKER_GLOBAL") {
                val audioSourceUUID = payload.replace("$commandWord ", "")
                this.audioEngine.makeSpeakerGlobal(audioSourceUUID)
                Log.info("Received make speaker global command")
            }

            if (commandWord == "RESET_GLOBAL_SPEAKERS") {
                this.audioEngine.resetGlobalSpeakers()
                Log.info("Received reset global speakers command")
            }
        }
    }

    fun unregisterChannel() {
        println("Unregistering audio channel ${AudioEngine.AUDIO_PLAYBACK_CHANNEL}")
        ClientPlayNetworking.unregisterGlobalReceiver(AudioEngine.AUDIO_PLAYBACK_CHANNEL)
        this.audioEngine.cleanup()
    }

}