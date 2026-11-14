package dev.sayed.mehrabalmomen.data.platform.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class CompassSensorManager(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun observeAzimuth(): Flow<Float> = callbackFlow {
        var currentAzimuth = 0f
        var isFirstReading = true
        var hasRotationVector = false

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                var rawAzimuth: Float? = null

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    rawAzimuth = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
                } else if (!hasRotationVector) {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        System.arraycopy(event.values, 0, gravity, 0, 3)
                        hasGravity = true
                    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                        hasGeomagnetic = true
                    }

                    if (hasGravity && hasGeomagnetic) {
                        val rotationMatrix = FloatArray(9)
                        val inclinationMatrix = FloatArray(9)
                        if (SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, gravity, geomagnetic)) {
                            val orientation = FloatArray(3)
                            SensorManager.getOrientation(rotationMatrix, orientation)
                            rawAzimuth = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
                        }
                    }
                }

                rawAzimuth?.let { targetAzimuth ->
                    if (isFirstReading) {
                        currentAzimuth = targetAzimuth
                        isFirstReading = false
                    } else {
                        var diff = (targetAzimuth - currentAzimuth) % 360f
                        if (diff < -180f) diff += 360f
                        if (diff > 180f) diff -= 360f
                        currentAzimuth = (currentAzimuth + 0.25f * diff + 360f) % 360f
                    }
                    trySend(currentAzimuth)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationVectorSensor != null) {
            hasRotationVector = true
            sensorManager.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME)
        } else {
            val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            accelSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME) }
            magSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_GAME) }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }.buffer(Channel.CONFLATED)
}
