package io.usys.report.utils.ludi

import androidx.lifecycle.LiveData
import io.usys.report.realm.model.PlayerRef
import io.usys.report.ui.ludi.roster.config.RosterConfig
import io.usys.report.ui.ludi.roster.config.RosterHolder

/** Realm IDs **/
typealias LudiObjectID = String
typealias TeamID = String
typealias RosterID = String
typealias PlayerID = String
typealias TryOutID = String
typealias TryOutRosterID = String

/** Roster **/
typealias RosterTypeName = String
typealias RosterIDs = MutableMap<RosterTypeName, RosterID>
typealias RosterHolderConfig = Pair<RosterHolder, RosterConfig>

/** LUDI FILTERS **/
typealias LudiAttribute = String
typealias LudiValue = String
typealias LudiFilter = Pair<LudiAttribute, LudiValue>
typealias LudiFilters = MutableMap<LudiAttribute, LudiValue>

/** PlayerRef **/
typealias LivePlayers = LiveData<List<PlayerRef>>