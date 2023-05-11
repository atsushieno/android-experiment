package dev.atsushieno.surfacecontrolviewhostexample.service

import android.content.Intent
import android.hardware.display.DisplayManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.view.SurfaceControlViewHost
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

object PluginViewController {
    const val OPCODE_CONNECT = 0

    const val MESSAGE_KEY_OPCODE = "opcode"
    const val MESSAGE_KEY_HOST_TOKEN = "hostToken"
    const val MESSAGE_KEY_DISPLAY_ID = "displayId"
    const val MESSAGE_KEY_WIDTH = "width"
    const val MESSAGE_KEY_HEIGHT = "height"
}

class PluginViewService : LifecycleService(), SavedStateRegistryOwner {
    lateinit var view: View
    private lateinit var messenger: Messenger
    lateinit var host: SurfaceControlViewHost

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()

        savedStateRegistryController.performRestore(null)
        messenger = Messenger(PluginViewControllerHandler(Looper.myLooper()!!, this))
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return messenger.binder
    }

    class PluginViewControllerHandler(looper: Looper, private val owner: PluginViewService) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            with(PluginViewController) {
                when (msg.data.getInt(MESSAGE_KEY_OPCODE)) {
                    OPCODE_CONNECT -> {
                        val hostToken = msg.data.getBinder(MESSAGE_KEY_HOST_TOKEN)!!
                        val displayId = msg.data.getInt(MESSAGE_KEY_DISPLAY_ID)
                        val width = msg.data.getInt(MESSAGE_KEY_WIDTH)
                        val height = msg.data.getInt(MESSAGE_KEY_HEIGHT)

                        val display = owner.getSystemService(DisplayManager::class.java)
                            .getDisplay(displayId)

                        owner.view = RemotedView(owner, width, height)

                        val rootView = owner.view

                        with(rootView) {
                            setViewTreeLifecycleOwner(owner)
                            setViewTreeSavedStateRegistryOwner(owner)
                            // FIXME: adjust it
                            //setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                        }

                        val messenger = msg.replyTo
                        owner.mainLooper.queue.addIdleHandler {

                            owner.host = SurfaceControlViewHost(
                                owner,
                                display,
                                hostToken
                            ).apply {
                                setView(rootView, width, height)

                                assert(messenger != null)
                                messenger.send(Message.obtain().apply {
                                    data = bundleOf("surfacePackage" to surfacePackage)
                                })
                            }
                            println("PluginViewService setup done.")

                            false
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}