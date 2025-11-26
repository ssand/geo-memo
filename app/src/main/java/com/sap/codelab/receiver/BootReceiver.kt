package com.sap.codelab.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.location.GeofenceScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Re-registers geofences on device reboot.
 */
class BootReceiver() : BroadcastReceiver(), KoinComponent {

    private val memoRepository: MemoRepository by inject()
    private val geofenceScheduler: GeofenceScheduler by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (!listOf("android.intent.action.QUICKBOOT_POWERON", "android.intent.action.BOOT_COMPLETED")
                .contains(intent?.action)
        ) return

        CoroutineScope(Dispatchers.IO).launch {
            memoRepository.getActiveMemosWithLocation().forEach {
                // Register geofence for each active memo with location. Location data is guaranteed to be present here.
                geofenceScheduler.registerMemoGeofence(it.id, it.reminderLatitude!!, it.reminderLongitude!!)
            }
        }
    }
}
