package io.usys.report.realm.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.realm.executeRealm
import io.usys.report.utils.newUUID
import java.io.Serializable

open class Chat: RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var senderId: String? = null
    var chatId: String? = null
    var receiverId: String? = null
    var messageText: String? = null
    var timestamp: Long? = null

    // create function to send message to firebase
    fun sendMessageToChat(message: String, senderId: String, chatId: String, timestamp: Long) {
        // create a new chat object
        executeRealm {
            this.apply {
                this.messageText = message
                this.senderId = senderId
                this.chatId = chatId
                this.timestamp = timestamp
            }
        }


        // send message to firebase
        // ...
    }

}
