package uk.co.livingroommove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uk.co.livingroommove.data.AppGraph
import uk.co.livingroommove.data.MoveEvent
import uk.co.livingroommove.ui.MoveApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_FULLSCREEN or
            android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val graph = AppGraph(applicationContext)
        graph.measurement.event(MoveEvent.APP_OPENED)
        val vm = ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MoveViewModel(graph) as T
        })[MoveViewModel::class.java]
        setContent { MoveApp(vm) }
    }
}
