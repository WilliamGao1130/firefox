/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.components.share

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import mozilla.components.feature.session.SessionUseCases
import org.bluepowerrobotics.bpfox.R
import org.bluepowerrobotics.bpfox.ext.components

/** A BroadcastReceiver to handle the "Print" action. */
class PrintReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val printUseCase: SessionUseCases.PrintContentUseCase = context.components.useCases.sessionUseCases.printContent
        val id = intent.getStringExtra(TAB_ID_KEY)
        if (id != null) {
            printUseCase.invoke(id)
        } else {
            Toast.makeText(context, R.string.unable_to_print_page_error, Toast.LENGTH_SHORT).show()
        }
    }
}
