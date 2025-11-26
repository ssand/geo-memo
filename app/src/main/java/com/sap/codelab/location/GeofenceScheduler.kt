package com.sap.codelab.location

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.sap.codelab.receiver.MemoLocationBroadcastReceiver

/**
 * Geofence scheduler used to register and remove geofences.
 * Possible improvements:
 * - Handle `addGeofences` success/failure states
 */
class GeofenceScheduler(private val context: Context) {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context.applicationContext)

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun registerMemoGeofence(memoId: Long, memoLatitude: Double, memoLongitude: Double) {
        val geofence = Geofence.Builder()
            .setRequestId(memoId.toString())
            .setCircularRegion(
                memoLatitude,
                memoLongitude,
                GEOFENCE_RADIUS_METERS
            )
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(0)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(request, pendingIntent())
    }

    fun removeMemoGeofence(memoId: Long) {
        geofencingClient.removeGeofences(listOf(memoId.toString()))
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, MemoLocationBroadcastReceiver::class.java).apply {
            action = MemoLocationBroadcastReceiver.ACTION_GEOFENCE_TRANSITION
        }
        return PendingIntent.getBroadcast(
            context,
            GEOFENCE_INTENT_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    companion object {
        private const val GEOFENCE_INTENT_REQ_CODE = 0x01
        private const val GEOFENCE_RADIUS_METERS = 200f // 200m radius around the selected location
    }
}
