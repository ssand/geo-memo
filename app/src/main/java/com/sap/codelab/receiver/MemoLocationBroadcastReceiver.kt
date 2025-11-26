package com.sap.codelab.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.utils.ifLet
import com.sap.codelab.utils.showGeoNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Broadcast receiver triggered on [Geofence.GEOFENCE_TRANSITION_ENTER]
 * On receive gets the Memo id form the Intent, loads the memo from the database and triggers a notification.
 */
class MemoLocationBroadcastReceiver() : BroadcastReceiver(), KoinComponent {

    private val memoRepository: MemoRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_GEOFENCE_TRANSITION) return
        val geoEvent = GeofencingEvent.fromIntent(intent) ?: return
        if (geoEvent.hasError()) return

        if (geoEvent.geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER) return

        val memoIds = geoEvent.triggeringGeofences?.mapNotNull { it.requestId.toLongOrNull() }

        memoIds?.forEach { memoId ->
            CoroutineScope(Dispatchers.IO).launch {
                val memo = loadMemo(memoId)
                ifLet(memo?.title, memo?.description) { (title, description) ->
                    showGeoNotification(context, title, description, memoId.toInt())
                }
            }
        }
    }

    private suspend fun loadMemo(memoId: Long) =
        runCatching { memoRepository.getActiveMemoById(memoId) }.getOrNull()

    companion object {
        const val ACTION_GEOFENCE_TRANSITION = "com.sap.codelab.ACTION_GEOFENCE_TRANSITION"
    }
}
