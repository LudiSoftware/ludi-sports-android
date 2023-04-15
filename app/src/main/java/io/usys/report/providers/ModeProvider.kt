package io.usys.report.providers

/** Team Modes */
enum class TeamMode(val mode: String) {
    CREATION("creation"),
    PRE_SEASON("pre_season"),
    IN_SEASON("in_season"),
    OFF_SEASON("off_season"),
    TRYOUT("tryout"),
    PENDING_ROSTER("pending_roster")
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