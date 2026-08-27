/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.customtabs

import android.content.Context
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.browser.state.selector.findCustomTab
import mozilla.components.browser.state.state.CustomTabSessionState
import mozilla.components.browser.state.state.ExternalAppType
import mozilla.components.browser.state.state.SessionState
import mozilla.components.concept.engine.permission.SitePermissions
import mozilla.components.feature.contextmenu.ContextMenuCandidate
import mozilla.components.feature.customtabs.CustomTabWindowFeature
import mozilla.components.feature.pwa.feature.ManifestUpdateFeature
import mozilla.components.feature.pwa.feature.WebAppActivityFeature
import mozilla.components.feature.pwa.feature.WebAppContentFeature
import mozilla.components.feature.pwa.feature.WebAppHideToolbarFeature
import mozilla.components.feature.pwa.feature.WebAppSiteControlsFeature
import mozilla.components.support.base.feature.ViewBoundFeatureWrapper
import mozilla.components.support.ktx.android.arch.lifecycle.addObservers
import mozilla.components.support.ktx.kotlin.isContentUrl
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.browser.BaseBrowserFragment
import org.bluepowerrobotics.bpfox.browser.ContextMenuSnackbarDelegate
import org.bluepowerrobotics.bpfox.browser.CustomTabColorsBinding
import org.bluepowerrobotics.bpfox.browser.CustomTabContextMenuCandidate
import org.bluepowerrobotics.bpfox.customtabs.ext.updateCustomTabsColors
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.nav
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.ext.runIfFragmentIsAttached

/** Fragment used for browsing the web within external apps. */
class ExternalAppBrowserFragment : BaseBrowserFragment(), SystemInsetsPaddedFragment {

    private val args by navArgs<ExternalAppBrowserFragmentArgs>()

    override val isSandboxCustomTab: Boolean
        get() = args.isSandboxCustomTab

    private val customTabColorsBinding = ViewBoundFeatureWrapper<CustomTabColorsBinding>()
    private val windowFeature = ViewBoundFeatureWrapper<CustomTabWindowFeature>()

    @Suppress("LongMethod")
    override fun initializeUI(view: View, tab: SessionState) {
        super.initializeUI(view, tab)

        val customTabSessionId = customTabSessionId ?: return
        val activity = requireActivity()
        val components = activity.components

        val manifest =
            args.webAppManifestUrl
                ?.ifEmpty { null }
                ?.let { url ->
                    requireComponents.core.webAppManifestStorage.getManifestCache(url)
                }

        val browserStore = requireComponents.core.store
        if (browserStore.state.findCustomTab(customTabSessionId)?.content?.private == false) {
            val settings = requireComponents.settings
            browserScreenStore.updateCustomTabsColors(
                context = requireContext(),
                customTab = (tab as? CustomTabSessionState),
                deviceUIMode = requireContext().resources.configuration.uiMode,
                shouldFollowDeviceTheme = settings.shouldFollowDeviceTheme,
                shouldUseLightTheme = settings.shouldUseLightTheme,
            )

            customTabColorsBinding.set(
                feature =
                    CustomTabColorsBinding(
                        browserScreenStore = browserScreenStore,
                        window = requireActivity().window,
                    ),
                owner = this,
                view = view,
            )
        }

        windowFeature.set(
            feature = CustomTabWindowFeature(activity, components.core.store, customTabSessionId),
            owner = this,
            view = view,
        )

        val customTabSession = (tab as? CustomTabSessionState)
        val isPwaTabOrTwaTab =
            customTabSession?.config?.externalAppType == ExternalAppType.PROGRESSIVE_WEB_APP ||
                customTabSession?.config?.externalAppType == ExternalAppType.TRUSTED_WEB_ACTIVITY

        // Only set hideToolbarFeature if isPwaTabOrTwaTab
        if (isPwaTabOrTwaTab) {
            hideToolbarFeature.set(
                feature =
                    WebAppHideToolbarFeature(
                        store = requireComponents.core.store,
                        customTabsStore = requireComponents.core.customTabsStore,
                        tabId = customTabSessionId,
                        manifest = manifest,
                        scope = viewLifecycleOwner.lifecycleScope,
                    ) { toolbarVisible ->
                        webAppToolbarShouldBeVisible = toolbarVisible
                        when (toolbarVisible) {
                            true -> collapseBrowserView()
                            false -> expandBrowserView()
                        }
                    },
                owner = this,
                view = view,
            )
        }

        if (manifest != null) {
            activity.lifecycle.addObservers(
                WebAppActivityFeature(
                    activity,
                    components.core.icons,
                    manifest,
                ),
                WebAppContentFeature(
                    store = requireComponents.core.store,
                    tabId = customTabSessionId,
                    manifest,
                ),
                ManifestUpdateFeature(
                    activity.applicationContext,
                    requireComponents.core.store,
                    requireComponents.core.webAppShortcutManager,
                    requireComponents.core.webAppManifestStorage,
                    customTabSessionId,
                    manifest,
                ),
            )
            viewLifecycleOwner.lifecycle.addObserver(
                WebAppSiteControlsFeature(
                    activity.applicationContext,
                    requireComponents.core.store,
                    requireComponents.useCases.sessionUseCases.reload,
                    customTabSessionId,
                    manifest,
                    WebAppSiteControlsBuilder(
                        requireComponents.core.store,
                        requireComponents.useCases.sessionUseCases.reload,
                        customTabSessionId,
                        manifest,
                    ),
                    notificationsDelegate = requireComponents.notificationsDelegate,
                )
            )
        } else {
            viewLifecycleOwner.lifecycle.addObserver(
                PoweredByNotification(
                    activity.applicationContext,
                    requireComponents.core.store,
                    customTabSessionId,
                    requireComponents.notificationsDelegate,
                )
            )
        }
    }

    override fun navToQuickSettingsSheet(tab: SessionState, sitePermissions: SitePermissions?) {
        requireComponents.useCases.trackingProtectionUseCases.containsException(tab.id) { contains ->
            lifecycleScope.launch {
                withContext(Dispatchers.Main) {
                    runIfFragmentIsAttached {
                        val directions =
                            ExternalAppBrowserFragmentDirections.actionGlobalTrustPanelFragment(
                                sessionId = tab.id,
                                url = tab.content.url,
                                title = tab.content.title,
                                isLocalPdf = tab.content.url.isContentUrl(),
                                isSecured = tab.content.securityInfo.isSecure,
                                sitePermissions = sitePermissions,
                                certificate = tab.content.securityInfo.certificate,
                                permissionHighlights = tab.content.permissionHighlights,
                                isTrackingProtectionEnabled = tab.trackingProtection.enabled && !contains,
                            )
                        nav(R.id.externalAppBrowserFragment, directions)
                    }
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

    override fun getContextMenuCandidates(
        context: Context,
        view: View,
    ): List<ContextMenuCandidate> =
        CustomTabContextMenuCandidate.defaultCandidates(
            context,
            context.components.useCases.contextMenuUseCases,
            view,
            ContextMenuSnackbarDelegate(),
        )
}
