/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import mozilla.telemetry.glean.private.NoExtras
import org.bluepowerrobotics.bpfox.GleanMetrics.GenaiAiControls
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.ext.showToolbar
import org.bluepowerrobotics.bpfox.settings.SupportUtils
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme

/** A fragment displaying the AI Controls settings screen. */
class AIControlsFragment : Fragment(), SystemInsetsPaddedFragment {
    private val args by navArgs<AIControlsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = content {
        val registry = requireComponents.aiFeatureRegistry
        val features = remember { registry.getFeatures().sortedForDisplay() }
        val featureBlock = requireComponents.aiControlsFeatureBlock
        val scope = rememberCoroutineScope()

        val aiBlockUiController = remember {
            AIBlockUIController.default(
                featureBlock = featureBlock,
                scope = scope,
            )
        }

        val showDialog = aiBlockUiController.showDialogFlow.collectAsState()
        val isBlocked = featureBlock.isBlocked.collectAsState(initial = false)

        FirefoxTheme {
            AIControlsScreen(
                registeredFeatures = features,
                showDialog = showDialog.value,
                isBlocked = isBlocked.value,
                itemToScrollTo = args.preferenceToScrollTo,
                onDialogDismiss = {
                    GenaiAiControls.globalPrefConfirmationClick.record(
                        GenaiAiControls.GlobalPrefConfirmationClickExtra(element = "cancel")
                    )
                    aiBlockUiController.onDialogDismiss()
                },
                onDialogConfirm = {
                    GenaiAiControls.globalPrefConfirmationClick.record(
                        GenaiAiControls.GlobalPrefConfirmationClickExtra(element = "block")
                    )
                    aiBlockUiController.onDialogConfirm()
                },
                onToggle = { currentlyBlocked ->
                    GenaiAiControls.globalPrefToggle.record(
                        GenaiAiControls.GlobalPrefToggleExtra(block = !currentlyBlocked)
                    )
                    if (!currentlyBlocked) {
                        GenaiAiControls.globalPrefConfirmationShown.record(NoExtras())
                    }
                    aiBlockUiController.onToggle(currentlyBlocked)
                },
                onFeatureToggle = { feature, enabled ->
                    GenaiAiControls.featurePrefChange.record(
                        GenaiAiControls.FeaturePrefChangeExtra(
                            feature = feature.id.value,
                            selection = if (enabled) "enabled" else "blocked",
                        )
                    )
                    scope.launch { feature.set(enabled) }
                },
                onFeatureNavLinkClick = { destination, featureId ->
                    GenaiAiControls.featureLinkClick.record(GenaiAiControls.FeatureLinkClickExtra(link = featureId))
                    destination.nav(this)
                },
                onBannerLearnMoreClick = {
                    GenaiAiControls.featureLinkClick.record(
                        GenaiAiControls.FeatureLinkClickExtra(link = "global_control")
                    )
                    openAiControlsSumoPage()
                },
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Ensures the toolbar shows when navigating to this fragment via Global Directions.
        showToolbar(getString(R.string.preferences_ai_controls))
    }

    private fun openAiControlsSumoPage() {
        val context = requireContext()
        SupportUtils.launchSandboxCustomTab(
            context = context,
            url =
                SupportUtils.getSumoURLForTopic(
                    context = context,
                    topic = SupportUtils.SumoTopic.AI_CONTROLS,
                    useMobilePage = true,
                ),
        )
    }
}
