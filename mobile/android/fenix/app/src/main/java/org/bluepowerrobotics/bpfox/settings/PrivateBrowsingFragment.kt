/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.biometric.BiometricManager
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import org.bluepowerrobotics.bpfox.GleanMetrics.PrivateBrowsingLocked
import org.bluepowerrobotics.bpfox.HomeActivity
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.components.DefaultPendingIntentFactory
import org.bluepowerrobotics.bpfox.components.DefaultShortcutManagerCompatWrapper
import org.bluepowerrobotics.bpfox.components.PrivateShortcutCreateManager
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.registerForActivityResult
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.ext.showToolbar
import org.bluepowerrobotics.bpfox.settings.biometric.DefaultBiometricUtils
import org.bluepowerrobotics.bpfox.settings.biometric.ext.isAuthenticatorAvailable
import org.bluepowerrobotics.bpfox.settings.biometric.ext.isDeviceLockCapable

/** Lets the user customize Private browsing options. */
class PrivateBrowsingFragment : PreferenceFragmentCompat(), SystemInsetsPaddedFragment {
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.preferences_private_browsing_options))

        // If user changes their device lock status (i.e. adds or removes device lock),
        // check the device pin status and determine if private browsing lock toggle
        // should be shown upon resuming.
        updatePreferences()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.private_browsing_preferences, rootKey)
        startForResult =
            registerForActivityResult(
                onFailure = { PrivateBrowsingLocked.authFailure.record() },
                onSuccess = { onSuccessfulAuthenticationUsingFallbackPrompt() },
            )
        updatePreferences()
    }

    @Suppress("CognitiveComplexMethod")
    private fun updatePreferences() {
        val biometricManager = BiometricManager.from(requireContext())
        val deviceCapable = biometricManager.isDeviceLockCapable()
        val userHasEnabledCapability = biometricManager.isAuthenticatorAvailable()

        requirePreference<Preference>(R.string.pref_key_add_private_browsing_shortcut).apply {
            setOnPreferenceClickListener {
                val privateShortcutCreateManager =
                    PrivateShortcutCreateManager(
                        shortcutManagerWrapper = DefaultShortcutManagerCompatWrapper(),
                        pendingIntentFactory = DefaultPendingIntentFactory(),
                    )
                privateShortcutCreateManager.createPrivateShortcut(requireContext())
                true
            }
        }

        requirePreference<SwitchPreferenceCompat>(R.string.pref_key_open_links_in_a_private_tab).apply {
            onPreferenceChangeListener = SharedPreferenceUpdater()
            isChecked = context.components.settings.openLinksInAPrivateTab
        }

        requirePreference<SwitchPreferenceCompat>(R.string.pref_key_allow_screenshots_in_private_mode).apply {
            isEnabled =
                !(context.components.settings.privateBrowsingModeLocked && biometricManager.isAuthenticatorAvailable())
            onPreferenceChangeListener =
                object : SharedPreferenceUpdater() {
                    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
                        if (
                            (activity as? HomeActivity)?.browsingModeManager?.mode?.isPrivate == true &&
                                newValue == false
                        ) {
                            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        } else {
                            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                        }
                        return super.onPreferenceChange(preference, newValue)
                    }
                }
        }

        // Show divider only if user does not have a device lock set
        requirePreference<PreferenceCategory>(R.string.pref_key_pbm_lock_category_divider).apply {
            isVisible =
                deviceCapable &&
                    !userHasEnabledCapability &&
                    context.components.settings.privateBrowsingLockedFeatureEnabled
        }

        requirePreference<SwitchPreferenceCompat>(R.string.pref_key_private_browsing_locked_enabled).apply {
            isChecked =
                context.components.settings.privateBrowsingModeLocked && biometricManager.isAuthenticatorAvailable()
            isVisible = context.components.settings.privateBrowsingLockedFeatureEnabled && deviceCapable
            isEnabled = userHasEnabledCapability

            setOnPreferenceChangeListener { preference, newValue ->
                val pbmLockEnabled = newValue as? Boolean ?: return@setOnPreferenceChangeListener false

                val titleRes =
                    if (pbmLockEnabled) {
                        R.string.pbm_authentication_enable_lock
                    } else {
                        R.string.pbm_authentication_disable_lock
                    }

                DefaultBiometricUtils.bindBiometricsCredentialsPromptOrShowWarning(
                    titleRes = titleRes,
                    view = requireView(),
                    onShowPinVerification = { intent -> startForResult.launch(intent) },
                    onAuthSuccess = {
                        onSuccessfulAuthenticationUsingPrimaryPrompt(
                            pbmLockEnabled = pbmLockEnabled,
                            preference = preference,
                        )
                    },
                    onAuthFailure = { PrivateBrowsingLocked.authFailure.record() },
                )

                PrivateBrowsingLocked.promptShown.record()

                // Cancel toggle change until biometric is successful
                false
            }
        }

        requirePreference<Preference>(R.string.pref_key_private_browsing_lock_device_feature_enabled).apply {
            isVisible =
                deviceCapable &&
                    !userHasEnabledCapability &&
                    context.components.settings.privateBrowsingLockedFeatureEnabled

            setOnPreferenceClickListener {
                context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                true
            }
        }
    }

    private fun onSuccessfulAuthenticationUsingFallbackPrompt() {
        PrivateBrowsingLocked.authSuccess.record()

        val newValue = !requireComponents.settings.privateBrowsingModeLocked
        recordPbmLockFeatureEnabledStateTelemetry(newValue)
        requireComponents.settings.privateBrowsingModeLocked = newValue
        // Update switch state manually
        requirePreference<SwitchPreferenceCompat>(R.string.pref_key_private_browsing_locked_enabled).apply {
            isChecked = !isChecked
        }
        updateScreenshotPreference(newValue)
    }

    private fun onSuccessfulAuthenticationUsingPrimaryPrompt(
        pbmLockEnabled: Boolean,
        preference: Preference,
    ) {
        PrivateBrowsingLocked.authSuccess.record()

        recordPbmLockFeatureEnabledStateTelemetry(pbmLockEnabled)
        requireComponents.settings.privateBrowsingModeLocked = pbmLockEnabled
        // Update switch state manually
        (preference as? SwitchPreferenceCompat)?.isChecked = pbmLockEnabled
        updateScreenshotPreference(pbmLockEnabled)
    }

    private fun recordPbmLockFeatureEnabledStateTelemetry(pbmLockEnabled: Boolean) {
        if (pbmLockEnabled) {
            PrivateBrowsingLocked.featureEnabled.record()
        } else {
            PrivateBrowsingLocked.featureDisabled.record()
        }
    }

    private fun updateScreenshotPreference(pbmLockEnabled: Boolean) {
        requirePreference<SwitchPreferenceCompat>(R.string.pref_key_allow_screenshots_in_private_mode).apply {
            if (pbmLockEnabled) {
                requireComponents.settings.allowScreenshotsInPrivateMode = false
                isChecked = false
                if ((activity as? HomeActivity)?.browsingModeManager?.mode?.isPrivate == true) {
                    activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
                }
            }
            isEnabled = !pbmLockEnabled
        }
    }
}
