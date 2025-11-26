package com.sap.codelab.view.create

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.sap.codelab.R
import com.sap.codelab.databinding.ActivityCreateMemoBinding
import com.sap.codelab.utils.LOCATION_PERMISSIONS
import com.sap.codelab.utils.extensions.empty
import com.sap.codelab.utils.hasLocationPermission
import com.sap.codelab.utils.openAppSettings
import com.sap.codelab.utils.shouldShowLocationRationale
import com.sap.codelab.view.locationselection.LocationSelectionActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Activity that allows a user to create a new Memo.
 */
internal class CreateMemo : AppCompatActivity() {

    private val viewModel: CreateMemoViewModel by viewModel()
    private lateinit var binding: ActivityCreateMemoBinding
    private var pendingSaveAfterPermission = false

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.values.all { it }
            if (granted && pendingSaveAfterPermission) {
                pendingSaveAfterPermission = false
                saveMemo()
            } else if (!granted) {
                showPermissionRationale()
            }
        }

    private val locationSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val lat = data?.getDoubleExtra(LocationSelectionActivity.EXTRA_LATITUDE, 0.0) ?: 0.0
                val lng = data?.getDoubleExtra(LocationSelectionActivity.EXTRA_LONGITUDE, 0.0) ?: 0.0
                viewModel.updateLocation(lat, lng)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupUiListeners()
        observeState()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        viewModel.updateMemoData(
            binding.contentCreateMemo.memoTitle.text.toString(),
            binding.contentCreateMemo.memoDescription.text.toString()
        )
        super.onSaveInstanceState(outState)
    }

    private fun setupUiListeners() = binding.contentCreateMemo.run {
        btnLocation.setOnClickListener { openLocationSelection() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    with(binding.contentCreateMemo) {
                        memoTitle.setText(state.title)
                        memoDescription.setText(state.description)
                        txtLocation.text =
                            if (state.hasLocation)
                                getString(R.string.selected_location, state.reminderLatitude, state.reminderLongitude)
                            else
                                String.empty()

                        memoTitleContainer.error = getErrorMessage(state.titleError, R.string.memo_title_empty_error)
                        memoDescription.error = getErrorMessage(state.descriptionError, R.string.memo_text_empty_error)
                    }
                }
            }
        }
    }

    private fun openLocationSelection() {
        val intent = Intent(this, LocationSelectionActivity::class.java)
        locationSelectionLauncher.launch(intent)
    }

    private fun requestLocationPermission() {
        if (shouldShowLocationRationale(this))
            showPermissionRationale()
        else
            locationPermissionLauncher.launch(LOCATION_PERMISSIONS)
    }

    private fun showPermissionRationale() {
        Snackbar.make(
            binding.root,
            R.string.location_permission_rationale,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.action_open_settings) {
            openAppSettings(this)
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_memo, menu)
        return true
    }

    /**
     * Handles actionbar interactions.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                checkPermissionsAndSaveMemo()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Saves the memo if the input is valid; otherwise shows the corresponding error messages.
     */
    private fun checkPermissionsAndSaveMemo() {
        viewModel.updateMemoData(
            binding.contentCreateMemo.memoTitle.text.toString(),
            binding.contentCreateMemo.memoDescription.text.toString()
        )
        if (viewModel.validateInput()) {
            if (viewModel.hasLocation() && !hasLocationPermission(this)) {
                pendingSaveAfterPermission = true
                requestLocationPermission()
            } else
                saveMemo()
        }
    }

    @SuppressLint("MissingPermission")
    private fun saveMemo() {
        viewModel.saveMemo {
            setResult(RESULT_OK)
            finish()
        }
    }

    /**
     * Returns the error message if there is an error, or an empty string otherwise.
     *
     * @param hasError          - whether there is an error.
     * @param errorMessageResId - the resource id of the error message to show.
     * @return the error message if there is an error, or an empty string otherwise.
     */
    private fun getErrorMessage(hasError: Boolean, @StringRes errorMessageResId: Int): String? {
        return if (hasError)
            getString(errorMessageResId)
        else
            null
    }
}
