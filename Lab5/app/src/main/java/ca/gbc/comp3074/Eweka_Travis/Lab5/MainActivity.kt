package ca.gbc.comp3074.Eweka_Travis.Lab5

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.Eweka_Travis.Lab5.ui.theme.Lab5Theme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager

    // Nullable because getDefaultSensor() returns null when the phone has no such sensor.
    private var temperatureSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private var accelerometerSensor: Sensor? = null   // extension: third sensor

    // Compose-observable state: changing these redraws the screen.
    private var temperature by mutableStateOf<Float?>(null)

    private var gyroX by mutableStateOf(0f)
    private var gyroY by mutableStateOf(0f)
    private var gyroZ by mutableStateOf(0f)

    private var accelX by mutableStateOf(0f)
    private var accelY by mutableStateOf(0f)
    private var accelZ by mutableStateOf(0f)

    private var temperatureAvailable by mutableStateOf(false)
    private var gyroscopeAvailable by mutableStateOf(false)
    private var accelerometerAvailable by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        temperatureAvailable = temperatureSensor != null
        gyroscopeAvailable = gyroscopeSensor != null
        accelerometerAvailable = accelerometerSensor != null

        setContent {
            Lab5Theme {
                SensorScreen(
                    temperature = temperature,
                    temperatureAvailable = temperatureAvailable,
                    gyroX = gyroX,
                    gyroY = gyroY,
                    gyroZ = gyroZ,
                    gyroscopeAvailable = gyroscopeAvailable,
                    accelX = accelX,
                    accelY = accelY,
                    accelZ = accelZ,
                    accelerometerAvailable = accelerometerAvailable
                )
            }
        }
    }

    // Start listening when the app comes to the front.
    override fun onResume() {
        super.onResume()

        temperatureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        accelerometerSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // Stop listening when the app is paused, to save battery.
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        // Note: on the emulator the ambient temperature sensor reports an
        // uninitialised value of about -1e32 until a temperature is set in
        // Extended Controls (Virtual sensors -> Additional sensors). The
        // values below are passed through exactly as the sensor sends them.
        when (event.sensor.type) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                temperature = event.values[0]          // °C
            }

            Sensor.TYPE_GYROSCOPE -> {
                gyroX = event.values[0]                // rad/s around X
                gyroY = event.values[1]                // rad/s around Y
                gyroZ = event.values[2]                // rad/s around Z
            }

            Sensor.TYPE_ACCELEROMETER -> {
                accelX = event.values[0]               // m/s² along X
                accelY = event.values[1]               // m/s² along Y
                accelZ = event.values[2]               // m/s² along Z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not required for this lab.
    }
}

@Composable
fun SensorScreen(
    temperature: Float?,
    temperatureAvailable: Boolean,
    gyroX: Float,
    gyroY: Float,
    gyroZ: Float,
    gyroscopeAvailable: Boolean,
    accelX: Float,
    accelY: Float,
    accelZ: Float,
    accelerometerAvailable: Boolean
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Android Sensor Monitor",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Travis Eweka - 101583426",
                style = MaterialTheme.typography.bodyMedium
            )

            TemperatureCard(temperature = temperature, available = temperatureAvailable)

            GyroscopeCard(x = gyroX, y = gyroY, z = gyroZ, available = gyroscopeAvailable)

            AccelerometerCard(x = accelX, y = accelY, z = accelZ, available = accelerometerAvailable)
        }
    }
}

@Composable
fun TemperatureCard(temperature: Float?, available: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ambient Temperature", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            when {
                !available -> Text("Temperature sensor is not available.")
                temperature == null -> Text("Waiting for sensor reading...")
                else -> Text(
                    text = "%.1f °C".format(temperature),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Composable
fun GyroscopeCard(x: Float, y: Float, z: Float, available: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Gyroscope", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            if (!available) {
                Text("Gyroscope is not available.")
            } else {
                Text("X: %.3f rad/s".format(x))
                Text("Y: %.3f rad/s".format(y))
                Text("Z: %.3f rad/s".format(z))
            }
        }
    }
}

// Independent practice: a third sensor (accelerometer).
@Composable
fun AccelerometerCard(x: Float, y: Float, z: Float, available: Boolean) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Accelerometer", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            if (!available) {
                Text("Accelerometer is not available.")
            } else {
                Text("X: %.2f m/s²".format(x))
                Text("Y: %.2f m/s²".format(y))
                Text("Z: %.2f m/s²".format(z))
            }
        }
    }
}
