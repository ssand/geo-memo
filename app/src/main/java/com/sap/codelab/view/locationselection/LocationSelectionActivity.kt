package com.sap.codelab.view.locationselection

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.sap.codelab.R
import com.sap.codelab.databinding.ActivityLocationSelectionBinding
import com.sap.codelab.utils.hasLocationPermission
import com.sap.codelab.utils.openAppSettings
import com.sap.codelab.utils.shouldShowLocationRationale
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * Location selection Activity:
 * 1. To align with the rest of the provided code-base implemented this screen as an Activity.
 * 2. Used Open Street Maps as it doesn't require a API key as Google Maps does. The downside it that it's slower than
 * Google Maps.
 */
class LocationSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLocationSelectionBinding
    private var locationOverlay: MyLocationNewOverlay? = null

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.values.any { it }
            if (granted)
                enableLocationOverlay()
            else {
                binding.map.controller.setCenter(DEFAULT_START_POINT)
                showPermissionRationale()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSelectionBinding.inflate(layoutInflater)

        Configuration.getInstance()
            .load(
                applicationContext,
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
            )

        setContentView(binding.root)

        binding.buttonCurrentLocation.setOnClickListener { centerOnUserLocation() }

        initMap()
        configureLocation()
        initLocationSelection()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        binding.map.onPause()
        super.onPause()
    }

    private fun initMap() {
        with(binding.map) {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    // Location selection listener
    private fun initLocationSelection() {
        val receiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                with(binding.map) {
                    overlays.removeAll { it is Marker }
                    val marker = Marker(this)
                    marker.position = p
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    overlays.add(marker)
                    invalidate()
                }

                val resultIntent = Intent().apply {
                    putExtra(EXTRA_LATITUDE, p.latitude)
                    putExtra(EXTRA_LONGITUDE, p.longitude)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean = false
        }

        binding.map.overlays.add(MapEventsOverlay(receiver))
    }

    // check for location permissions and init the location overlay
    private fun configureLocation() {
        if (!hasLocationPermission(this)) {
            requestLocationPermission()
            binding.map.controller.setCenter(DEFAULT_START_POINT)
        } else
            enableLocationOverlay()
    }

    private fun enableLocationOverlay() {
        if (locationOverlay != null) return

        val provider = GpsMyLocationProvider(this)
        val overlay = MyLocationNewOverlay(provider, binding.map)
        overlay.enableMyLocation()

        binding.map.overlays.add(overlay)
        locationOverlay = overlay

        provider.lastKnownLocation.let { lastLocation ->
            if (lastLocation != null)
                binding.map.controller.setCenter(
                    GeoPoint(lastLocation.latitude, lastLocation.longitude)
                )
        }

        overlay.runOnFirstFix {
            val userLocation = overlay.myLocation
            if (userLocation != null) {
                runOnUiThread { binding.map.controller.animateTo(userLocation) }
            }
        }
    }

    private fun centerOnUserLocation() {
        if (!hasLocationPermission(this)) {
            if (shouldShowLocationRationale(this))
                showPermissionRationale()
            else
                requestLocationPermission()
            return
        }

        if (locationOverlay == null)
            enableLocationOverlay()

        locationOverlay?.myLocation?.let { binding.map.controller.animateTo(it) }
    }

    private fun showPermissionRationale() {
        Snackbar.make(
            binding.root,
            R.string.location_permission_rationale,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.action_open_settings) {
            openAppSettings(this)
        }.show()
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(LOCATION_PERMISSIONS)
    }

    companion object {
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private val DEFAULT_START_POINT = GeoPoint(50.0, 50.0)
    }
}
