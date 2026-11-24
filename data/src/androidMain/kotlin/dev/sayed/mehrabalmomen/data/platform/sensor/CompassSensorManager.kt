package dev.sayed.mehrabalmomen.data.platform.sensor

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.Surface
import android.view.WindowManager
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.qiblah.CompassData
import dev.sayed.mehrabalmomen.domain.repository.qiblah.CompassSensorManager as DomainCompassSensorManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow

class CompassSensorManager(private val context: Context) : DomainCompassSensorManager {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    override fun observeAzimuth(location: Location?): Flow<CompassData> = callbackFlow {
        var isFirstReading = true
        var hasRotationVector = false

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasGeomagnetic = false

        var currentAccuracy = CompassData.SENSOR_ACCURACY_HIGH

        val declination = if (location != null && location.latitude != 0.0 && location.longitude != 0.0) {
            val geoField = GeomagneticField(
                location.latitude.toFloat(),
                location.longitude.toFloat(),
                0f,
                System.currentTimeMillis(),
            )
            geoField.declination
        } else {
            0f
        }

        var sinSum = 0f
        var cosSum = 0f

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                var rawAzimuth: Float? = null

                @Suppress("DEPRECATION")
                val rotation = windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
                var axisX = SensorManager.AXIS_X
                var axisY = SensorManager.AXIS_Y

                when (rotation) {
                    Surface.ROTATION_90 -> {
                        axisX = SensorManager.AXIS_Y
                        axisY = SensorManager.AXIS_MINUS_X
                    }
                    Surface.ROTATION_180 -> {
                        axisX = SensorManager.AXIS_MINUS_X
                        axisY = SensorManager.AXIS_MINUS_Y
                    }
                    Surface.ROTATION_270 -> {
                        axisX = SensorManager.AXIS_MINUS_Y
                        axisY = SensorManager.AXIS_X
                    }
                }

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val remappedMatrix = FloatArray(9)
                    SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, remappedMatrix)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(remappedMatrix, orientation)
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
                            val remappedMatrix = FloatArray(9)
                            SensorManager.remapCoordinateSystem(rotationMatrix, axisX, axisY, remappedMatrix)
                            val orientation = FloatArray(3)
                            SensorManager.getOrientation(remappedMatrix, orientation)
                            rawAzimuth = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
                        }
                    }
                }

                rawAzimuth?.let { magAzimuth ->
                    val trueAzimuth = (magAzimuth + declination + 360f) % 360f
                    val rad = Math.toRadians(trueAzimuth.toDouble())
                    val sinVal = kotlin.math.sin(rad).toFloat()
                    val cosVal = kotlin.math.cos(rad).toFloat()

                    if (isFirstReading) {
                        sinSum = sinVal
                        cosSum = cosVal
                        isFirstReading = false
                    } else {
                        val alpha = 0.15f
                        sinSum += alpha * (sinVal - sinSum)
                        cosSum += alpha * (cosVal - cosSum)
                    }

                    val smoothedRad = kotlin.math.atan2(sinSum.toDouble(), cosSum.toDouble())
                    val smoothedAzimuth = (Math.toDegrees(smoothedRad).toFloat() + 360f) % 360f

                    trySend(CompassData(azimuth = smoothedAzimuth, accuracy = currentAccuracy))
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD || sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    currentAccuracy = when (accuracy) {
                        SensorManager.SENSOR_STATUS_UNRELIABLE -> CompassData.SENSOR_ACCURACY_UNRELIABLE
                        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> CompassData.SENSOR_ACCURACY_LOW
                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> CompassData.SENSOR_ACCURACY_MEDIUM
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> CompassData.SENSOR_ACCURACY_HIGH
                        else -> CompassData.SENSOR_ACCURACY_HIGH
                    }
                }
            }
        }

        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationVectorSensor != null) {
            hasRotationVector = true
            sensorManager.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            val accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val magSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            accelSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            magSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }.buffer(Channel.CONFLATED)
}
