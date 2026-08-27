/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

// We need to do full suppressing as we have a import, that is deprecated.
// Follow-up https://github.com/mozilla-mobile/fenix/issues/25091

@file:Suppress("DEPRECATION")

package org.bluepowerrobotics.bpfox.components

import android.content.Context
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.concept.engine.Engine
import mozilla.components.feature.customtabs.CustomTabIntentProcessor
import mozilla.components.feature.intent.processing.TabIntentProcessor
import mozilla.components.feature.pwa.ManifestStorage
import mozilla.components.feature.pwa.intent.WebAppIntentProcessor
import mozilla.components.feature.search.SearchUseCases
import mozilla.components.feature.session.SessionUseCases
import mozilla.components.feature.tabs.CustomTabsUseCases
import mozilla.components.feature.tabs.TabsUseCases
import mozilla.components.feature.webnotifications.WebNotificationIntentProcessor
import org.bluepowerrobotics.bpfox.customtabs.FennecWebAppIntentProcessor
import org.bluepowerrobotics.bpfox.home.intent.FennecBookmarkShortcutsIntentProcessor
import org.bluepowerrobotics.bpfox.intent.ExternalDeepLinkIntentProcessor
import org.bluepowerrobotics.bpfox.perf.lazyMonitored
import org.bluepowerrobotics.bpfox.shortcut.PasswordManagerIntentProcessor

/** Component group for miscellaneous components. */
@Suppress("LongParameterList")
class IntentProcessors(
    private val context: Context,
    private val store: BrowserStore,
    private val sessionUseCases: SessionUseCases,
    private val tabsUseCases: TabsUseCases,
    private val customTabsUseCases: CustomTabsUseCases,
    private val searchUseCases: SearchUseCases,
    private val manifestStorage: ManifestStorage,
    private val engine: Engine,
) {
    /** Provides intent processing functionality for ACTION_VIEW and ACTION_SEND intents. */
    val intentProcessor by lazyMonitored {
        TabIntentProcessor(tabsUseCases, searchUseCases.newTabSearch, isPrivate = false, engine = engine)
    }

    /** Provides intent processing functionality for ACTION_VIEW and ACTION_SEND intents in private tabs. */
    val privateIntentProcessor by lazyMonitored {
        TabIntentProcessor(tabsUseCases, searchUseCases.newPrivateTabSearch, isPrivate = true, engine = engine)
    }

    val customTabIntentProcessor by lazyMonitored {
        CustomTabIntentProcessor(customTabsUseCases.add, context.resources, isPrivate = false)
    }

    val privateCustomTabIntentProcessor by lazyMonitored {
        CustomTabIntentProcessor(customTabsUseCases.add, context.resources, isPrivate = true)
    }

    val externalDeepLinkIntentProcessor by lazyMonitored {
        ExternalDeepLinkIntentProcessor()
    }

    val externalAppIntentProcessors by lazyMonitored {
        listOf(
            WebAppIntentProcessor(store, customTabsUseCases.addWebApp, sessionUseCases.loadUrl, manifestStorage),
            FennecWebAppIntentProcessor(context, customTabsUseCases, manifestStorage),
        )
    }

    val fennecPageShortcutIntentProcessor by lazyMonitored {
        FennecBookmarkShortcutsIntentProcessor(tabsUseCases.addTab)
    }

    val webNotificationsIntentProcessor by lazyMonitored {
        WebNotificationIntentProcessor(engine)
    }

    val passwordManagerIntentProcessor by lazyMonitored {
        PasswordManagerIntentProcessor()
    }
}
