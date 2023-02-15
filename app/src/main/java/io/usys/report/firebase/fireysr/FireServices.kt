package io.usys.report.firebase

import com.google.firebase.database.DataSnapshot

/**
 * SERVICES
 */
inline fun fireGetAllServicesAsync(crossinline block: DataSnapshot?.() -> Unit) {
    firebaseDatabase {
        it.child(FireTypes.SERVICES)
            .fairAddListenerForSingleValueEvent { ds ->
                block(ds)
            }
    }
}
