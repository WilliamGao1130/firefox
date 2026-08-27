/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.settings.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import androidx.navigation.fragment.findNavController
import mozilla.components.browser.state.search.SearchEngine
import mozilla.components.support.ktx.android.view.hideKeyboard
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.e2e.SystemInsetsPaddedFragment
import org.bluepowerrobotics.bpfox.ext.components
import org.bluepowerrobotics.bpfox.ext.requireComponents
import org.bluepowerrobotics.bpfox.ext.showToolbar
import org.bluepowerrobotics.bpfox.theme.FirefoxTheme

/** A [Fragment] that allows user to select what search engine shortcuts will be visible in the quick search menu. */
class SearchShortcutsFragment : Fragment(), SystemInsetsPaddedFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
        FirefoxTheme {
            SearchEngineShortcuts(
                getString(R.string.preferences_category_engines_in_search_menu),
                requireComponents.core.store,
                onEditEngineClicked = {
                    navigateToSaveEngineFragment(it)
                },
                onCheckboxClicked = { engine, isEnabled ->
                    requireContext()
                        .components
                        .useCases
                        .searchUseCases
                        .updateDisabledSearchEngineIds(
                            engine.id,
                            isEnabled,
                        )
                },
                onDeleteEngineClicked = {
                    requireContext().components.useCases.searchUseCases.removeSearchEngine(it)
                },
                onAddEngineClicked = {
                    navigateToSaveEngineFragment()
                },
            )
        }
    }

    private fun navigateToSaveEngineFragment(engine: SearchEngine? = null) {
        val directions =
            SearchShortcutsFragmentDirections.actionSearchShortcutsFragmentToSaveSearchEngineFragment(engine?.id)

        findNavController().navigate(directions)
    }

    override fun onResume() {
        super.onResume()
        view?.hideKeyboard()
        showToolbar(getString(R.string.preferences_manage_search_shortcuts_2))
    }
}
