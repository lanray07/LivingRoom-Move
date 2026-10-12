package uk.co.livingroommove.ui

import android.net.Uri
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import uk.co.livingroommove.R
import uk.co.livingroommove.BuildConfig
import uk.co.livingroommove.core.*

@androidx.annotation.OptIn(UnstableApi::class)
@Composable fun WorkoutPlayer(workout: Workout?, preferences: Preferences, resumeMs: Long,
    onPosition: (Long) -> Unit, onFinished: () -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val allowed = (workout == null && BuildConfig.DEBUG) || (workout != null && PublicationGate.reason(workout) == null && workout.programmeId == null)
    if (!allowed) { Text(stringResource(R.string.draft_detail), color = Ink); return }
    val player = remember(workout?.id) {
        ExoPlayer.Builder(context).build().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MOVIE).build(), true)
            setHandleAudioBecomingNoisy(true)
            val factory = DefaultMediaSourceFactory(context)
            if (workout == null) setMediaItem(MediaItem.fromUri("asset:///technical-demo.wav"))
            else {
                val voice = requireNotNull(approvedVoice(workout))
                val caption = MediaItem.SubtitleConfiguration.Builder(Uri.parse(voice.captionsUri!!))
                    .setMimeType(MimeTypes.TEXT_VTT).setLanguage(voice.language).setSelectionFlags(C.SELECTION_FLAG_DEFAULT).build()
                val video = factory.createMediaSource(MediaItem.Builder().setUri(workout.videoUri!!).setSubtitleConfigurations(listOf(caption)).build())
                val audio = factory.createMediaSource(MediaItem.fromUri(voice.audioUri!!))
                setMediaSource(MergingMediaSource(video, audio))
            }
            playbackParameters = PlaybackParameters(preferences.narrationSpeed)
            trackSelectionParameters = trackSelectionParameters.buildUpon().setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !preferences.captions).build()
            prepare(); seekTo(resumeMs); playWhenReady = true
        }
    }
    var playing by remember { mutableStateOf(true) }
    var position by remember { mutableLongStateOf(resumeMs) }
    var duration by remember { mutableLongStateOf(0) }
    var error by remember { mutableStateOf(false) }
    var buffering by remember { mutableStateOf(true) }
    var captions by remember { mutableStateOf(preferences.captions) }
    var narration by remember { mutableStateOf(true) }
    var ended by remember { mutableStateOf(false) }
    val currentPositionCallback by rememberUpdatedState(onPosition)
    val currentFinished by rememberUpdatedState(onFinished)
    DisposableEffect(player, lifecycle) {
        val session = MediaSession.Builder(context, player).build()
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
            override fun onPlayerError(failure: PlaybackException) { error = true }
            override fun onPlaybackStateChanged(playbackState: Int) {
                buffering = playbackState == Player.STATE_BUFFERING
                if (playbackState == Player.STATE_ENDED && !ended) { ended = true; currentFinished() }
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) { player.pause(); currentPositionCallback(player.currentPosition) }
        }
        player.addListener(listener); lifecycle.addObserver(observer)
        onDispose {
            if (!ended) currentPositionCallback(player.currentPosition)
            lifecycle.removeObserver(observer); player.removeListener(listener); session.release(); player.release()
        }
    }
    LaunchedEffect(player) {
        var ticks = 0
        while (true) {
            position = player.currentPosition; duration = player.duration.coerceAtLeast(0)
            if (!ended && ticks++ % 5 == 0) currentPositionCallback(position)
            delay(1000)
        }
    }
    fun toggle() { if (player.isPlaying) player.pause() else player.play() }
    BackHandler { player.pause(); onBack() }
    val focus = remember { FocusRequester() }
    val inputMode = LocalInputModeManager.current
    LaunchedEffect(Unit) { inputMode.requestInputMode(InputMode.Keyboard); focus.requestFocus() }
    Column(Modifier.fillMaxSize().background(Cream).padding(32.dp).verticalScroll(rememberScrollState())
        .onPreviewKeyEvent { key ->
            val event = key.nativeKeyEvent
            if (event.action != KeyEvent.ACTION_DOWN) false else when (event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> { toggle(); true }
                KeyEvent.KEYCODE_MEDIA_PLAY -> { player.play(); true }
                KeyEvent.KEYCODE_MEDIA_PAUSE -> { player.pause(); true }
                KeyEvent.KEYCODE_MEDIA_REWIND -> { player.seekTo((player.currentPosition - 10000).coerceAtLeast(0)); true }
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> { player.seekTo((player.currentPosition + 10000).coerceAtMost(duration)); true }
                else -> false
            }
        }, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Heading(workout?.title ?: stringResource(R.string.technical_test))
        if (workout == null) Text(stringResource(R.string.test_no_progress), color = Muted, fontSize = 19.sp)
        AndroidView(factory = { ctx -> PlayerView(ctx).apply { this.player = player; useController = false; isFocusable = false; keepScreenOn = true } },
            modifier = Modifier.fillMaxWidth().height(220.dp), update = { it.player = player })
        Text(stringResource(R.string.playback_time, position / 1000, duration / 1000), color = Ink, fontSize = 20.sp)
        if (buffering) Text(stringResource(R.string.buffering), color = Muted, fontSize = 18.sp)
        if (error) {
            Text(stringResource(R.string.playback_error), color = Ink, fontSize = 19.sp)
            MoveButton(stringResource(R.string.retry)) { error = false; player.prepare(); player.play() }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MoveButton(stringResource(if (playing) R.string.pause else R.string.resume), Modifier.focusRequester(focus), primary = true) { toggle() }
            MoveButton(stringResource(R.string.replay)) { player.seekTo(0); player.play() }
            MoveButton(stringResource(R.string.back)) { player.pause(); onBack() }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MoveButton(stringResource(R.string.captions_setting, onOff(captions))) {
                captions = !captions; player.trackSelectionParameters = player.trackSelectionParameters.buildUpon().setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !captions).build()
            }
            MoveButton(stringResource(R.string.narration_setting, onOff(narration))) {
                narration = !narration; player.volume = if (narration) 1f else 0f
            }
        }
    }
}
