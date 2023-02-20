package io.usys.report.realm.model

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.R
import io.usys.report.firebase.DatabasePaths
import io.usys.report.realm.executeRealm
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.utils.newUUID
import java.io.Serializable
import java.text.DateFormat

open class Chat: RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var senderId: String? = null
    var senderName: String? = null
    var chatId: String? = null
    var receiverId: String? = null
    var messageText: String? = null
    var timestamp: Long? = null

    // create function to send message to firebase
    fun sendMessageToChat(message: String, senderId: String, senderName: String, chatId: String, timestamp: Long) {
        // create a new chat object
        executeRealm {
            this.apply {
                this.messageText = message
                this.senderId = senderId
                this.senderName = senderName
                this.chatId = chatId
                this.timestamp = timestamp
            }
        }
        // send message to firebase
        // ...
    }

}

fun getFireDBChat(): DatabaseReference {
    return FirebaseDatabase.getInstance().reference.child(DatabasePaths.CHAT.path)
}


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {


    var chatMessages: MutableList<Chat> = mutableListOf()
    var userId: String? = null

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val messageSender: TextView = itemView.findViewById(R.id.message_sender)
        val messageTime: TextView = itemView.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        safeUserId { itUserId ->
            userId = itUserId
        }
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = chatMessages[position]

        if (chatMessage.senderId == userId) {
            holder.messageContainer.gravity = Gravity.END
            holder.messageText.text = chatMessage.messageText
            holder.messageSender.text = chatMessage.senderName
            holder.messageTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(chatMessage.timestamp)
        } else {
            holder.messageContainer.gravity = Gravity.START
            holder.messageText.text = chatMessage.messageText
            holder.messageSender.text = chatMessage.senderName
            holder.messageTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(chatMessage.timestamp)
        }


    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    fun addMessage(chatMessage: Chat) {
        chatMessages.add(chatMessage)
        notifyItemInserted(chatMessages.size - 1)
    }

}


class ChatFireBaseDatabaseListener(val adapter: ChatAdapter, val recyclerView: RecyclerView) : ChildEventListener {
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val chatMessage = snapshot.getValue(Chat::class.java)
        chatMessage?.let {
            adapter.addMessage(it)
            recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        // not used in this example
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        // not used in this example
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        // not used in this example
    }

    override fun onCancelled(error: DatabaseError) {
        // not used in this example
    }
}
