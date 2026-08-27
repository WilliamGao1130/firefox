/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.selectors

import mozilla.components.compose.browser.toolbar.concept.BrowserToolbarTestTags.ADDRESSBAR_SEARCH_BOX
import org.bluepowerrobotics.bpfox.bookmarks.BookmarksTestTag.BOOKMARK_SEARCH_ITEM
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.Selector
import org.bluepowerrobotics.bpfox.ui.efficiency.helpers.SelectorStrategy

object BookmarkSearchSelectors {

    val SEARCH_BOX =
        Selector(
            strategy = SelectorStrategy.COMPOSE_EDITABLE_BY_ANCESTOR_TAG,
            value = ADDRESSBAR_SEARCH_BOX,
            description = "Bookmark search box",
            groups = listOf("requiredForPage"),
        )

    val SEARCH_ITEM =
        Selector(
            strategy = SelectorStrategy.COMPOSE_BY_TAG,
            value = BOOKMARK_SEARCH_ITEM,
            description = "Bookmark search item",
            groups = listOf(),
        )

    val all =
        listOf(
            SEARCH_BOX,
            SEARCH_ITEM,
        )
}
