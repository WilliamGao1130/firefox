/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.tabstray.ui.theme

import androidx.compose.runtime.Composable
import org.bluepowerrobotics.bpfox.tabstray.redux.state.Page
import org.bluepowerrobotics.bpfox.theme.DefaultThemeProvider
import org.bluepowerrobotics.bpfox.theme.Theme
import org.bluepowerrobotics.bpfox.theme.ThemeProvider

/**
 * [ThemeProvider] for the Tab Manager.
 *
 * When on [Page.PrivateTabs], [Theme.Private] is used. Otherwise, we fallback to [DefaultThemeProvider].
 */
class TabManagerThemeProvider(val selectedPage: Page) : ThemeProvider {
    @Composable
    override fun provideTheme(): Theme =
        if (selectedPage == Page.PrivateTabs) {
            Theme.Private
        } else {
            DefaultThemeProvider.provideTheme()
        }
}
