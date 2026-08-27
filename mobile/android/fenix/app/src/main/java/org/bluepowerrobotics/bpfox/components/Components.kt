/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.components

import android.app.Application
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import com.google.android.play.core.review.ReviewManagerFactory
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.MainScope
import mozilla.components.concept.ai.controls.AIFeatureBlock
import mozilla.components.concept.ai.controls.AIFeatureRegistry
import mozilla.components.feature.addons.AddonManager
import mozilla.components.feature.addons.amo.AMOAddonsProvider
import mozilla.components.feature.addons.migration.DefaultSupportedAddonsChecker
import mozilla.components.feature.addons.update.DefaultAddonUpdater
import mozilla.components.feature.autofill.AutofillConfiguration
import mozilla.components.feature.summarize.PageSummaryFeature
import mozilla.components.feature.summarize.settings.SummarizationSettings
import mozilla.components.lib.ai.controls.AIFeatureBlockStorage
import mozilla.components.lib.ai.controls.dataStore
import mozilla.components.lib.ai.controls.default
import mozilla.components.lib.crash.store.CrashAction
import mozilla.components.lib.crash.store.CrashMiddleware
import mozilla.components.lib.integrity.googleplay.GooglePlayIntegrityClient
import mozilla.components.lib.llm.mlpa.MlpaTokenStorage
import mozilla.components.lib.publicsuffixlist.PublicSuffixList
import mozilla.components.service.fxrelay.eligibility.RelayEligibilityStore
import mozilla.components.service.fxrelay.eligibility.middlewares.ClearLastUsedMiddleware
import mozilla.components.support.base.android.DefaultProcessInfoProvider
import mozilla.components.support.base.android.NotificationsDelegate
import mozilla.components.support.base.worker.Frequency
import mozilla.components.support.remotesettings.DefaultRemoteSettingsSyncScheduler
import mozilla.components.support.remotesettings.RemoteSettingsServer
import mozilla.components.support.remotesettings.RemoteSettingsService
import mozilla.components.support.remotesettings.into
import mozilla.components.support.utils.BuildManufacturerChecker
import mozilla.components.support.utils.ClipboardHandler
import mozilla.components.support.utils.ext.packageManagerWrapper
import org.bluepowerrobotics.bpfox.BuildConfig
import org.bluepowerrobotics.bpfox.Config
import org.bluepowerrobotics.bpfox.FeatureFlags
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.autofill.AutofillConfirmActivity
import org.bluepowerrobotics.bpfox.autofill.AutofillSearchActivity
import org.bluepowerrobotics.bpfox.autofill.AutofillUnlockActivity
import org.bluepowerrobotics.bpfox.browser.relay.ErrorMessages
import org.bluepowerrobotics.bpfox.browser.relay.RelayFeatureIntegration
import org.bluepowerrobotics.bpfox.components.appstate.AppAction
import org.bluepowerrobotics.bpfox.components.appstate.AppState
import org.bluepowerrobotics.bpfox.components.appstate.setup.checklist.SetupChecklistState
import org.bluepowerrobotics.bpfox.components.appstate.setup.checklist.getSetupChecklistCollection
import org.bluepowerrobotics.bpfox.components.bookmarks.lastSavedFolderCache
import org.bluepowerrobotics.bpfox.components.ipprotection.IPProtection
import org.bluepowerrobotics.bpfox.components.ipprotection.IPProtectionAuthSources
import org.bluepowerrobotics.bpfox.components.llm.Llm
import org.bluepowerrobotics.bpfox.components.llm.ext.accessTokenProvider
import org.bluepowerrobotics.bpfox.components.metrics.MetricsMiddleware
import org.bluepowerrobotics.bpfox.crashes.CrashReportingAppMiddleware
import org.bluepowerrobotics.bpfox.crashes.SettingsCrashReportCache
import org.bluepowerrobotics.bpfox.datastore.pocketStoriesSelectedCategoriesDataStore
import org.bluepowerrobotics.bpfox.distributions.DefaultDistributionBrowserStoreProvider
import org.bluepowerrobotics.bpfox.distributions.DefaultDistributionProviderChecker
import org.bluepowerrobotics.bpfox.distributions.DefaultDistributionSettings
import org.bluepowerrobotics.bpfox.distributions.DistributionIdManager
import org.bluepowerrobotics.bpfox.ext.asRecentTabs
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.filterState
import org.bluepowerrobotics.bpfox.ext.sort
import org.bluepowerrobotics.bpfox.home.PocketMiddleware
import org.bluepowerrobotics.bpfox.home.SettingsBackedPocketSettings
import org.bluepowerrobotics.bpfox.home.blocklist.BlocklistHandler
import org.bluepowerrobotics.bpfox.home.blocklist.BlocklistMiddleware
import org.bluepowerrobotics.bpfox.home.collections.migration.CollectionsMigrationRepository
import org.bluepowerrobotics.bpfox.home.collections.migration.DefaultCollectionsMigrationRepository
import org.bluepowerrobotics.bpfox.home.middleware.HomeTelemetryMiddleware
import org.bluepowerrobotics.bpfox.home.setup.store.DefaultSetupChecklistRepository
import org.bluepowerrobotics.bpfox.home.setup.store.SetupChecklistPreferencesMiddleware
import org.bluepowerrobotics.bpfox.home.setup.store.SetupChecklistTelemetryMiddleware
import org.bluepowerrobotics.bpfox.ipprotection.store.DefaultIPProtectionPromptRepository
import org.bluepowerrobotics.bpfox.messaging.state.MessagingMiddleware
import org.bluepowerrobotics.bpfox.nimbus.FxNimbus
import org.bluepowerrobotics.bpfox.onboarding.FenixOnboarding
import org.bluepowerrobotics.bpfox.perf.AppLinkIntentLaunchTypeProvider
import org.bluepowerrobotics.bpfox.perf.AppStartReasonProvider
import org.bluepowerrobotics.bpfox.perf.StartupActivityLog
import org.bluepowerrobotics.bpfox.perf.StartupStateProvider
import org.bluepowerrobotics.bpfox.perf.StrictModeManager
import org.bluepowerrobotics.bpfox.perf.lazyMonitored
import org.bluepowerrobotics.bpfox.reviewprompt.ReviewPromptMiddleware
import org.bluepowerrobotics.bpfox.search.VoiceSearchAIControlFeature
import org.bluepowerrobotics.bpfox.settings.emailmasks.middleware.DefaultEmailMasksRepository
import org.bluepowerrobotics.bpfox.settings.emailmasks.middleware.EmailMasksRepository
import org.bluepowerrobotics.bpfox.settings.settingssearch.DefaultFenixSettingsIndexer
import org.bluepowerrobotics.bpfox.termsofuse.TermsOfUseManager
import org.bluepowerrobotics.bpfox.termsofuse.store.DefaultTermsOfUsePromptRepository
import org.bluepowerrobotics.bpfox.utils.Settings
import org.bluepowerrobotics.bpfox.utils.isLargeScreenSize
import org.bluepowerrobotics.bpfox.wifi.WifiConnectionMonitor
import org.mozilla.gecko.search.SearchWidgetProvider

