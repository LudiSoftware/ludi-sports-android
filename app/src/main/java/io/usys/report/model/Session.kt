package io.usys.report.model

import android.app.Activity
import android.util.Log
import androidx.core.app.ActivityCompat
import io.usys.report.ui.AuthControllerActivity
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.usys.report.R
import io.usys.report.firebase.FireTypes
import io.usys.report.firebase.coreFireLogoutAsync
import io.usys.report.ui.ysr.sport.containsItem
import io.usys.report.utils.*
import java.util.*
import kotlin.collections.isNullOrEmpty

/**
 * Created by ChazzCoin : October 2022.
 */
open class Session : RealmObject() {
    //DO NOT MAKE STATIC!
    @PrimaryKey
    var id: Int = 1

    //DO NOT MAKE STATIC
    var sports: RealmList<Sport>? = RealmList()
    var services: RealmList<Service>? = RealmList()
    var organizations: RealmList<Organization>? = RealmList()

    /** -> EVERYTHING IS STATIC BELOW THIS POINT <- **/
    companion object {
        //UserController
        private const val WAITING = "waiting"

        /** -> Controller Methods <- >  */
        private const val sessionId = 1
        var user: User? = null
        var USER_ID = ""

        //Class Variables
        private var mRealm = Realm.getDefaultInstance()

        //GET CURRENT SESSION
        var session: Session? = null
            get() {
                try {
                    if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
                    field = mRealm.where(Session::class.java).equalTo("id", sessionId).findFirst()
                    if (field == null) {
                        field = Session()
                        field?.id = sessionId

                    }
                } catch (e: Exception) { e.printStackTrace() }
                return field
            }
            private set

        //CORE ->
        fun updateSession(user: User?): Session? {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            //Guest guest = SessionsController.getSession().getGuest();
            val session = session
            Log.e("loggedUser", "_wait__")
            session { itSession ->
                executeRealm { it.insertOrUpdate(itSession) }
            }
            return session
        }

        fun getCurrentUser() : User? {
            var usr: User? = null
            try {
                if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
                usr = mRealm.where(User::class.java).findFirst()
                if (usr == null) { usr = User() }
            } catch (e: Exception) { e.printStackTrace() }
            return usr
        }


        //CORE ->
        fun createObjects() {
            createUser()
            executeRealm { itRealm ->
                itRealm.createObject(Session::class.java, sessionId)
                itRealm.createObject(Sport::class.java, newUUID())
                itRealm.createObject(Organization::class.java, newUUID())
                itRealm.createObject(Review::class.java, newUUID())
                itRealm.createObject(Service::class.java, newUUID())
            }

        }
        fun createSession() {
            val realm = Realm.getDefaultInstance()
            if (realm.where(Session::class.java) == null){
                realm.executeTransaction { itRealm ->
                    itRealm.createObject(Session::class.java, sessionId)
                    val temp = Session().apply {
                        this.services = RealmList()
                    }
                    itRealm.insertOrUpdate(temp)
                }
            }
        }
        //CORE ->
        fun createUser() {
            val realm = Realm.getDefaultInstance()
            if (realm.where(User::class.java) == null){
                realm.executeTransaction { itRealm ->
                    itRealm.createObject(User::class.java, newUUID())
                }
            }
        }

        fun updateUser(newNser: User){
            executeRealm { itRealm ->
                user = newNser
                itRealm.insertOrUpdate(newNser)
            }
        }

        //CORE -> LOG CURRENT USER OUT
        private fun deleteAllRealmObjects() {
            executeRealm {
                it.where(Session::class.java).findAll().deleteAllFromRealm()
                it.where(User::class.java).findAll().deleteAllFromRealm()
                it.where(Organization::class.java).findAll().deleteAllFromRealm()
                it.where(Sport::class.java).findAll().deleteAllFromRealm()
                it.where(Review::class.java).findAll().deleteAllFromRealm()
                it.where(Service::class.java).findAll().deleteAllFromRealm()
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
                executeRealm {
                    itSports.add(sport)
                }
            }

            session { itSession ->
                executeRealm {
                    if (itSession.sports.isNullOrEmpty()) {
                        itSession.sports = RealmList()
                        itSession.sports?.add(sport)
                    } else {
                        itSession.sports?.let {
                            var containsSport = false
                            for (item in it) {
                                if (item.name == sport?.name) {
                                    containsSport = true
                                }
                            }
                            if (!containsSport) it.add(sport)
                        }
                    }
                }
            }
        }

        //ADD ORGANIZATION, this should be safe
        fun addOrganization(organization: Organization?) {
            session { itSession ->
                executeRealm {
                    itSession.organizations?.add(organization)
                }
            }
        }
        fun addService(service: Service?) {
            session { itSession ->
                executeRealm {
                    itSession.services?.add(service)
                }
            }
        }
        fun addServices(services: RealmList<Service>) {
            session { itSession ->
                for (item in services) {
                    executeRealm {
                        itSession.services?.add(item)
                    }
                }
            }
        }

    }
}


fun <T> RealmList<T>?.addToSession() {
    this?.first()?.let {
        when (it) {
//            is User -> Session.updateSession(it)
            is Sport -> this.cast<RealmList<Sport>>()?.let { it1 -> Session.addSports(it1) }
//            is Organization -> Session.addOrganization(it)
            is Service -> this.cast<RealmList<Service>>()?.let { it1 -> Session.addServices(it1) }
            else -> { null }
        }
    }
}

