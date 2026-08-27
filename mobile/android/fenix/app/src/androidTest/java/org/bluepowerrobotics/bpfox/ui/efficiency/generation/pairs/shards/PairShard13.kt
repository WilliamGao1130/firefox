/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.bluepowerrobotics.bpfox.ui.efficiency.generation.pairs.shards

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.bluepowerrobotics.bpfox.ui.efficiency.generation.pairs.BasePairShardTest
import org.bluepowerrobotics.bpfox.ui.efficiency.generation.pairs.PairCase
import org.bluepowerrobotics.bpfox.ui.efficiency.generation.pairs.PairShardData

@RunWith(Parameterized::class)
class PairShard13(private val case: PairCase) : BasePairShardTest(case) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<Array<Any>> =
            PairShardData.loadShard(
                shardIndex = 13,
                shardCount = 20,
            )
    }

    @Test
    fun verifyNavigationPairReachability() {
        runNavigationPairCase()
    }
}
