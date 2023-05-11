package dev.atsushieno.surfacecontrolviewhostexample.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.view.SurfaceControlViewHost.SurfacePackage
import android.view.SurfaceView
import android.view.ViewGroup.LayoutParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.atsushieno.surfacecontrolviewhostexample.client.ui.theme.ClientTheme
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : ComponentActivity() {
    class MainActivityViewModel : ViewModel()

    private val messageHandlerThread = HandlerThread("IncomingMessengerHandler").apply { start() }
    private val incomingMessenger = Messenger(ClientReplyHandler(messageHandlerThread.looper) {
        surfaceView.setChildSurfacePackage(it)
        println("client: surfaceView.setChildSurfacePackage() done.")
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        surfaceView = SurfaceView(this)

        setContent {
            ClientTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        val context = LocalContext.current
                        TextButton(onClick = { launchPluginServiceUI(context) }) {
                            Text("Connect")
                        }
                        AndroidView(factory = {
                            surfaceView
                        })
                    }
                }
            }
        }
    }

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var surfaceView: SurfaceView

    val pluginViewServiceAction = "dev.atsushieno.surfacecontrolviewhostexample.viewcontrol.v1"
    val pluginViewServicePackageName = "dev.atsushieno.surfacecontrolviewhostexample.service"
    val pluginViewServiceClassName = "dev.atsushieno.surfacecontrolviewhostexample.service.PluginViewService"

    var bindingInProcess = false

    private fun launchPluginServiceUI(context: Context) {
        surfaceView.apply {
            layoutParams.width = LayoutParams.MATCH_PARENT
            layoutParams.height = LayoutParams.MATCH_PARENT
            requestLayout()
        }

        viewModel.viewModelScope.launch {
            val conn = suspendCoroutine { continuation ->
                if (bindingInProcess)
                    return@suspendCoroutine
                bindingInProcess = true

                context.bindService(
                    Intent().setClassName(pluginViewServicePackageName, pluginViewServiceClassName),
                    PluginViewHostConnection {
                        continuation.resume(it)
                    },
                    Context.BIND_AUTO_CREATE
                )
            }
            val message = Message.obtain().apply {
                data = bundleOf(
                    "opcode" to 0,
                    "hostToken" to surfaceView.hostToken,
                    "displayId" to surfaceView.display.displayId,
                    "width" to surfaceView.width,
                    "height" to surfaceView.height
                )
                replyTo = incomingMessenger
            }

            assert(message.replyTo != null)
            conn.outgoingMessenger.send(message)
        }
    }

    class PluginViewHostConnection(private val onConnected: (PluginViewHostConnection) -> Unit) : ServiceConnection {
        lateinit var outgoingMessenger: Messenger

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            outgoingMessenger = Messenger(service)
            onConnected(this)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    class ClientReplyHandler(looper: Looper, private val onSurfacePackageReceived: (SurfacePackage) -> Unit) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            val pkg = msg.data.getParcelable("surfacePackage") as SurfacePackage?
            pkg?.let { onSurfacePackageReceived(it) }
        }
    }
}
