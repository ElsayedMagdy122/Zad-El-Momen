package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerState
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
import platform.Foundation.NSBundle
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
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun play(source: AudioSource, startPositionMs: Long) {
        val url = when (source) {
            is AudioSource.RemoteUrl -> NSURL.URLWithString(source.url)
            is AudioSource.LocalFile -> NSURL.fileURLWithPath(source.path)
            is AudioSource.LocalResource -> NSBundle.mainBundle.URLForResource(source.name, null)
        }

        if (url == null) {
            _playerState.update {
                it.copy(
                    status = AudioPlayerStatus.ERROR,
                    errorMessage = "Invalid URL for source: $source"
                )
            }
            return
        }

        val playerItem = AVPlayerItem(url)
        avPlayer.replaceCurrentItemWithPlayerItem(playerItem)

        if (startPositionMs > 0) {
            val time = CMTimeMakeWithSeconds(startPositionMs / 1000.0, 1000)
            avPlayer.seekToTime(time)
        }

        avPlayer.play()

        _playerState.update {
            it.copy(
                currentSource = source,
                status = AudioPlayerStatus.PLAYING,
                errorMessage = null
            )
        }
        startProgressTracker()
    }

    override fun pause() {
        avPlayer.pause()
        _playerState.update { it.copy(status = AudioPlayerStatus.PAUSED) }
        stopProgressTracker()
    }

    override fun resume() {
        avPlayer.play()
        _playerState.update { it.copy(status = AudioPlayerStatus.PLAYING) }
        startProgressTracker()
    }

    override fun stop() {
        avPlayer.pause()
        avPlayer.replaceCurrentItemWithPlayerItem(null)
        stopProgressTracker()
        _playerState.update { AudioPlayerState() }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun seekTo(positionMs: Long) {
        val time = CMTimeMakeWithSeconds(positionMs / 1000.0, 1000)
        avPlayer.seekToTime(time)
        _playerState.update { it.copy(currentPositionMs = positionMs) }
    }

    override fun setVolume(volume: Float) {
        avPlayer.volume = volume
    }

    override fun release() {
        stopProgressTracker()
        avPlayer.replaceCurrentItemWithPlayerItem(null)
        playerScope.cancel()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = playerScope.launch {
            while (isActive) {
                val currentItem = avPlayer.currentItem
                var durationMs = 0L
                var currentPosMs = 0L

                if (currentItem != null) {
                    durationMs = (CMTimeGetSeconds(currentItem.duration) * 1000).toLong()
                    currentPosMs = (CMTimeGetSeconds(avPlayer.currentTime()) * 1000).toLong()
                    if (durationMs > 0 && currentPosMs >= durationMs - 500) {
                        _playerState.update { it.copy(status = AudioPlayerStatus.ENDED) }
                        break
                    }
                }

                _playerState.update {
                    it.copy(
                        currentPositionMs = currentPosMs.coerceAtLeast(0L),
                        durationMs = durationMs.coerceAtLeast(0L)
                    )
                }

                delay(500L)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }
}
