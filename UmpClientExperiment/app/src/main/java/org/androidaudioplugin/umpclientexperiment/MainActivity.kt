package org.androidaudioplugin.umpclientexperiment

import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.androidaudioplugin.umpclientexperiment.ui.theme.UmpClientExperimentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val manager = getSystemService(MIDI_SERVICE) as MidiManager
        val midi2Devices =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
                manager.getDevicesForTransport(MidiManager.TRANSPORT_UNIVERSAL_MIDI_PACKETS).toList()
            else
                listOf()
        val midi1Devices =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
                manager.getDevicesForTransport(MidiManager.TRANSPORT_MIDI_BYTE_STREAM).toList()
            else
                manager.devices.toList()

        setContent {
            UmpClientExperimentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column {
                        ListDevices("MIDI1", midi1Devices, modifier = Modifier.padding(innerPadding))
                        ListDevices("UMP", midi2Devices, modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun ListDevices(label: String, devices: List<MidiDeviceInfo>, modifier: Modifier = Modifier) {
    Column {
        Text(label, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
    devices.forEach {
        Column {
            Text(it.properties.getString(MidiDeviceInfo.PROPERTY_NAME) ?: "<no name>", fontWeight = FontWeight.Bold)
            it.ports.forEach {
                Text("${it.portNumber}: ${it.name}: ${if (it.type == MidiDeviceInfo.PortInfo.TYPE_INPUT) "in" else "out"}")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListDevicesPreview(label: String, devices: List<MidiDeviceInfo>) {
    UmpClientExperimentTheme {
        ListDevices(label, devices)
    }
}