package io.usys.report.realm.model

import android.app.Activity
import androidx.core.app.ActivityCompat
import io.usys.report.ui.AuthControllerActivity
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.firebase.coreFireLogoutAsync
import io.usys.report.realm.model.users.Coach
import io.usys.report.realm.model.users.Player
import io.usys.report.realm.writeToRealm
import io.usys.report.realm.model.users.User
import io.usys.report.realm.realm
import io.usys.report.realm.session
import io.usys.report.realm.sessionSports
import io.usys.report.ui.ludi.sport.containsItem
import io.usys.report.utils.*
import io.usys.report.utils.androidx.launchActivity
import kotlin.collections.isNullOrEmpty

/**
 * Created by ChazzCoin : October 2022.
 */
open class Session : RealmObject() {
    //DO NOT MAKE STATIC!
    @PrimaryKey
    var id: String = "1"

    //DO NOT MAKE STATIC
    var user: User? = null
    var coach: Coach? = null
    var player: Player? = null
    var parent: Parent? = null
    var sports: RealmList<Sport>? = RealmList()
    var services: RealmList<Service>? = RealmList()
    var organizations: RealmList<Organization>? = RealmList()
    var teams: RealmList<Team>? = RealmList()
    var coaches: RealmList<Coach>? = RealmList()
    var players: RealmList<Player>? = RealmList()
    var parents: RealmList<Parent>? = RealmList()
    var leagues: RealmList<League>? = RealmList()
    var locations: RealmList<Location>? = RealmList()

    /** -> EVERYTHING IS STATIC BELOW THIS POINT <- **/
    companion object {
        /** -> Controller Methods <- >  */
        private const val sessionId = "1"

        //GET CURRENT SESSION
        var session: Session? = null
            get() {
                try {
                    writeToRealm {
                        field = realm().where(Session::class.java).equalTo("id", sessionId).findFirst()
                        if (field == null) {
                            field = Session()
                            field?.id = sessionId
                        }
                    }
                    return field
                } catch (e: Exception) { e.printStackTrace() }
                return field
            }
            private set

        fun isSessionAlreadyCreated(sessionId: String): Boolean {
            tryCatch {
                val existingSession =
                    realm().where(Session::class.java).equalTo("id", sessionId).findFirst()
                return existingSession != null
            }
            return false
        }

        //Must Have.
        fun createObjects() {
            writeToRealm { itRealm ->
                if (!isSessionAlreadyCreated("1")) {
                    itRealm.createObject(Session::class.java, sessionId)
                }
                itRealm.createObject(Sport::class.java, newUUID())
                itRealm.createObject(Organization::class.java, newUUID())
                itRealm.createObject(Review::class.java, newUUID())
                itRealm.createObject(Service::class.java, newUUID())
                itRealm.createObject(League::class.java, newUUID())
                itRealm.createObject(Location::class.java, newUUID())
                itRealm.createObject(Team::class.java, newUUID())
            }
        }

        //CORE -> LOG CURRENT USER OUT
        fun deleteAllRealmObjects() {
            writeToRealm {
                it.where(Session::class.java).findAll().deleteAllFromRealm()
                it.where(User::class.java).findAll().deleteAllFromRealm()
                it.where(Organization::class.java).findAll().deleteAllFromRealm()
                it.where(Sport::class.java).findAll().deleteAllFromRealm()
                it.where(Review::class.java).findAll().deleteAllFromRealm()
                it.where(Service::class.java).findAll().deleteAllFromRealm()
                it.where(Team::class.java).findAll().deleteAllFromRealm()
                it.where(PlayerRef::class.java).findAll().deleteAllFromRealm()
                it.where(Roster::class.java).findAll().deleteAllFromRealm()
                it.where(TryOut::class.java).findAll().deleteAllFromRealm()
                it.where(Coach::class.java).findAll().deleteAllFromRealm()
                it.deleteAll()
            }
        }

        //CORE -> SYSTEM RESTART THE APP
        fun logoutAndRestartApplication(context: Activity) {
            deleteAllRealmObjects()
            ActivityCompat.finishAffinity(context)
            coreFireLogoutAsync(context).addOnCompleteListener {
                context.launchActivity<AuthControllerActivity>()
            }
        }

        /** -> OBJECT MODEL HELPERS <- **/

        fun addSports(sports: RealmList<Sport>?) {
            sports?.let { itSports ->
                itSports.forEach {
                    addSport(sport = it)
                }
            }
        }

        //ADD SPORT, this should be safe
        fun addSport(sport: Sport?) {

            sessionSports { itSports ->
                if (itSports.containsItem(sport)) return
                writeToRealm {
                    itSports.add(sport)
                }
            }

            session { itSession ->
                writeToRealm {
                    if (itSession.sports.isNullOrEmpty()) {
                        itSession.sports = RealmList()
                        itSession.sports?.add(sport)
                    } else {
                        itSession.sports?.let {
                            var containsSport = false
                            for (item in it) {
                                if (item.toString() == sport?.id) {
                                    containsSport = true
                                }
                            }
                            if (!containsSport) it.add(sport)
                        }
                    }
                }
            }
        }

        fun addServices(services: RealmList<Service>) {
            session { itSession ->
                for (item in services) {
                    writeToRealm {
                        itSession.services?.add(item)
                    }
                }
            }
        }

    }
}


