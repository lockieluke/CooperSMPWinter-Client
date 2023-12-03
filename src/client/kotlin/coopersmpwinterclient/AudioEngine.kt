package coopersmpwinterclient

import com.goxr3plus.streamplayer.stream.Outlet
import com.goxr3plus.streamplayer.stream.StreamPlayer
import kotlinx.serialization.Serializable
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import java.util.*
import java.util.logging.Logger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class AudioSource(val x: Int, val y: Int, val z: Int, val world: String, val uuid: String)

@Serializable
data class AudioStreamDefinition(val audioName: String, val audioPacketsCount: Int, val audioUUID: String, val audioSize: Int, val audioSourceUUID: String)

data class AudioPlaybackPacket(val audioUUID: UUID, val audioPacketIndex: Int, val encodedAudioPacket: String)

class AudioEngine {

    companion object {
        val AUDIO_PLAYBACK_CHANNEL = Identifier("coopersmpwinter", "audio_playback")
        val shared = AudioEngine()
    }

    var audioPlaybackPackets = arrayOf<AudioPlaybackPacket>()
    var audioSources = arrayOf<AudioSource>()
    var audioDefinitions = arrayOf<AudioStreamDefinition>()
    var streamPlayers = mutableMapOf<String, StreamPlayer>()
    var scale = 0.0

    fun negotiateAudioSource(audioSource: AudioSource) {
        if (!this.audioSources.any { it.uuid == audioSource.uuid }) {
            this.audioSources += audioSource
            this.streamPlayers[audioSource.uuid] = StreamPlayer()
            Log.info("Negotiated audio source ${audioSource.uuid}")
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun playAudio(audioDefinition: AudioStreamDefinition) {
        val audioPlaybackPackets = this.audioPlaybackPackets.filter { it.audioUUID.toString() == audioDefinition.audioUUID }.sortedBy { it.audioPacketIndex }
        val streamPlayer = this.streamPlayers[audioDefinition.audioSourceUUID]

        if (streamPlayer != null) {
            if (audioPlaybackPackets.size == audioDefinition.audioPacketsCount) {
                val audioPacket = Base64.decode(audioPlaybackPackets.joinToString("") { it.encodedAudioPacket })
                if (streamPlayer.isPlaying)
                    streamPlayer.stop()

                if (audioPacket.size == audioDefinition.audioSize) {
                    try {
                        val disableStreamPlayerLogger = {
                            val loggerField = StreamPlayer::class.java.getDeclaredField("logger")
                            loggerField.isAccessible = true
                            val logger: Logger = loggerField.get(streamPlayer) as Logger
                            logger.useParentHandlers = false
                        }

                        val disableOutletLogger = {
                            val loggerField = Outlet::class.java.getDeclaredField("logger")
                            loggerField.isAccessible = true
                            val logger: Logger = loggerField.get(streamPlayer.outlet) as Logger
                            logger.useParentHandlers = false
                        }

                        disableOutletLogger()
                        disableStreamPlayerLogger()

                        Log.info("Playing audio ${audioDefinition.audioName} with ${audioPacket.size} bytes")
                        streamPlayer.open(audioPacket.inputStream())
                        streamPlayer.play()
                    } catch (e: Exception) {
                        Log.error("Failed to play audio ${audioDefinition.audioName}: ${e.message} because ${e.cause?.message}")
                    }

                    this.adjustVolumes()
                } else {
                    Log.info("Audio packet size ${audioPacket.size} does not match audio definition size ${audioDefinition.audioSize}")
                }

                this.audioDefinitions = this.audioDefinitions.filter { it.audioUUID != audioDefinition.audioUUID }.toTypedArray()
                this.audioPlaybackPackets = arrayOf()
            }
        } else {
            Log.info("Stream player for ${audioDefinition.audioSourceUUID} does not exist")
        }
    }

    fun stopAudio(audioSourceUUID: String) {
        Log.info("Stopping audio for $audioSourceUUID")
        this.streamPlayers[audioSourceUUID]?.stop()
    }

    fun adjustVolumes(location: Vec3d = MinecraftClient.getInstance().player!!.pos) {
        this.streamPlayers.forEach { (uuid, streamPlayer) ->
            val audioSource = this.audioSources.first { it.uuid == uuid }
            val distance = location.distanceTo(Vec3d(audioSource.x.toDouble(), audioSource.y.toDouble(), audioSource.z.toDouble()))
            // The higher the divisor, the more far the audio travels
            val volume = 1.0 - (distance / 10.0).coerceIn(0.0, 1.0)

            streamPlayer.setGain(volume.round(2) * this.scale)
        }
    }

    fun cleanup() {
        this.streamPlayers.forEach { it.value.stop() }
        this.streamPlayers = mutableMapOf()
        this.audioSources = arrayOf()
        this.audioDefinitions = arrayOf()
        this.audioPlaybackPackets = arrayOf()
    }

}