package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.usys.report.realm.model.Sport
import io.usys.report.utils.log

/**
 * SPORTS
 */
fun fireGetAndLoadSportsIntoSessionAsync() {
    firebaseDatabase {
        it.child(FireTypes.SPORTS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.loadIntoSession<Sport>()
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    log("Failed")
                }
            })
    }
}
