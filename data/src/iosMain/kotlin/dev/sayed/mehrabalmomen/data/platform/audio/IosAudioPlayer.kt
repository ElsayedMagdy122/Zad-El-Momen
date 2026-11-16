package dev.sayed.mehrabalmomen.data.platform.audio

import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerState
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.seekToTime
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL

class IosAudioPlayer : AudioPlayer {

    private val avPlayer = AVPlayer()
    private val playerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    override val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    init {
        setupAudioSession()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun setupAudioSession() {
        try {
            val session = AVAudioSession.sharedInstance()
            session.setCategory(AVAudioSessionCategoryPlayback, error = null)
            session.setActive(true, error = null)
        } catch (_: Exception) {}
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun play(source: AudioSource, startPositionMs: Long) {
        val nsUrl = when (source) {
            is AudioSource.RemoteUrl -> NSURL.URLWithString(source.url)
            is AudioSource.LocalFile -> NSURL.fileURLWithPath(source.path)
            is AudioSource.LocalResource -> null
        } ?: return

        val playerItem = AVPlayerItem(uRL = nsUrl)
        avPlayer.replaceCurrentItemWithPlayerItem(playerItem)

        if (startPositionMs > 0) {
            val cmTime = CMTimeMakeWithSeconds(startPositionMs / 1000.0, 1000)
            avPlayer.seekToTime(cmTime)
        }

        avPlayer.play()

        _playerState.update {
            it.copy(
                status = AudioPlayerStatus.PLAYING,
                currentSource = source,
                errorMessage = null
            )
        }

        startProgressTracker()
    }

    override fun pause() {
        avPlayer.pause()
        _playerState.update { it.copy(status = AudioPlayerStatus.PAUSED) }
    }

    override fun resume() {
        avPlayer.play()
        _playerState.update { it.copy(status = AudioPlayerStatus.PLAYING) }
    }

    override fun stop() {
        avPlayer.pause()
        avPlayer.replaceCurrentItemWithPlayerItem(null)
        progressJob?.cancel()
        _playerState.update { AudioPlayerState() }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun seekTo(positionMs: Long) {
        val cmTime = CMTimeMakeWithSeconds(positionMs / 1000.0, 1000)
        avPlayer.seekToTime(cmTime)
    }

    override fun setVolume(volume: Float) {
        // No-op for iOS AVPlayer in this configuration
    }

    override fun release() {
        stop()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = playerScope.launch {
            while (isActive) {
                val currentItem = avPlayer.currentItem
                if (currentItem != null) {
                    val currentSec = CMTimeGetSeconds(avPlayer.currentTime())
                    val totalSec = CMTimeGetSeconds(currentItem.duration)

                    val posMs = if (currentSec.isNaN()) 0L else (currentSec * 1000).toLong()
                    val durMs = if (totalSec.isNaN()) 0L else (totalSec * 1000).toLong()

                    _playerState.update {
                        it.copy(
                            currentPositionMs = posMs,
                            durationMs = durMs
                        )
                    }
                }
                delay(500)
            }
        }
    }
}
