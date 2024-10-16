package com.dicoding.restaurantreview.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dicoding.restaurantreview.R
import com.dicoding.restaurantreview.databinding.FragmentSettingsBinding
import com.dicoding.restaurantreview.ui.ViewModelFactory
import com.dicoding.restaurantreview.worker.DailyReminderWorker
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel: SettingsViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(context, getString(R.string.notifications_permission_granted), Toast.LENGTH_SHORT).show()
                enableDailyReminder(true)
            } else {
                Toast.makeText(context, getString(R.string.notifications_permission_rejected), Toast.LENGTH_SHORT).show()
                binding.switchDailyReminder.isChecked = false
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupThemeSwitch()
        setupDailyReminderSwitch()
    }

    private fun setupThemeSwitch() {
        settingsViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setupDailyReminderSwitch() {
        settingsViewModel.getDailyReminderSettings().observe(viewLifecycleOwner) { isEnabled ->
            binding.switchDailyReminder.isChecked = isEnabled
        }

        binding.switchDailyReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        enableDailyReminder(true)
                    }
                } else {
                    enableDailyReminder(true)
                }
            } else {
                enableDailyReminder(false)
            }
        }
    }

    private fun enableDailyReminder(enable: Boolean) {
        settingsViewModel.saveDailyReminderSetting(enable)
        if (enable) {
            val dailyReminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
                .build()
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                DAILY_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                dailyReminderRequest
            )
        } else {
            WorkManager.getInstance(requireContext()).cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DAILY_REMINDER_WORK_NAME = "daily_reminder_work"
    }
}