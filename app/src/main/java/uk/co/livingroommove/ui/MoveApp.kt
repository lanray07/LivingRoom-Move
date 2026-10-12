package uk.co.livingroommove.ui

import android.os.LocaleList
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import uk.co.livingroommove.BuildConfig
import uk.co.livingroommove.MoveViewModel
import uk.co.livingroommove.R
import uk.co.livingroommove.core.*
import uk.co.livingroommove.data.MoveEvent
import java.util.Locale
import kotlin.math.roundToInt

val Forest = Color(0xFF294C3B)
val Ink = Color(0xFF253B30)
val Cream = Color(0xFFF5F1E8)
val Muted = Color(0xFF526052)
val Sage = Color(0xFFE3E9DD)

@Composable fun MoveButton(label: String, modifier: Modifier = Modifier, primary: Boolean = false,
    enabled: Boolean = true, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    val dark = primary || focused
    Box(modifier.onFocusChanged { focused = it.isFocused }
        .focusProperties { canFocus = true }
        .border(if (focused) 4.dp else 1.dp, if (focused) Color(0xFFAE6A21) else Forest.copy(alpha = .3f), RoundedCornerShape(12.dp))
        .clip(RoundedCornerShape(12.dp))
        .background(if (dark) Forest else Sage)
        .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
        .padding(horizontal = 18.dp, vertical = 12.dp).defaultMinSize(minHeight = 28.dp)) {
        Text(label, color = if (dark) Cream else Ink, fontSize = 18.sp)
    }
}

