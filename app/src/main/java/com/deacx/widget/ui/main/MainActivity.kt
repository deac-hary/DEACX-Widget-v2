package com.deacx.widget.ui.main

import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Bundle
import android.transition.TransitionManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.deacx.widget.R
import com.deacx.widget.databinding.ActivityMainBinding
import com.deacx.widget.widget.greeting.GreetingPeriod
import com.deacx.widget.widget.render.DeacxWidgetRenderer
import com.deacx.widget.widget.render.WidgetDateFormat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime

/**
 * Entry point for the DEACX companion app. The preview inflates the exact
 * widget layout (not a mockup) so it always matches what's rendered onto
 * the home screen.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private val locationPermissionLauncher =
    registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationPermissionLauncher.launch(
    arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
)
        val preview = binding.widgetPreview
        preview.widgetGreetingText.setText(DeacxWidgetRenderer.greetingTextRes(GreetingPeriod.current()))
        preview.widgetDateText.text = LocalDateTime.now().format(WidgetDateFormat.formatter)

        binding.displayTextInput.doAfterTextChanged { editable ->
            viewModel.onDraftTextChanged(editable?.toString().orEmpty())
        }
        binding.saveButton.setOnClickListener { viewModel.save() }
        binding.resetButton.setOnClickListener { viewModel.resetToDefault() }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.draftText.collect { text ->
                        // Guard against feedback: only push into the EditText
                        // when the change came from outside (the initial
                        // DataStore load or Reset), not from the user's own
                        // typing.
                        if (binding.displayTextInput.text?.toString() != text) {
                            binding.displayTextInput.setText(text)
                            binding.displayTextInput.setSelection(text.length)
                        }
                        preview.widgetCustomText.text = text
                        preview.widgetCustomText.isVisible = text.isNotBlank()
                    }
                }
                launch {
                    viewModel.isSaved.collect { isSaved ->
                        TransitionManager.beginDelayedTransition(binding.root)
                        binding.saveButton.isEnabled = !isSaved
                        binding.saveButton.setText(
                            if (isSaved) R.string.save_button_saved_label else R.string.save_button_label
                        )
                    }
                }
                launch {
                    viewModel.saveError.collect {
                        Snackbar.make(binding.root, R.string.save_error_message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
