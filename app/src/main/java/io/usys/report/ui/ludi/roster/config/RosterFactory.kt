package io.usys.report.ui.ludi.roster.config

import io.realm.Realm
import io.usys.report.realm.findRosterIdByTeamId
import io.usys.report.realm.findTryOutByTeamId
import io.usys.report.utils.ludi.RosterHolderConfig
import io.usys.report.utils.ludi.RosterID


/**
 * Custom Roster Setups
 *      Pair<RosterHolder, RosterConfig>
 *          <RosterId, RosterId Configuration>
 * 1. The Roster Id that the Configuration Applies to.
 * 2. The specific configuration for that Roster Id.
 *      - Layout Configurations
 *      - Size
 *      - Name
 *      - Team
 *      -
 */

fun Realm.rosterViewFactory(teamId:String) : RosterHolderConfig {
    val rosterConfig = RosterConfig(teamId)
    val rosterHolder = RosterHolder()
    // Official Roster
    this.findRosterIdByTeamId(teamId)?.let { rosterId ->
        // official roster
        rosterHolder[RosterType.OFFICIAL.type] = rosterId
    }
    // TryOut Roster
    this.findTryOutByTeamId(teamId) { to ->
        to.rosterId?.let {
            // tryout roster
            rosterHolder[RosterType.TRYOUT.type] = it
            if (to.splits > 1) {
                for (i in 1..to.splits) {
                    rosterHolder[RosterType.SELECTED.type + i] = it
                }
            } else {
                rosterHolder[RosterType.SELECTED.type] = it
            }
        }
    }

    rosterConfig.apply {
        this.rosterId = currentRosterId
    }
    return RosterHolderConfig(rosterHolder, rosterConfig)
}


/** Custom Roster HashMap
 * <RosterType, RosterId>
 */
class RosterHolder : MutableMap<String, RosterID> {
    private val rosterMap: MutableMap<String, RosterID> = LinkedHashMap()

    override val entries: MutableSet<MutableMap.MutableEntry<String, RosterID>>
        get() = rosterMap.entries
    override val keys: MutableSet<String>
        get() = rosterMap.keys
    override val size: Int
        get() = rosterMap.size
    override val values: MutableCollection<String>
        get() = rosterMap.values

    override fun clear() {
        rosterMap.clear()
    }

    override fun isEmpty(): Boolean {
        return rosterMap.isEmpty()
    }

    override fun remove(key: String): String? {
        return rosterMap.remove(key)
    }

    override fun putAll(from: Map<out String, String>) {
        rosterMap.putAll(from)
    }

    override fun put(key: String, value: String): String? {
        return rosterMap.put(key, value)
    }

    override fun get(key: String): String? {
        return rosterMap[key]
    }

    override fun containsValue(value: String): Boolean {
        return rosterMap.containsValue(value)
    }

    override fun containsKey(key: String): Boolean {
        return rosterMap.containsKey(key)
    }
}

