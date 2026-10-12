package uk.co.livingroommove

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.co.livingroommove.core.*
import uk.co.livingroommove.data.AppGraph
import uk.co.livingroommove.data.MoveEvent

class MoveViewModel(val graph: AppGraph) : ViewModel() {
    val state = graph.local.state.stateIn(viewModelScope, SharingStarted.Eagerly, LocalState())
    fun preferences(change: (Preferences) -> Preferences) = update { it.copy(preferences = change(it.preferences)) }
    fun update(change: (LocalState) -> LocalState) { viewModelScope.launch { graph.local.update(change) } }
    fun favourite(id: String) = update { it.copy(favourites = if (id in it.favourites) it.favourites - id else it.favourites + id) }
    fun safetyAccepted() {
        graph.measurement.event(MoveEvent.ONBOARDING_COMPLETED)
        update { it.copy(safetyVersion = 1) }
    }
    fun position(workout: Workout, position: Long) = update { old ->
        if (PublicationGate.reason(workout) != null) old else {
            val progress = old.progress[workout.id] ?: SessionProgress()
            old.copy(progress = old.progress + (workout.id to progress.copy(positionMs = position.coerceAtLeast(0), lastPlayedAt = System.currentTimeMillis())))
        }
    }
    fun complete(workout: Workout) {
        graph.measurement.event(MoveEvent.SESSION_COMPLETED)
        if ((state.value.progress[workout.id]?.completed ?: 0) > 0) graph.measurement.event(MoveEvent.REPEAT_SESSION)
        update { recordCompletion(it, workout, true, System.currentTimeMillis()) }
    }
    fun delete() { viewModelScope.launch { graph.local.delete() } }
}
