/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings.sitepermissions

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.R as materialR
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.concept.engine.permission.SitePermissions
import mozilla.components.support.ktx.kotlin.stripDefaultPort
import mozilla.components.ui.widgets.withCenterAlignedButtons
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.ext.showToolbar
import org.bluepowerrobotics.bpfox.settings.PhoneFeature
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.AUTOPLAY
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.CAMERA
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.CROSS_ORIGIN_STORAGE_ACCESS
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.LOCAL_DEVICE_ACCESS
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.LOCAL_NETWORK_ACCESS
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.LOCATION
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.MEDIA_KEY_SYSTEM_ACCESS
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.MICROPHONE
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.NOTIFICATION
import org.bluepowerrobotics.bpfox.settings.PhoneFeature.PERSISTENT_STORAGE
import org.bluepowerrobotics.bpfox.settings.quicksettings.AutoplayValue
import org.bluepowerrobotics.bpfox.settings.requirePreference
import org.bluepowerrobotics.bpfox.utils.Settings

/** Settings screen allowing users to manage the status of all browser permissions. */
@SuppressWarnings("TooManyFunctions")
class SitePermissionsDetailsExceptionsFragment : PreferenceFragmentCompat(), SystemInsetsPaddedFragment {
    @VisibleForTesting internal lateinit var sitePermissions: SitePermissions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sitePermissions = SitePermissionsDetailsExceptionsFragmentArgs.fromBundle(requireArguments()).sitePermissions
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.site_permissions_details_exceptions_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        showToolbar(sitePermissions.origin.stripDefaultPort())
        viewLifecycleOwner.lifecycleScope.launch(Main) {
            sitePermissions =
                requireNotNull(
                    requireComponents.core.permissionStorage.findSitePermissionsBy(
                        sitePermissions.origin,
                        private = false,
                    )
                )
            bindCategoryPhoneFeatures()
        }
    }

    @VisibleForTesting
    internal fun bindCategoryPhoneFeatures() {
        val settings = provideSettings()

        initPhoneFeature(CAMERA)
        initPhoneFeature(LOCATION)
        initPhoneFeature(MICROPHONE)
        initPhoneFeature(NOTIFICATION)
        initPhoneFeature(PERSISTENT_STORAGE)
        initPhoneFeature(CROSS_ORIGIN_STORAGE_ACCESS)
        initPhoneFeature(MEDIA_KEY_SYSTEM_ACCESS)
        initAutoplayFeature()
        initPhoneFeature(LOCAL_DEVICE_ACCESS, visible = settings.isLnaFeatureEnabled)
        initPhoneFeature(LOCAL_NETWORK_ACCESS, visible = settings.isLnaFeatureEnabled)
        bindClearPermissionsButton()
    }

    @VisibleForTesting
    internal fun initPhoneFeature(phoneFeature: PhoneFeature, visible: Boolean = true) {
        val summary = phoneFeature.getActionLabel(provideContext(), sitePermissions)
        val preference = getPreference(phoneFeature)
        preference.summary = summary
        preference.isVisible = visible
        preference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            navigateToPhoneFeature(phoneFeature)
            true
        }
        preference.icon?.setTint(
            MaterialColors.getColor(
                provideContext(),
                materialR.attr.colorOnSurface,
                "Could not resolve themed color",
            )
        )
    }

    @VisibleForTesting
    internal fun getPreference(phoneFeature: PhoneFeature): Preference =
        requirePreference(phoneFeature.getPreferenceId())

    @VisibleForTesting internal fun provideContext(): Context = requireContext()

    @VisibleForTesting internal fun provideSettings(): Settings = provideContext().components.settings

    @VisibleForTesting
    internal fun initAutoplayFeature() {
        val phoneFeature = getPreference(AUTOPLAY)
        phoneFeature.summary = getAutoplayLabel()

        phoneFeature.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            navigateToPhoneFeature(AUTOPLAY)
            true
        }
    }

    @VisibleForTesting
    internal fun getAutoplayLabel(): String {
        val context = provideContext()
        val settings = provideSettings()
        val autoplayValues = AutoplayValue.values(context, settings, sitePermissions)
        val selected =
            autoplayValues.firstOrNull { it.isSelected() }
                ?: AutoplayValue.getFallbackValue(
                    context,
                    settings,
                    sitePermissions,
                )

        return selected.label
    }

    @VisibleForTesting
    internal fun bindClearPermissionsButton() {
        val button: Preference = requirePreference(R.string.pref_key_exceptions_clear_site_permissions)

        button.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .apply {
                    setMessage(R.string.confirm_clear_permissions_site)
                    setTitle(R.string.clear_permissions)
                    setPositiveButton(R.string.clear_permissions_positive) { dialog: DialogInterface, _ ->
                        clearSitePermissions()
                        dialog.dismiss()
                    }
                    setNegativeButton(R.string.clear_permissions_negative) { dialog: DialogInterface, _ ->
                        dialog.cancel()
                    }
                }
                .show()
                .withCenterAlignedButtons()

            true
        }
    }

    private fun clearSitePermissions() {
        // Use fragment's lifecycle; the view may be gone by the time dialog is interacted with.
        lifecycleScope.launch(IO) {
            requireContext().components.core.permissionStorage.deleteSitePermissions(sitePermissions)
            withContext(Main) {
                requireView().findNavController().popBackStack()
                requireContext().components.tryReloadTabBy(sitePermissions.origin)
            }
        }
    }

    @VisibleForTesting
    internal fun navigateToPhoneFeature(phoneFeature: PhoneFeature) {
        val directions =
            SitePermissionsDetailsExceptionsFragmentDirections.actionSitePermissionsToExceptionsToManagePhoneFeature(
                phoneFeature = phoneFeature,
                sitePermissions = sitePermissions,
            )
        requireView().findNavController().navigate(directions)
    }
}
