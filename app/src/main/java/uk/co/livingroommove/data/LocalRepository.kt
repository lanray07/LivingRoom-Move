package uk.co.livingroommove.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import uk.co.livingroommove.core.*
import java.io.IOException

private val Context.localData by preferencesDataStore("livingroom_move")
class LocalRepository(private val context: Context) {
    private val key = stringPreferencesKey("state_v1")
    val state = context.localData.data.catch { if (it is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw it }
        .map { preferences -> preferences[key]?.let { runCatching { catalogueJson.decodeFromString<LocalState>(it) }.getOrNull() } ?: LocalState() }
    suspend fun update(transform: (LocalState) -> LocalState) {
        context.localData.edit { prefs ->
            val old = prefs[key]?.let { runCatching { catalogueJson.decodeFromString<LocalState>(it) }.getOrNull() } ?: LocalState()
            prefs[key] = catalogueJson.encodeToString(transform(old))
        }
    }
    suspend fun delete() { context.localData.edit { it.clear() } }
}
