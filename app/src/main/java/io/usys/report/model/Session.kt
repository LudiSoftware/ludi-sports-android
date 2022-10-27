package io.usys.report.model

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.core.app.ActivityCompat
import io.usys.report.AuthController
import io.usys.report.utils.executeRealm
import io.usys.report.utils.session
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by ChazzCoin : October 2022.
 */
open class Session : RealmObject() {
    //DO NOT MAKE STATIC!
    @PrimaryKey
    var sessionId = 0

    //DO NOT MAKE STATIC
    var sports: RealmList<Sport>? = RealmList()
    var organizations: RealmList<Organization>? = RealmList()

    /** -> EVERYTHING IS STATIC BELOW THIS POINT <- **/
    companion object {
        //UserController
        private const val WAITING = "waiting"

        /** -> Controller Methods <- >  */
        private const val aisession = 1
        var USER_UID = ""

        //Class Variables
        private var mRealm = Realm.getDefaultInstance()

        //GET CURRENT SESSION
        var session: Session? = null
            get() {
                try {
                    if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
                    field = mRealm.where(Session::class.java).equalTo("sessionId", aisession).findFirst()
                    if (field == null) {
                        field = Session()
                        field?.sessionId = aisession
                    }
                } catch (e: Exception) { e.printStackTrace() }
                return field
            }
            private set

        //GET CURRENT USER
        var user: User? = null
            get() {
                try {
                    if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
                    field = mRealm.where(User::class.java).findFirst()
                    if (field == null) { field = User() }
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

        //CORE ->
        fun createObjects() {
            createUser()
            executeRealm { itRealm ->
                itRealm.createObject(Sport::class.java)
                itRealm.createObject(Organization::class.java)
                itRealm.createObject(Review::class.java)
            }

        }

        //CORE ->
        fun createUser() {
            val realm = Realm.getDefaultInstance()
            if (realm.where(User::class.java) == null){
                realm.executeTransaction { itRealm ->
                    itRealm.createObject(User::class.java)
                }
            }
        }

        //CORE ->
        fun deleteUser() {
            executeRealm { it.delete(User::class.java) }
        }

        //CORE ->
        fun deleteAll() {
            executeRealm { it.deleteAll() }
        }

        fun updateUser(newNser: User){
            val curUser = user
            executeRealm { itRealm ->
                curUser?.id = newNser.id
                curUser?.auth = newNser.auth
                curUser?.name = newNser.name
                curUser?.email = newNser.email
                curUser?.phone = newNser.phone
                itRealm.insertOrUpdate(curUser)
            }
        }

        //CORE -> CHECK IS USER IS LOGGED IN
        val isLogged: Boolean
            get() {
                if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
                val session = session
                if (session != null && session.isValid) {
                    val user = Session.user
                    if (user != null && user.isValid) {
                        return true
                    }
                }
                return false
            }

        //CORE -> LOG CURRENT USER OUT
        fun logOut() {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            mRealm.executeTransaction {
                mRealm.where(Session::class.java).findAll().deleteAllFromRealm()
                mRealm.where(User::class.java).findAll().deleteAllFromRealm()
                mRealm.where(Organization::class.java).findAll().deleteAllFromRealm()
                mRealm.where(Sport::class.java).findAll().deleteAllFromRealm()
                mRealm.where(Review::class.java).findAll().deleteAllFromRealm()
            }
        }

        //CORE -> SYSTEM RESTART THE APP
        fun restartApplication(context: Activity) {
            logOut()
            ActivityCompat.finishAffinity(context)
            context.startActivity(Intent(context, AuthController::class.java))
        }

        /** -> OBJECT MODEL HELPERS <- **/

        //ADD SPORT, this should be safe
        fun addSport(sport: Sport?) {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            val session = session
            session?.let { itSession ->
                mRealm.beginTransaction()
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
                mRealm.copyToRealmOrUpdate(session) //safe?
                mRealm.commitTransaction()
            }
        }

        //REMOVE ALL SPORT
        fun removeAllSports() {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            val session = session
            session?.let { itSession ->
                mRealm.beginTransaction()
                itSession.sports?.clear()
                mRealm.where(Sport::class.java).findAll().deleteAllFromRealm()
                mRealm.copyToRealmOrUpdate(session) //safe?
                mRealm.commitTransaction()
            }
        }

        //ADD ORGANIZATION, this should be safe
        fun addOrganization(organization: Organization?) {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            val session = session
            session?.let { itSession ->
                mRealm.beginTransaction()
                itSession.organizations?.add(organization)
                mRealm.copyToRealmOrUpdate(session) //safe?
                mRealm.commitTransaction()
            }
        }

        //REMOVE ORGANIZATION
        fun removeOrganization(organization: Organization?) {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            val session = session
            session?.let { itSession ->
                mRealm.beginTransaction()
                itSession.organizations?.remove(organization)
                mRealm.copyToRealmOrUpdate(session) //safe?
                mRealm.commitTransaction()
            }
        }

        //REMOVE ALL ORGANIZATION
        fun removeAllOrganization() {
            if (mRealm == null) { mRealm = Realm.getDefaultInstance() }
            val session = session
            session?.let { itSession ->
                mRealm.beginTransaction()
                itSession.organizations?.clear()
                mRealm.where(Organization::class.java).findAll().deleteAllFromRealm()
                mRealm.copyToRealmOrUpdate(session) //safe?
                mRealm.commitTransaction()
            }
        }


        /** -> SPOTS <- **/

        fun createNewSpot(spot: Spot){
            mRealm?.let {
                it.beginTransaction()
                it.insert(spot)
                it.commitTransaction()
            }
        }

    }
}


