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
        Log.d("!!!!", "initialized UmpDeviceService")
    }
}

class UmpReceiver : MidiReceiver() {
    override fun onSend(p0: ByteArray?, p1: Int, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }
}