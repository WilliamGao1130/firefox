/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.debugsettings.autofill

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import mozilla.components.compose.base.annotation.FlexibleWindowLightDarkPreview
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.components.components
import org.bluepowerrobotics.bpfox.compose.list.SwitchListItem
import org.bluepowerrobotics.bpfox.compose.list.TextListItem
import org.bluepowerrobotics.bpfox.debugsettings.store.DebugDrawerAction
import org.bluepowerrobotics.bpfox.debugsettings.store.DebugDrawerStore
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme
import org.bluepowerrobotics.bpfox.utils.Settings

@Composable
internal fun AutofillTools(
    debugDrawerStore: DebugDrawerStore,
    modifier: Modifier = Modifier,
    settings: Settings = components.settings,
) {
    var isChecked by remember { mutableStateOf(settings.allowScreenCaptureInSecureScreens) }

    Column(modifier = modifier) {
        SwitchListItem(
            label = stringResource(R.string.autofill_debug_enable_screen_capture),
            checked = isChecked,
            showSwitchAfter = true,
            onClick = {
                val newCheckedState = !isChecked
                isChecked = newCheckedState
                settings.allowScreenCaptureInSecureScreens = newCheckedState
            },
        )

        HorizontalDivider()

        TextListItem(
            label = stringResource(id = R.string.debug_drawer_logins_title),
            onClick = {
                debugDrawerStore.dispatch(DebugDrawerAction.NavigateTo.Logins)
            },
        )

        HorizontalDivider()

        TextListItem(
            label = stringResource(id = R.string.debug_drawer_addresses_title),
            onClick = {
                debugDrawerStore.dispatch(DebugDrawerAction.NavigateTo.Addresses)
            },
        )

        HorizontalDivider()

        TextListItem(
            label = stringResource(id = R.string.debug_drawer_credit_cards_title),
            onClick = {
                debugDrawerStore.dispatch(DebugDrawerAction.NavigateTo.CreditCards)
            },
        )

        HorizontalDivider()
    }
}

@FlexibleWindowLightDarkPreview
@Composable
internal fun AutofillToolsPreview() {
    FirefoxTheme {
        AutofillTools(
            debugDrawerStore = DebugDrawerStore(),
            settings = Settings(LocalContext.current.applicationContext),
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        )
    }
}
