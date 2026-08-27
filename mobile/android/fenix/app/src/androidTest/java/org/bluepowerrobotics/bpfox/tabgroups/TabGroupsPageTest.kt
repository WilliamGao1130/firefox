/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.tabgroups

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.bluepowerrobotics.bpfox.tabstray.TabsTrayTestTag
import org.bluepowerrobotics.bpfox.tabstray.data.TabsTrayItem
import org.bluepowerrobotics.bpfox.tabstray.data.createTabGroup
import org.bluepowerrobotics.bpfox.tabstray.redux.state.TabsTrayState
import org.bluepowerrobotics.bpfox.tabstray.ui.tabpage.TabGroupsPage
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme

@RunWith(AndroidJUnit4::class)
class TabGroupsPageTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun verifyEmptyState() {
        composeTestRule.setContent {
            FirefoxTheme {
                TabGroupsPage(
                    state = TabsTrayState.TabGroupState(),
                    onTabGroupClick = {},
                    onEditTabGroupClick = {},
                    onShareTabGroupClick = {},
                    onDeleteTabGroupClick = {},
                )
            }
        }

        composeTestRule.onNodeWithTag(TabsTrayTestTag.EMPTY_TAB_GROUPS_LIST).assertIsDisplayed()
    }

    @Test
    fun verifyTabGroupClick() {
        val group = createTabGroup(title = "Group 1")
        var groupClicked = false
        var clickedGroup: TabsTrayItem.TabGroup? = null

        composeTestRule.setContent {
            FirefoxTheme {
                TabGroupsPage(
                    state = TabsTrayState.TabGroupState(groups = listOf(group)),
                    onTabGroupClick = {
                        groupClicked = true
                        clickedGroup = it
                    },
                    onEditTabGroupClick = {},
                    onShareTabGroupClick = {},
                    onDeleteTabGroupClick = {},
                )
            }
        }

        composeTestRule.onNodeWithTag("${TabsTrayTestTag.TAB_GROUP_ROOT}.${group.id}").performClick()

        assertTrue(groupClicked)
        assertEquals(group, clickedGroup)
    }

    @Test
    fun verifyDeleteTabGroupClick() {
        val group = createTabGroup(title = "Group 1")
        var deleteClicked = false
        var clickedGroup: TabsTrayItem.TabGroup? = null

        composeTestRule.setContent {
            FirefoxTheme {
                TabGroupsPage(
                    state = TabsTrayState.TabGroupState(groups = listOf(group)),
                    onTabGroupClick = {},
                    onDeleteTabGroupClick = {
                        deleteClicked = true
                        clickedGroup = it
                    },
                    onEditTabGroupClick = {},
                    onShareTabGroupClick = {},
                )
            }
        }

        composeTestRule.onAllNodesWithTag(TabsTrayTestTag.TAB_GROUP_THREE_DOT_BUTTON)[0].performClick()
        composeTestRule.onNodeWithTag(TabsTrayTestTag.DELETE_TAB_GROUP).performClick()

        assertTrue(deleteClicked)
        assertEquals(group, clickedGroup)
    }

    @Test
    fun verifyEditTabGroupClick() {
        val group = createTabGroup(title = "Group 1")
        var editClicked = false
        var clickedGroup: TabsTrayItem.TabGroup? = null

        composeTestRule.setContent {
            FirefoxTheme {
                TabGroupsPage(
                    state = TabsTrayState.TabGroupState(groups = listOf(group)),
                    onTabGroupClick = {},
                    onDeleteTabGroupClick = {},
                    onEditTabGroupClick = {
                        editClicked = true
                        clickedGroup = it
                    },
                    onShareTabGroupClick = {},
                )
            }
        }

        composeTestRule.onAllNodesWithTag(TabsTrayTestTag.TAB_GROUP_THREE_DOT_BUTTON)[0].performClick()
        composeTestRule.onNodeWithTag(TabsTrayTestTag.EDIT_TAB_GROUP).performClick()

        assertTrue(editClicked)
        assertEquals(group, clickedGroup)
    }
}
