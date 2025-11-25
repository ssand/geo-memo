package com.sap.codelab.view.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sap.codelab.data.model.MemoEntity
import com.sap.codelab.databinding.ActivityViewMemoBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

internal const val BUNDLE_MEMO_ID: String = "memoId"

/**
 * Activity that allows a user to see the details of a memo.
 */
internal class ViewMemo : AppCompatActivity() {

    val viewModel: ViewMemoViewModel by viewModel()
    private lateinit var binding: ActivityViewMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        if (savedInstanceState == null) {
            // Observe the memo state flow for changes
            lifecycleScope.launch {
                viewModel.memo.collect { value ->
                    value?.let { memo ->
                        // Update the UI whenever the memo changes
                        updateUI(memo)
                    }
                }
            }
            val id = intent.getLongExtra(BUNDLE_MEMO_ID, -1)
            viewModel.loadMemo(id)
        }
    }

    /**
     * Updates the UI with the given memo details.
     *
     * @param memo - the memo whose details are to be displayed.
     */
    private fun updateUI(memo: MemoEntity) {
        binding.contentCreateMemo.run {
            memoTitle.setText(memo.title)
            memoDescription.setText(memo.description)
            memoTitle.isEnabled = false
            memoDescription.isEnabled = false
        }
    }
}
