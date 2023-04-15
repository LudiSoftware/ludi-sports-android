package io.usys.report.providers

/** Team Status */
enum class TeamStatus(val status: String) {
    IN_SEASON("open"),
    POST_SEASON("pending"),
    TRYOUT("finalized"),
    PENDING("archive"),
    ARCHIVE("archive"),
    DEAD("dead")
}

/** Tryout Status */

/** Roster Status */
const val ROSTER_STATUS_OPEN = "open"
const val ROSTER_STATUS_CLOSED = "closed"
const val ROSTER_STATUS_READY_FOR_SUBMISSION = "ready for submission"
const val ROSTER_STATUS_SUBMITTED = "submitted"
const val ROSTER_STATUS_ACCEPTED = "accepted"
const val ROSTER_STATUS_REJECTED = "rejected"
enum class RosterStatus(val status: String) {
    OPEN("open"),
    PENDING("pending"),
    FINALIZED("finalized"),
    ARCHIVE("archive"),
    DEAD("dead")
}

/** Player Status */
enum class PlayerStatus(val status: String) {
    OPEN("open"),
    PENDING("pending"),
    SELECTED("finalized"),
    SEND_LETTER("archive"),
    PENDING_APPROVAL("dead"),
    APPROVED("approved"),
    REJECTED("rejected"),
}