inline fun <reified T> addObjectToSessionList(objectToAdd: T) {
    val objectClassName = objectToAdd.getClassName()
    writeToRealm { itRealm ->
        session { itSession ->
            when (objectClassName) {
                "Sport" -> {
                    itSession.sports?.let { itSports ->
                        if (!itSports.contains(objectToAdd as Sport)) {
                            itSports.add(objectToAdd)
                        }
                    }
                }
                "Service" -> {
                    itSession.services?.let { itServices ->
                        if (!itServices.contains(objectToAdd as Service)) {
                            itServices.add(objectToAdd)
                        }
                    }
                }
                "Organization" -> {
                    itSession.organizations?.let { itOrgs ->
                        if (!itOrgs.contains(objectToAdd as Organization)) {
                            itOrgs.add(objectToAdd)
                        }
                    }
                }
                "Team" -> {
                    itSession.teams?.let { itTeams ->
                        if (!itTeams.contains(objectToAdd as Team)) {
                            itTeams.add(objectToAdd)
                        }
                    }
                }
                "Coach" -> {
                    itSession.coaches?.let { itCoaches ->
                        if (!itCoaches.contains(objectToAdd as Coach)) {
                            itCoaches.add(objectToAdd)
                        }
                    }
                }
                "Player" -> {
                    itSession.players?.let { itPlayers ->
                        if (!itPlayers.contains(objectToAdd as Player)) {
                            itPlayers.add(objectToAdd)
                        }
                    }

                }
                "Parent" -> {
                    itSession.parents?.let { itParents ->
                        if (!itParents.contains(objectToAdd as Parent)) {
                            itParents.add(objectToAdd)
                        }
                    }

                }
                "League" -> {
                    itSession.leagues?.let { itLeagues ->
                        if (!itLeagues.contains(objectToAdd as League)) {
                            itLeagues.add(objectToAdd)
                        }
                    }
                }
                "Location" -> {
                    itSession.locations?.let { itLocations ->
                        if (!itLocations.contains(objectToAdd as Location)) {
                            itLocations.add(objectToAdd)
                        }
                    }

                }
                else -> log("No List Found for RealmObject")
            }
            itRealm.insertOrUpdate(itSession)
        }
    }
}

inline fun <reified T> T.getClassName(): String {
    return T::class.java.simpleName
}

inline fun <reified T> RealmList<T>?.addToSession() {
    this?.first()?.let {
        when (it) {
            is Sport -> this.cast<RealmList<Sport>>()?.let { it1 -> Session.addSports(it1) }
            is Service -> this.cast<RealmList<Service>>()?.let { it1 -> Session.addServices(it1) }
            else -> { null }
        }
    }
}

