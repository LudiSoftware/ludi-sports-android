package io.usys.report.providers

import io.usys.report.R

/** Team Modes */
enum class TeamMode(val mode: String, val color: Int, val title: String) {
    CREATION("creation", R.color.ludiRosterCardSelected, "Team Creation"),
    PRE_SEASON("pre_season", R.color.ludiRosterCardSelected, "Pre-Season"),
    IN_SEASON("in_season", R.color.ludiRosterCardSelected, "In-Season"),
    OFF_SEASON("off_season", R.color.ludiWhite, "Off-Season"),
    TRYOUT("tryout", R.color.ludiRosterCardRed, "Tryout"),
    PENDING_ROSTER("pending_roster", R.color.ludiRosterCardYellow, "Pending Roster");

    companion object {
        fun parse(mode: String?): TeamMode {
            return values().find { it.mode == mode } ?: CREATION
        }
    }
}

/** Tryout Modes */
enum class TryoutMode(val mode: String) {
    REGISTRATION("registration"),
    TRYOUT("tryout"),
    PENDING_ROSTER("pending_roster"),
    COMPLETE("complete")
}

/** Roster Modes */
enum class RosterMode(val mode: String) {
    REGISTRATION("registration"),
    TRYOUT("tryout"),
    PENDING_ROSTER("pending_roster"),
    COMPLETE("complete")
}

/** Player Modes */
enum class PlayerMode(val mode: String) {
    PENDING_REGISTRATION("pending_registration"),
    REGISTERED("registered"),
    PENDING_APPROVAL("pending_approval"),
    APPROVED("approved"),
    REJECTED("rejected")
}