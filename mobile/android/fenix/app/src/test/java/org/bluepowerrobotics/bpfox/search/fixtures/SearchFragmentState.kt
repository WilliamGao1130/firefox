/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.search.fixtures

import org.bluepowerrobotics.bpfox.components.metrics.MetricsUtils
import org.bluepowerrobotics.bpfox.search.SearchEngineSource
import org.bluepowerrobotics.bpfox.search.SearchFragmentState

/** Prebuilt empty [SearchFragmentState]. */
val EMPTY_SEARCH_FRAGMENT_STATE =
    SearchFragmentState(
        query = "",
        url = "",
        searchTerms = "",
        searchEngineSource = SearchEngineSource.None,
        defaultEngine = null,
        searchSuggestionsProviders = emptyList(),
        hiddenSuggestions = emptySet(),
        searchSuggestionsOrientedAtBottom = false,
        searchStartedForCurrentUrl = false,
        shouldShowSearchSuggestions = false,
        showSearchSuggestionsFromCurrentEngine = false,
        showSearchSuggestionsHint = false,
        showClipboardSuggestions = false,
        showSearchTermHistory = false,
        showHistorySuggestionsForCurrentEngine = false,
        showAllHistorySuggestions = false,
        showBookmarksSuggestionsForCurrentEngine = false,
        showAllBookmarkSuggestions = false,
        showSyncedTabsSuggestionsForCurrentEngine = false,
        showAllSyncedTabsSuggestions = false,
        showSessionSuggestionsForCurrentEngine = false,
        showAllSessionSuggestions = false,
        showSponsoredSuggestions = false,
        showNonSponsoredSuggestions = false,
        showStocksSuggestions = false,
        showSportsSuggestions = false,
        showFlightsSuggestions = false,
        showTrendingSearches = false,
        showRecentSearches = false,
        showQrButton = false,
        tabId = null,
        pastedText = null,
        searchAccessPoint = MetricsUtils.Source.NONE,
        clipboardHasUrl = false,
    )
