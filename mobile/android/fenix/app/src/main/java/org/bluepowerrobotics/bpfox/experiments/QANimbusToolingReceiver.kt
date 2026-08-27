/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.experiments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.support.base.log.logger.Logger
import org.mozilla.experiments.nimbus.initializeTooling
import org.bluepowerrobotics.bpfox.ext.components

private val logger = Logger("QANimbusToolingReceiver")

/**
 * Receiver triggered on demand via `nimbus-cli` to manually enroll into Nimbus experiments.
 *
 * ```
 *   adb shell am broadcast -a org.bluepowerrobotics.bpfox.NIMBUS_TOOLING \
 *       -p org.bluepowerrobotics.bpfox
 * ```
 *
 * `-p org.bluepowerrobotics.bpfox` is the package name, so adjust that value for release/beta/nightly/debug.
 *
 * @param dispatcher the [CoroutineDispatcher] the tooling commands are applied on.
 */
class QANimbusToolingReceiver(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_NIMBUS_TOOLING) return

        logger.info("Enqueueing QANimbusToolingReceiver via debug trigger")

        val applicationContext = context.applicationContext

        val pendingResult: PendingResult? = goAsync()
        CoroutineScope(dispatcher).launch {
            try {
                applicationContext.components.nimbus.sdk.initializeTooling(
                    applicationContext,
                    intent,
                )
            } finally {
                logger.info("Nimbus tooling command processed")
                pendingResult?.finish()
            }
        }
    }

    companion object {
        const val ACTION_NIMBUS_TOOLING = "org.bluepowerrobotics.bpfox.NIMBUS_TOOLING"
    }
}
