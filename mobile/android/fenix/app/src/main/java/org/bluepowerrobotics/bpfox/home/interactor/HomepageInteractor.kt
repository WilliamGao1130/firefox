/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.home.interactor

import org.bluepowerrobotics.bpfox.home.bookmarks.interactor.BookmarksInteractor
import org.bluepowerrobotics.bpfox.home.pocket.interactor.PocketStoriesInteractor
import org.bluepowerrobotics.bpfox.home.privatebrowsing.interactor.PrivateBrowsingInteractor
import org.bluepowerrobotics.bpfox.home.recentsyncedtabs.interactor.RecentSyncedTabInteractor
import org.bluepowerrobotics.bpfox.home.recenttabs.interactor.RecentTabInteractor
import org.bluepowerrobotics.bpfox.home.recentvisits.interactor.RecentVisitsInteractor
import org.bluepowerrobotics.bpfox.home.search.HomeSearchInteractor
import org.bluepowerrobotics.bpfox.home.sessioncontrol.CollectionInteractor
import org.bluepowerrobotics.bpfox.home.sessioncontrol.MessageCardInteractor
import org.bluepowerrobotics.bpfox.home.sessioncontrol.SetupChecklistInteractor
import org.bluepowerrobotics.bpfox.home.sessioncontrol.TabSessionInteractor
import org.bluepowerrobotics.bpfox.home.sessioncontrol.TrackingProtectionInteractor
import org.bluepowerrobotics.bpfox.home.sessioncontrol.WallpaperInteractor
import org.bluepowerrobotics.bpfox.home.termsofuse.PrivacyNoticeBannerInteractor
import org.bluepowerrobotics.bpfox.home.toolbar.ToolbarInteractor
import org.bluepowerrobotics.bpfox.home.topsites.interactor.TopSiteInteractor

/** Homepage interactor for interactions with the homepage UI. */
interface HomepageInteractor :
    CollectionInteractor,
    TopSiteInteractor,
    TabSessionInteractor,
    ToolbarInteractor,
    HomeSearchInteractor,
    MessageCardInteractor,
    PrivacyNoticeBannerInteractor,
    RecentTabInteractor,
    RecentSyncedTabInteractor,
    BookmarksInteractor,
    RecentVisitsInteractor,
    PocketStoriesInteractor,
    PrivateBrowsingInteractor,
    WallpaperInteractor,
    SetupChecklistInteractor,
    TrackingProtectionInteractor
