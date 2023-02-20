package io.usys.report.realm.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.R
import io.usys.report.realm.executeRealm
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
                this.senderName = senderId
                this.chatId = chatId
                this.timestamp = timestamp
            }
        }
        // send message to firebase
        // ...
    }

}


class ChatAdapter(private val chatMessages: MutableList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val messageSender: TextView = itemView.findViewById(R.id.message_sender)
        val messageTime: TextView = itemView.findViewById(R.id.message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        holder.messageText.text = chatMessage.messageText
        holder.messageSender.text = chatMessage.senderName
        holder.messageTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(chatMessage.timestamp)
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    fun addMessage(chatMessage: Chat) {
        chatMessages.add(chatMessage)
        notifyItemInserted(chatMessages.size - 1)
    }

}
