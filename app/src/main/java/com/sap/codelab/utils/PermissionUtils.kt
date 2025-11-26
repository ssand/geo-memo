package com.sap.codelab.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION
)

fun hasLocationPermission(context: Context): Boolean {
    return hasPermission(context, *LOCATION_PERMISSIONS)
}

fun shouldShowLocationRationale(context: Activity): Boolean {
    return shouldShowRationale(context, *LOCATION_PERMISSIONS)
}

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private fun hasPermission(context: Context, vararg permission: String): Boolean {
    permission.iterator().forEach {
        if (ContextCompat.checkSelfPermission(context, it) != android.content.pm.PackageManager.PERMISSION_GRANTED)
            return false
    }
    return true
}

fun shouldShowRationale(context: Activity, vararg permission: String): Boolean {
    return permission.any { shouldShowRequestPermissionRationale(context, it) }
}
