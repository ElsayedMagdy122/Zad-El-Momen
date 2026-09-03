package dev.sayed.mehrabalmomen.data.platform.audio

import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerState
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import platform.AVFoundation.*
import platform.Foundation.NSURL
import platform.darwin.NSObject

class IosAudioPlayer : AudioPlayer {
    private var avPlayer: AVPlayer? = null
    private val _playerState = MutableStateFlow(AudioPlayerState())
    override val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    override fun play(source: AudioSource, startPositionMs: Long) {
        stop()
        
        val urlString = when (source) {
            is AudioSource.RemoteUrl -> source.url
            // For LocalResource, we'd need more logic to find the file path in the bundle
            else -> return 
        }

        val url = NSURL.URLWithString(urlString) ?: return
        avPlayer = AVPlayer(url)
        
        _playerState.update { it.copy(currentSource = source, status = AudioPlayerStatus.BUFFERING) }
        
        avPlayer?.play()
        _playerState.update { it.copy(status = AudioPlayerStatus.PLAYING) }
        
        startProgressTracker()
    }

    override fun pause() {
        avPlayer?.pause()
        _playerState.update { it.copy(status = AudioPlayerStatus.PAUSED) }
        stopProgressTracker()
    }

    override fun resume() {
        avPlayer?.play()
        _playerState.update { it.copy(status = AudioPlayerStatus.PLAYING) }
        startProgressTracker()
    }

    override fun stop() {
        avPlayer?.pause()
        avPlayer = null
        _playerState.update { it.copy(status = AudioPlayerStatus.IDLE, currentPositionMs = 0) }
        stopProgressTracker()
    }

    override fun seekTo(positionMs: Long) {
        // Implementation for AVPlayer seek
    }

    override fun release() {
        stop()
        scope.cancel()
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                // Track progress if needed
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }
}