private const val AMO_COLLECTION_MAX_CACHE_AGE = 2 * 24 * 60L // Two days in minutes

/**
 * Provides access to all components. This class is an implementation of the Service Locator pattern, which helps us
 * manage the dependencies in our app.
 *
 * Note: these aren't just "components" from "android-components": they're any "component" that can be considered a
 * building block of our app.
 */
class Components(
    private val context: Context,
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() },
) {
    val backgroundServices by lazyMonitored {
        BackgroundServices(
            context,
            push,
            settings,
            analytics.crashReporter,
            core.lazyHistoryStorage,
            core.lazyBookmarksStorage,
            core.lazyPasswordsStorage,
            core.lazyRemoteTabsStorage,
            core.lazyAutofillStorage,
            strictMode,
        )
    }
    val services by lazyMonitored { Services(context, core.store, backgroundServices.accountManager) }
    val core by lazyMonitored {
        Core(context, analytics.crashReporter, strictMode, performance.visualCompletenessQueue)
    }

    val useCases by lazyMonitored {
        UseCases(
            context = context,
            crashReporter = lazyMonitored { analytics.crashReporter },
            engine = lazyMonitored { core.engine },
            store = lazyMonitored { core.store },
            shortcutManager = lazyMonitored { core.webAppShortcutManager },
            topSitesStorage = lazyMonitored { core.topSitesStorage },
            bookmarksStorage = lazyMonitored { core.bookmarksStorage },
            historyStorage = lazyMonitored { core.historyStorage },
            lastSavedFolderCache = lazyMonitored { context.components.settings.lastSavedFolderCache },
            syncedTabsCommands = lazyMonitored { backgroundServices.syncedTabsCommands },
            adsClientProvider = ads.lazyAdsClientProvider,
            appStore = lazyMonitored { appStore },
            client = lazyMonitored { core.client },
            strictMode = lazyMonitored { strictMode },
        )
    }

    private val notificationManagerCompat = NotificationManagerCompat.from(context)

    val notificationsDelegate: NotificationsDelegate by lazyMonitored {
        NotificationsDelegate(notificationManagerCompat)
    }

    val intentProcessors by lazyMonitored {
        IntentProcessors(
            context,
            core.store,
            useCases.sessionUseCases,
            useCases.tabsUseCases,
            useCases.customTabsUseCases,
            useCases.searchUseCases,
            core.webAppManifestStorage,
            core.engine,
        )
    }

    val addonsProvider by lazyMonitored {
        // Check if we have a customized (overridden) AMO collection (supported in Nightly & Beta)
        if (FeatureFlags.customExtensionCollectionFeature && settings.amoCollectionOverrideConfigured()) {
            AMOAddonsProvider(
                context,
                core.client,
                collectionUser = settings.overrideAmoUser,
                collectionName = settings.overrideAmoCollection,
            )
        }
        // Use build config otherwise
        else if (BuildConfig.AMO_COLLECTION_USER.isNotEmpty() && BuildConfig.AMO_COLLECTION_NAME.isNotEmpty()) {
            AMOAddonsProvider(
                context,
                core.client,
                serverURL = BuildConfig.AMO_SERVER_URL,
                collectionUser = BuildConfig.AMO_COLLECTION_USER,
                collectionName = BuildConfig.AMO_COLLECTION_NAME,
                maxCacheAgeInMinutes = AMO_COLLECTION_MAX_CACHE_AGE,
            )
        }
        // Fall back to defaults
        else {
            AMOAddonsProvider(context, core.client, maxCacheAgeInMinutes = AMO_COLLECTION_MAX_CACHE_AGE)
        }
    }

    @Suppress("MagicNumber")
    val addonUpdater by lazyMonitored {
        DefaultAddonUpdater(context, Frequency(12, TimeUnit.HOURS), notificationsDelegate)
    }

    @Suppress("MagicNumber")
    val supportedAddonsChecker by lazyMonitored {
        DefaultSupportedAddonsChecker(
            context,
            Frequency(12, TimeUnit.HOURS),
        )
    }

    @Suppress("MagicNumber")
    val remoteSettingsSyncScheduler by lazyMonitored {
        DefaultRemoteSettingsSyncScheduler(
            context,
            Frequency(2, TimeUnit.HOURS),
        )
    }

    val addonManager by lazyMonitored {
        AddonManager(core.store, core.engine, addonsProvider, addonUpdater)
    }

    val analytics by lazyMonitored { Analytics(context, settings, nimbus, performance.visualCompletenessQueue) }

    val remoteSettingsService = lazyMonitored {
        RemoteSettingsService(
            context,
            when (settings.remoteSettingsServer) {
                context.getString(R.string.remote_settings_server_prod) -> RemoteSettingsServer.Prod.into()
                context.getString(R.string.remote_settings_server_dev) -> RemoteSettingsServer.Dev.into()
                context.getString(R.string.remote_settings_server_stage) -> RemoteSettingsServer.Stage.into()
                else -> RemoteSettingsServer.Prod.into()
            },
            channel = BuildConfig.BUILD_TYPE,
            // Need to send this value separately, since `isLargeScreenSize()` is a fenix extension
            isLargeScreenSize = context.isLargeScreenSize(),
        )
    }
    val nimbus: NimbusComponents by lazyMonitored {
        NimbusComponents(
            context = context,
            engine = lazyMonitored { core.engine },
            remoteSettingsService = remoteSettingsService.value.remoteSettingsService,
        )
    }
    val publicSuffixList by lazyMonitored { PublicSuffixList(context) }
    val clipboardHandler by lazyMonitored { ClipboardHandler(context) }
    val performance by lazyMonitored { PerformanceComponent() }
    val push by lazyMonitored { Push(context, analytics.crashReporter) }
    val wifiConnectionMonitor by lazyMonitored { WifiConnectionMonitor(context as Application) }

    val strictMode by lazyMonitored {
        StrictModeManager(
            Config.channel.isDebug,
            this,
            BuildManufacturerChecker(),
        )
    }
    val settings by lazyMonitored { Settings(context) }
    val fenixOnboarding by lazyMonitored { FenixOnboarding(context) }

    val playStoreReviewPromptController by lazyMonitored {
        PlayStoreReviewPromptController(
            manager = ReviewManagerFactory.create(context),
            numberOfAppLaunches = { settings.numberOfAppLaunches },
        )
    }

    val autofillConfiguration by lazyMonitored {
        AutofillConfiguration(
            storage = core.passwordsStorage,
            publicSuffixList = publicSuffixList,
            unlockActivity = AutofillUnlockActivity::class.java,
            confirmActivity = AutofillConfirmActivity::class.java,
            searchActivity = AutofillSearchActivity::class.java,
            applicationName = context.getString(R.string.app_name),
            httpClient = core.client,
        )
    }

    val appStartReasonProvider by lazyMonitored {
        AppStartReasonProvider(processInfoProvider = DefaultProcessInfoProvider())
    }
    val startupActivityLog by lazyMonitored { StartupActivityLog() }
    val startupStateProvider by lazyMonitored { StartupStateProvider(startupActivityLog, appStartReasonProvider) }

    val appLinkIntentLaunchTypeProvider by lazyMonitored { AppLinkIntentLaunchTypeProvider(appStartReasonProvider) }

    val appStore by lazyMonitored {
        val blocklistHandler = BlocklistHandler(settings)

        AppStore(
                initialState =
                    AppState(
                            collections = core.tabCollectionStorage.cachedTabCollections,
                            expandedCollections = emptySet(),
                            topSites = core.topSitesStorage.cachedTopSites.sort(),
                            bookmarks = emptyList(),
                            // Provide an initial state for recent tabs to prevent re-rendering on the home screen.
                            //  This will otherwise cause a visual jump as the section gets rendered from no state
                            //  to some state.
                            recentTabs =
                                if (settings.showRecentTabsFeature) {
                                    core.store.state.asRecentTabs()
                                } else {
                                    emptyList()
                                },
                            recentHistory = emptyList(),
                            setupChecklistState = setupChecklistState(),
                        )
                        .run { filterState(blocklistHandler) },
                middlewares =
                    listOf(
                        ProfileMarkerMiddleware(markerName = "AppStore", profiler = core.engine.profiler),
                        LogMiddleware(tag = "AppStore", shouldIncludeDetailedData = { Config.channel.isDebug }),
                        BlocklistMiddleware(blocklistHandler),
                        PocketMiddleware(
                            lazyMonitored { core.pocketStoriesService },
                            context.pocketStoriesSelectedCategoriesDataStore,
                            SettingsBackedPocketSettings(settings),
                            performance.visualCompletenessQueue,
                        ),
                        MessagingMiddleware(
                            controller = nimbus.messaging,
                            settings = settings,
                        ),
                        MetricsMiddleware(
                            metrics = analytics.metrics,
                            nimbusEventStore = nimbus.events,
                        ),
                        CrashReportingAppMiddleware(
                            CrashMiddleware(
                                cache = SettingsCrashReportCache(settings),
                                crashReporter = analytics.crashReporter,
                                currentTimeInMillis = currentTimeMillis,
                            )
                        ),
                        HomeTelemetryMiddleware(),
                        SetupChecklistPreferencesMiddleware(DefaultSetupChecklistRepository(context, settings)),
                        SetupChecklistTelemetryMiddleware(),
                        ReviewPromptMiddleware(
                                continuousOnboardingInProgress = {
                                    val continuousOnboardingCompleted =
                                        settings.seventhDayOnboardingCompletedTimestamp != -1L
                                    settings.continuousOnboardingFeatureEnabled && !continuousOnboardingCompleted
                                },
                                shouldShowCustomPrompt = {
                                    settings.customReviewPromptUiEnabled && settings.isTelemetryEnabled
                                },
                                disableCustomPrompt = { settings.customReviewPromptUiEnabled = false },
                                createJexlHelper = nimbus::createJexlHelper,
                                nimbusEventStore = nimbus.events,
                            )
                            .also {
                                settings.migrateLastReviewPromptTimePrefIfNeeded(nimbus.events)
                            },
                        AppVisualCompletenessMiddleware(performance.visualCompletenessQueue),
                    ),
            )
            .also {
                it.dispatch(AppAction.SetupChecklistAction.Init)
                it.dispatch(AppAction.CrashActionWrapper(CrashAction.Initialize))
            }
    }

    private fun setupChecklistState() =
        if (settings.showSetupChecklist) {
            val type = FxNimbus.features.setupChecklist.value().setupChecklistType
            SetupChecklistState(
                checklistItems =
                    getSetupChecklistCollection(
                        settings = settings,
                        collection = type,
                        tabStripEnabled = settings.isTabStripEnabled,
                    )
            )
        } else {
            null
        }

    val fxSuggest by lazyMonitored { FxSuggest(context, remoteSettingsService.value, analytics.crashReporter) }

    val distributionIdManager by lazyMonitored {
        DistributionIdManager(
            packageManager = context.packageManagerWrapper,
            browserStoreProvider = DefaultDistributionBrowserStoreProvider(core.store),
            distributionProviderChecker = DefaultDistributionProviderChecker(context),
            distributionSettings = DefaultDistributionSettings(settings),
            metricController = analytics.metrics,
        )
    }

    val integrityClient by lazyMonitored {
        GooglePlayIntegrityClient.create(
            context = context,
            projectNumberToken = BuildConfig.GPS_INTEGRITY_TOKEN,
            requestHashProvider = clientUUID,
        )
    }

    val termsOfUsePromptRepository by lazyMonitored {
        DefaultTermsOfUsePromptRepository(settings)
    }

    val termsOfUseManager by lazyMonitored {
        TermsOfUseManager(termsOfUsePromptRepository)
    }

    val settingsIndexer by lazyMonitored {
        DefaultFenixSettingsIndexer(
            context = context,
            additionalProviders =
                settingsSearchProviders(summarizationFeatureConfiguration = core.summarizeFeatureSettings),
        )
    }

    val ipProtectionPromptRepository by lazyMonitored {
        DefaultIPProtectionPromptRepository(settings)
    }

    val ads by lazyMonitored {
        Ads(context = context)
    }

    val relayEligibilityStore by lazyMonitored {
        RelayEligibilityStore(middleware = listOf(ClearLastUsedMiddleware()))
    }

    val emailMasksRepository: EmailMasksRepository by lazyMonitored {
        DefaultEmailMasksRepository(settings)
    }

    val collectionsMigrationRepository: CollectionsMigrationRepository by lazyMonitored {
        DefaultCollectionsMigrationRepository(settings)
    }

    val relayFeatureIntegration by lazyMonitored {
        RelayFeatureIntegration(
            engine = core.engine,
            accountManager = backgroundServices.accountManager,
            store = relayEligibilityStore,
            appStore = appStore,
            errorMessages =
                ErrorMessages(
                    maxMasksReached = context.getString(R.string.email_masks_max_free_tier_reached),
                    errorRetrievingMasks = context.getString(R.string.email_masks_error_retrieving_masks),
                ),
        )
    }

    val aiFeatureBlockStorage by lazyMonitored {
        AIFeatureBlockStorage.dataStore(context)
    }

    val summarizationSettings: SummarizationSettings by lazyMonitored {
        SummarizationSettings.dataStore(context)
    }

    val aiFeatureRegistry by lazyMonitored {
        AIFeatureRegistry.default(scope = MainScope(), context = context).also {
            if (settings.shakeToSummarizeFeatureFlagEnabled) {
                it.register(PageSummaryFeature(summarizationSettings))
            }
            it.register(
                VoiceSearchAIControlFeature(
                    settings = settings,
                    onUpdateWidget = { SearchWidgetProvider.updateAllWidgets(context) },
                )
            )
        }
    }

    @Suppress("unused")
    val aiControlsFeatureBlock by lazyMonitored {
        AIFeatureBlock.default(
            storage = aiFeatureBlockStorage,
            registry = aiFeatureRegistry,
        )
    }

    val llm: Llm by lazyMonitored {
        Llm(
            client = core.client,
            storage = MlpaTokenStorage.sharedPrefs(context),
            fxaTokenProvider = backgroundServices.accountManager.accessTokenProvider,
            integrityClient = integrityClient,
            userIdProvider = clientUUID,
        )
    }

    val clientUUID by lazyMonitored { ClientUUID.build(context) }

    val ipProtection by lazyMonitored {
        IPProtection(
            engine = core.engine,
            browserStore = core.store,
            syncStore = backgroundServices.syncStore,
            authSources =
                IPProtectionAuthSources(
                    fxaAccountManager = lazy { backgroundServices.accountManager },
                    integrityClient = lazy { integrityClient },
                ),
            lazyAppStore = lazy { appStore },
            settings = settings,
            context = context,
        )
    }
}

/** Returns the [Components] object from within a [Composable]. */
val components: Components
    @Composable @ReadOnlyComposable get() = LocalContext.current.components
