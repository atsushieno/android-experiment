package org.androidaudioplugin.umpclientexperiment;


import android.media.midi.MidiReceiver
import android.media.midi.MidiUmpDeviceService
import android.util.Log

import androidx.annotation.RequiresApi

@RequiresApi(api = 35)
class UmpDeviceService : MidiUmpDeviceService() {
    private val receiver by lazy { UmpReceiver() }
    override fun onGetInputPortReceivers(): MutableList<MidiReceiver> =
        mutableListOf(receiver)

    init {
        Log.i("!!!!", "initialized UmpDeviceService")
    }
}

class UmpReceiver : MidiReceiver() {
    override fun onSend(bytes: ByteArray, offset: Int, length: Int, timestamp: Long) {
        Log.i("!!!!", "UMP Received: " + bytes.joinToString(",") { it.toString(16) })
    }
}