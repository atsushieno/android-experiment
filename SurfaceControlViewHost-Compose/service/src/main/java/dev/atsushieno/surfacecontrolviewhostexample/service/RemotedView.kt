package dev.atsushieno.surfacecontrolviewhostexample.service

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.sp

class RemotedView(context: Context, width: Int, height: Int) : LinearLayout(context) {
    private val text = TextView(context).apply { text = "android.widget.TextView text" }
    private val compose = ComposeView(context)

    init {
        layoutParams = ViewGroup.LayoutParams(width, height)
        addView(text)
        addView(compose)

        with(compose) {
            setContent {
                MaterialTheme {
                    Column {
                        Text("This is Compose Text", fontSize = 20.sp, color = Color.Red)
                        Text("This is Compose Text", fontSize = 20.sp, color = Color.Green)
                        Text("This is Compose Text", fontSize = 20.sp, color = Color.Blue)
                    }
                }
            }
        }
    }
}
