package org.androidaudioplugin.umpclientexperiment;

import android.media.midi.MidiDeviceService
import android.media.midi.MidiReceiver
import android.util.Log


class Midi1DeviceService : MidiDeviceService() {
    private val receiver by lazy { Midi1Receiver() }
    override fun onGetInputPortReceivers(): Array<MidiReceiver> =
        arrayOf(receiver)

    init {
        Log.d("!!!!", "initialized Midi1DeviceService")
    }
}

class Midi1Receiver : MidiReceiver() {
    override fun onSend(p0: ByteArray?, p1: Int, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }
}