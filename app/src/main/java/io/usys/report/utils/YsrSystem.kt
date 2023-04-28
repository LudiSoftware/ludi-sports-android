package io.usys.report.utils

import android.content.Context
import android.widget.Toast


fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

class YsrMode {
    companion object {
        val VIEW_ONLY = "viewonly"
        val BASIC = "basic"
        val TRYOUTS = "tryouts"
        val SEASON = "season"
    }
}

open class AuthTypes {
    companion object {
        // Auth Types
        var MASTER = "master"
        var ADMIN = "admin"
        var ORG_ADMIN_USER = "org_admin"
        var COACH_USER = "coach"
        var PLAYER_USER = "player"
        var PARENT_USER = "parent"
        var BASIC_USER = "basic" // Default
        var WAITING = "waiting"

        var UNASSIGNED = "unassigned"
    }
}