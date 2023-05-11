package dev.atsushieno.surfacecontrolviewhostexample.service

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import dev.atsushieno.surfacecontrolviewhostexample.service.ui.theme.ServiceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addContentView(
            RemotedView(this, 1000, 1000),
            ViewGroup.LayoutParams(1080, 1900))
    }
}
