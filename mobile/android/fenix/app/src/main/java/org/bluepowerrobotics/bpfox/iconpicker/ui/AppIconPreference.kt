/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.iconpicker.ui

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.findNavController
import mozilla.components.compose.base.annotation.FlexibleWindowPreview
import org.bluepowerrobotics.bpfox.GleanMetrics.CustomizationSettings
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.iconpicker.AppIcon
import org.bluepowerrobotics.bpfox.iconpicker.AppIconRepository
import org.bluepowerrobotics.bpfox.iconpicker.DefaultAppIconRepository
import org.bluepowerrobotics.bpfox.iconpicker.DefaultPackageManagerWrapper
import org.bluepowerrobotics.bpfox.settings.ComposePreference
import org.bluepowerrobotics.bpfox.settings.CustomizationFragmentDirections
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme
import org.bluepowerrobotics.bpfox.theme.PreviewThemeProvider
import org.bluepowerrobotics.bpfox.theme.Theme

private val IconSize = 40.dp

/**
 * User preference showing the currently selected icon and enables the user to navigate to the app icon selection view.
 */
class AppIconPreference
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ComposePreference(context, attrs) {

    private val appIconRepository: AppIconRepository by lazy {
        DefaultAppIconRepository(
            packageManager = DefaultPackageManagerWrapper(context.packageManager),
            packageName = context.packageName,
        )
    }

    @Composable
    override fun Content() {
        val view = LocalView.current

        SelectAppIcon(
            appIcon = appIconRepository.selectedAppIcon,
            onClick = {
                CustomizationSettings.appIconSelectionTapped.record()
                view
                    .findNavController()
                    .navigate(CustomizationFragmentDirections.actionCustomizationFragmentAppIconSelectionFragment())
            },
        )
    }
}

@Composable
private fun SelectAppIcon(
    appIcon: AppIcon,
    onClick: (AppIcon) -> Unit,
) {
    Surface {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .clickable { onClick(appIcon) }
                    .padding(
                        horizontal = FirefoxTheme.layout.space.dynamic200,
                        vertical = FirefoxTheme.layout.space.static100,
                    ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(
                appIcon = appIcon,
                iconSize = IconSize,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.preference_select_app_icon_title),
                    style = FirefoxTheme.typography.body1,
                )

                Text(
                    text = stringResource(appIcon.titleId),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = FirefoxTheme.typography.body2,
                )
            }
        }
    }
}

@FlexibleWindowPreview
@Composable
private fun SelectAppIconPreview(@PreviewParameter(PreviewThemeProvider::class) theme: Theme) {
    FirefoxTheme(theme) {
        SelectAppIcon(AppIcon.AppDefault) {}
    }
}