@Composable fun MoveApp(vm: MoveViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val localised = remember(configuration, state.preferences.language) {
        val copy = android.content.res.Configuration(configuration)
        copy.setLocales(LocaleList(Locale.forLanguageTag(state.preferences.language)))
        context.createConfigurationContext(copy)
    }
    val density = LocalDensity.current
    CompositionLocalProvider(LocalContext provides localised,
        LocalConfiguration provides localised.resources.configuration,
        LocalDensity provides Density(density.density, density.fontScale * state.preferences.textScale)) {
        MaterialTheme {
            var screen by rememberSaveable { mutableStateOf("home") }
            var category by rememberSaveable { mutableStateOf("all") }
            var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
            var returnScreen by rememberSaveable { mutableStateOf("home") }
            var technical by rememberSaveable { mutableStateOf(false) }
            val inputMode = LocalInputModeManager.current
            val selected = vm.graph.catalogue.workouts.find { it.id == selectedId }
            BackHandler(screen != "home") {
                screen = when (screen) { "detail" -> returnScreen; "safety", "player", "done" -> if (technical) "settings" else "detail"; "family", "delete" -> "settings"; else -> "home" }
            }
            fun open(workout: Workout) { returnScreen = screen; selectedId = workout.id; technical = false; screen = "detail" }
            fun start() { screen = if (state.safetyVersion < 1) "safety" else "player" }
            LaunchedEffect(screen, selectedId) {
                if (screen == "player" && !technical && selected != null) vm.graph.measurement.event(MoveEvent.SESSION_STARTED)
                if (screen == "programmes") vm.graph.measurement.event(MoveEvent.PROGRAMME_VIEWED)
            }
            if (screen == "player" && (technical || selected != null)) {
                WorkoutPlayer(if (technical) null else selected, state.preferences,
                    if (technical) 0L else state.progress[selectedId]?.positionMs ?: 0L,
                    onPosition = { position -> selected?.takeUnless { technical }?.let { vm.position(it, position) } },
                    onFinished = { selected?.takeUnless { technical }?.let(vm::complete); screen = "done" },
                    onBack = { screen = if (technical) "settings" else "detail" })
            } else {
                Row(Modifier.fillMaxSize().background(Cream).padding(32.dp), horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                    Column(Modifier.width(192.dp).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.app_name), color = Forest, fontSize = 24.sp)
                        Spacer(Modifier.height(8.dp))
                        val nav = listOf("home" to R.string.home, "sessions" to R.string.sessions,
                            "programmes" to R.string.programmes, "favourites" to R.string.favourites,
                            "progress" to R.string.progress, "settings" to R.string.settings)
                        nav.forEach { (destination, title) ->
                            MoveButton(stringResource(title), Modifier.fillMaxWidth(), primary = screen == destination) {
                                category = "all"; screen = destination
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Text(stringResource(R.string.remote_hint), color = Muted, fontSize = 14.sp)
                    }
                    val first = remember(screen, category, selectedId) { FocusRequester() }
                    val listStates = remember { mutableMapOf<String, LazyListState>() }
                    val listState = listStates.getOrPut("$screen/$category") { LazyListState() }
                    val visibleWorkouts = vm.graph.catalogue.workouts.filter {
                        (category == "all" || it.category == category) &&
                            (screen != "favourites" || it.id in state.favourites) &&
                            (state.preferences.duration == 0 || it.durationMinutes == state.preferences.duration) &&
                            (state.preferences.posture == "ALL" || it.posture.name == state.preferences.posture)
                    }
                    val focusIndex = if (screen in listOf("sessions", "favourites") && screen == returnScreen)
                        visibleWorkouts.indexOfFirst { it.id == selectedId }.coerceAtLeast(0) else 0
                    LaunchedEffect(first, focusIndex) {
                        inputMode.requestInputMode(InputMode.Keyboard)
                        if (screen in listOf("sessions", "favourites") && visibleWorkouts.isNotEmpty()) listState.scrollToItem(focusIndex + 1)
                        else listState.scrollToItem(when (screen) {
                            "detail" -> 1
                            "safety", "family_consent", "delete" -> 1
                            "done" -> 2
                            "programmes", "membership" -> 1
                            "progress" -> 2
                            else -> 0
                        })
                        withFrameNanos { }
                        first.requestFocus()
                    }
                    LazyColumn(Modifier.weight(1f).fillMaxHeight(), state = listState, verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        when (screen) {
                            "home" -> {
                                item {
                                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Forest)) {
                                    Column(Modifier.weight(1f).padding(24.dp)) {
                                        Text(stringResource(R.string.greeting), color = Color(0xFFD5DFC9), fontSize = 16.sp)
                                        Text(stringResource(R.string.hero_title), color = Cream, fontSize = 36.sp, lineHeight = 40.sp)
                                        Spacer(Modifier.height(10.dp))
                                        Text(stringResource(R.string.tagline), color = Cream, fontSize = 20.sp)
                                        Spacer(Modifier.height(18.dp))
                                        MoveButton(stringResource(R.string.start_gentle), Modifier.focusRequester(first)) { screen = "sessions"; category = "all" }
                                    }
                                    Image(painterResource(R.drawable.brand_portrait), contentDescription = null,
                                        contentScale = ContentScale.Crop, modifier = Modifier.width(228.dp).height(290.dp))
                                    }
                                }
                                item { Text(stringResource(R.string.draft_notice), color = Muted, fontSize = 17.sp) }
                                if (state.preferences.reminder) item { Text(stringResource(R.string.reminder_message), color = Forest, fontSize = 18.sp) }
                                items(listOf("chair" to R.string.chair, "mobility" to R.string.mobility, "balance" to R.string.balance)) { (id, title) ->
                                    MoveButton(stringResource(title), Modifier.fillMaxWidth()) { category = id; screen = "sessions" }
                                }
                            }
                            "sessions", "favourites" -> {
                                item { Heading(stringResource(if (screen == "favourites") R.string.favourites else R.string.sessions)) }
                                val workouts = visibleWorkouts
                                if (workouts.isEmpty()) item {
                                    Text(stringResource(R.string.empty_sessions), color = Muted, fontSize = 20.sp)
                                    MoveButton(stringResource(R.string.clear_filters), Modifier.focusRequester(first)) { vm.preferences { it.copy(duration = 0, posture = "ALL") } }
                                }
                                items(workouts, key = { it.id }) { workout ->
                                    MoveButton("${workout.title}\n${stringResource(R.string.session_meta, pluralStringResource(R.plurals.minutes, workout.durationMinutes, workout.durationMinutes), postureLabel(workout.posture))}\n${stringResource(if (PublicationGate.reason(workout) == null) R.string.approved_badge else R.string.draft_badge)}",
                                        Modifier.fillMaxWidth().then(if (workout == workouts[focusIndex]) Modifier.focusRequester(first) else Modifier)) { open(workout) }
                                }
                            }
                            "detail" -> selected?.let { workout ->
                                item { Heading(workout.title); Text(stringResource(R.string.session_meta, pluralStringResource(R.plurals.minutes, workout.durationMinutes, workout.durationMinutes), postureLabel(workout.posture)), color = Muted, fontSize = 19.sp) }
                                item {
                                    MoveButton(stringResource(if (workout.id in state.favourites) R.string.remove_favourite else R.string.add_favourite), Modifier.focusRequester(first)) { vm.favourite(workout.id) }
                                }
                                item { Text(workout.description, color = Ink, fontSize = 21.sp) }
                                if (PublicationGate.reason(workout) != null) item { Text(stringResource(R.string.draft_detail), color = Muted, fontSize = 20.sp) }
                                else item {
                                    Text(workout.safetyPrompts.joinToString("\n"), color = Ink, fontSize = 20.sp)
                                }
                                if (PublicationGate.reason(workout) == null && workout.programmeId == null) item {
                                    MoveButton(stringResource(R.string.play_session), primary = true) { start() }
                                }
                                item { MoveButton(stringResource(R.string.back_sessions)) { screen = returnScreen } }
                            }
                            "safety" -> {
                                item { Heading(stringResource(R.string.before_start)); Text(stringResource(R.string.safety_text), color = Ink, fontSize = 21.sp) }
                                item { MoveButton(stringResource(R.string.safety_accept), Modifier.focusRequester(first), primary = true) { vm.safetyAccepted(); screen = "player" } }
                                item { MoveButton(stringResource(R.string.back)) { screen = "detail" } }
                            }
                            "done" -> {
                                item { Heading(stringResource(if (technical) R.string.test_complete else R.string.session_complete)) }
                                item { Text(stringResource(if (technical) R.string.test_no_progress else R.string.completion_text), color = Ink, fontSize = 21.sp) }
                                item { MoveButton(stringResource(R.string.home), Modifier.focusRequester(first), primary = true) { screen = "home" } }
                                item { MoveButton(stringResource(R.string.replay)) { screen = "player" } }
                            }
                            "programmes" -> {
                                item { Heading(stringResource(R.string.programmes)); Text(stringResource(R.string.programme_intro), color = Muted, fontSize = 20.sp) }
                                item { MoveButton(stringResource(R.string.view_membership), Modifier.focusRequester(first)) { screen = "membership" } }
                                items(vm.graph.catalogue.programmes) { programme ->
                                    Column(Modifier.fillMaxWidth().background(Sage, RoundedCornerShape(16.dp)).padding(20.dp)) {
                                        Text(programme.title, color = Forest, fontSize = 24.sp)
                                        Text(programme.description, color = Ink, fontSize = 18.sp)
                                        Text(stringResource(R.string.programme_draft), color = Muted, fontSize = 17.sp)
                                    }
                                }
                            }
                            "membership" -> {
                                item { Heading(stringResource(R.string.membership)); Text(stringResource(R.string.billing_unavailable), color = Ink, fontSize = 21.sp) }
                                item { MoveButton(stringResource(R.string.restore), Modifier.focusRequester(first)) { vm.graph.billing.restore() } }
                                item { Text(stringResource(R.string.test_prices), color = Muted, fontSize = 19.sp) }
                                item { Text(stringResource(R.string.renewal_draft), color = Muted, fontSize = 18.sp) }
                                item { Text(stringResource(R.string.restore_unavailable), color = Muted, fontSize = 18.sp) }
                                item { MoveButton(stringResource(R.string.back_programmes)) { screen = "programmes" } }
                            }
                            "progress" -> {
                                item { Heading(stringResource(R.string.progress)) }
                                item { val count = state.progress.values.sumOf { it.completed }; Text(pluralStringResource(R.plurals.progress_total, count, count), color = Forest, fontSize = 27.sp) }
                                item { MoveButton(stringResource(R.string.sessions), Modifier.focusRequester(first)) { screen = "sessions" } }
                                val recent = state.progress.entries.sortedByDescending { it.value.lastPlayedAt }.mapNotNull { p -> vm.graph.catalogue.workouts.find { it.id == p.key } }
                                if (recent.isEmpty()) item { Text(stringResource(R.string.empty_progress), color = Muted, fontSize = 20.sp) }
                                items(recent) { MoveButton(it.title) { open(it) } }
                            }
                            "settings", "family" -> {
                                item { Heading(stringResource(if (screen == "family") R.string.family_setup else R.string.settings)) }
                                if (screen == "family") item { Text(stringResource(R.string.family_active), color = Muted, fontSize = 19.sp) }
                                item { MoveButton(stringResource(R.string.text_size, ((state.preferences.textScale - 1f) * 100).roundToInt()), Modifier.focusRequester(first)) {
                                    vm.preferences { it.copy(textScale = when (it.textScale) { 1f -> 1.15f; 1.15f -> 1.3f; else -> 1f }) }
                                } }
                                item { MoveButton(stringResource(R.string.captions_setting, onOff(state.preferences.captions))) { vm.preferences { it.copy(captions = !it.captions) } } }
                                item { MoveButton(stringResource(R.string.speed_setting, state.preferences.narrationSpeed)) { vm.preferences { it.copy(narrationSpeed = when (it.narrationSpeed) { 1f -> .85f; .85f -> .7f; else -> 1f }) } } }
                                item { MoveButton(stringResource(R.string.motion_setting, onOff(state.preferences.reducedMotion))) { vm.preferences { it.copy(reducedMotion = !it.reducedMotion) } } }
                                item { MoveButton(stringResource(R.string.posture_setting, if (state.preferences.posture == "ALL") stringResource(R.string.any_position) else postureLabel(Posture.valueOf(state.preferences.posture)))) {
                                    vm.preferences { it.copy(posture = when (it.posture) { "ALL" -> "SEATED"; "SEATED" -> "STANDING"; else -> "ALL" }) }
                                } }
                                item { MoveButton(stringResource(R.string.duration_setting, if (state.preferences.duration == 0) stringResource(R.string.any_duration) else pluralStringResource(R.plurals.minutes, state.preferences.duration, state.preferences.duration))) {
                                    vm.preferences { it.copy(duration = when (it.duration) { 0 -> 5; 5 -> 10; 10 -> 15; else -> 0 }) }
                                } }
                                item { MoveButton(stringResource(R.string.reminder_setting, onOff(state.preferences.reminder))) { vm.preferences { it.copy(reminder = !it.reminder) } } }
                                item { MoveButton(stringResource(R.string.language_setting, if (state.preferences.language == "en-GB") "English (UK)" else "English (US)")) { vm.preferences { it.copy(language = if (it.language == "en-GB") "en-US" else "en-GB") } } }
                                item { Text(stringResource(R.string.language_drafts), color = Muted, fontSize = 18.sp) }
                                if (screen == "settings") {
                                    item { MoveButton(stringResource(R.string.family_setup)) { screen = "family_consent" } }
                                    item { MoveButton(stringResource(R.string.delete_data)) { screen = "delete" } }
                                    if (BuildConfig.DEBUG) item { MoveButton(stringResource(R.string.technical_test)) { technical = true; selectedId = null; screen = "player" } }
                                    item { Text(stringResource(R.string.privacy_summary), color = Muted, fontSize = 18.sp) }
                                } else item { MoveButton(stringResource(R.string.finish_family)) { screen = "settings" } }
                            }
                            "family_consent" -> {
                                item { Heading(stringResource(R.string.family_setup)); Text(stringResource(R.string.family_consent), color = Ink, fontSize = 21.sp) }
                                item { MoveButton(stringResource(R.string.family_agree), Modifier.focusRequester(first), primary = true) { screen = "family" } }
                                item { MoveButton(stringResource(R.string.back)) { screen = "settings" } }
                            }
                            "delete" -> {
                                item { Heading(stringResource(R.string.delete_data)); Text(stringResource(R.string.delete_confirmation), color = Ink, fontSize = 21.sp) }
                                item { MoveButton(stringResource(R.string.keep_data), Modifier.focusRequester(first)) { screen = "settings" } }
                                item { MoveButton(stringResource(R.string.delete_confirm)) { vm.delete(); screen = "home" } }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable fun Heading(text: String) { Text(text, color = Forest, fontSize = 32.sp, lineHeight = 38.sp, modifier = Modifier.semantics { heading() }) }
@Composable fun onOff(on: Boolean) = stringResource(if (on) R.string.on else R.string.off)
@Composable fun postureLabel(posture: Posture) = stringResource(when (posture) { Posture.SEATED -> R.string.seated; Posture.STANDING -> R.string.standing; Posture.MIXED -> R.string.mixed })
