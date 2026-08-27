/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import org.bluepowerrobotics.bpfox.GleanMetrics.SentFromFirefox
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.showToolbar

/** Lets the user customize link sharing feature. */
class LinkSharingFragment : PreferenceFragmentCompat(), SystemInsetsPaddedFragment {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.link_sharing_preferences, rootKey)

        requirePreference<SwitchPreferenceCompat>(R.string.pref_key_link_sharing).apply {
            isChecked = context.components.settings.whatsappLinkSharingEnabled

            onPreferenceChangeListener =
                object : SharedPreferenceUpdater() {
                    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
                        SentFromFirefox.settingsToggled.record(
                            SentFromFirefox.SettingsToggledExtra(enabled = newValue as Boolean)
                        )

                        return super.onPreferenceChange(preference, newValue)
                    }
                }

            title =
                getString(
                    R.string.link_sharing_toggle_body,
                    getString(R.string.firefox),
                )
            summary =
                getString(
                    R.string.link_sharing_toggle_title,
                    getString(R.string.firefox),
                )
        }
    }

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.preferences_link_sharing))
    }
}
