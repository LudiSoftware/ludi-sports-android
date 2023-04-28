package io.usys.report.realm.model

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.usys.report.R
import io.usys.report.firebase.DatabasePaths
import io.usys.report.providers.notifications.ludiChatNotify
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.writeToRealm
import io.usys.report.realm.model.users.safeUserId
import io.usys.report.realm.realm
import io.usys.report.utils.makeGone
import io.usys.report.utils.makeVisible
import io.usys.report.utils.newUUID
import io.usys.report.utils.views.loadUriIntoImgView
import org.jetbrains.anko.image
import java.io.Serializable
import java.text.DateFormat

open class Chat: RealmObject(), Serializable {
    @PrimaryKey
    var id: String = newUUID()
    var senderId: String? = null
    var senderName: String? = null
    var senderImage: String? = null
    var chatId: String? = null
    var receiverId: String? = null
    var messageText: String? = null
    var timestamp: Long? = null

}

fun getFireDBChat(): DatabaseReference {
    return FirebaseDatabase.getInstance().reference.child(DatabasePaths.CHAT.path)
}


class ChatAdapter(val context: Context?=null) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val realmInstance = realm()
    private var chatMessages: MutableList<Chat> = mutableListOf()
    var userId: String? = null

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageContainer: LinearLayout = itemView.findViewById(R.id.message_container)
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val messageSender: TextView = itemView.findViewById(R.id.message_sender)
        val messageTime: TextView = itemView.findViewById(R.id.message_time)
        val messageUserImage: CircleImageView = itemView.findViewById(R.id.chat_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        realmInstance.safeUserId { itUserId ->
            userId = itUserId
        }
        return ChatViewHolder(view)
    }


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = chatMessages[position]

        if (chatMessage.senderId == userId) {
            holder.messageContainer.layoutParams = (holder.messageContainer.layoutParams as ConstraintLayout.LayoutParams).apply {
                startToEnd = R.id.chat_image
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                leftToRight = ConstraintLayout.LayoutParams.UNSET
                leftToLeft = ConstraintLayout.LayoutParams.UNSET
                startToStart = ConstraintLayout.LayoutParams.UNSET
                endToStart = ConstraintLayout.LayoutParams.UNSET
                marginEnd = 0
                leftMargin = 0
            }
            holder.messageContainer.setBackgroundResource(R.drawable.ft_border_rounded_creme)
            holder.messageText.text = chatMessage.messageText
            holder.messageUserImage.makeGone()
            holder.messageSender.makeGone()
            holder.messageTime.makeGone()
        } else {
            holder.messageContainer.layoutParams = (holder.messageContainer.layoutParams as ConstraintLayout.LayoutParams).apply {
                startToEnd = R.id.chat_image
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                bottomToTop = R.id.message_time
                topToBottom = R.id.message_sender
                startToStart = ConstraintLayout.LayoutParams.UNSET
                endToStart = ConstraintLayout.LayoutParams.UNSET
                width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_SPREAD
            }
            holder.messageContainer.setBackgroundResource(R.drawable.ft_border_rounded_creme)
            holder.messageUserImage.makeVisible()
            holder.messageUserImage.loadUriIntoImgView(chatMessage.senderImage ?: "")

            holder.messageSender.makeVisible()
            holder.messageTime.makeVisible()
            holder.messageText.makeVisible()
            holder.messageContainer.makeVisible()
            holder.messageText.text = chatMessage.messageText
            holder.messageSender.text = chatMessage.senderName
            holder.messageTime.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(chatMessage.timestamp)
        }


    }


    override fun getItemCount(): Int {
        return chatMessages.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addMessage(chatMessage: Chat) {
        if (chatMessages.contains(chatMessage)) {
            return
        }
        chatMessages.add(chatMessage)
        notifyItemInserted(chatMessages.size - 1)
        this.notifyDataSetChanged()
        showNotification(chatMessage.messageText ?: "Oops! Something went wrong.")
    }

    private fun showNotification(message: String) {
        // show notification
        ludiChatNotify(context, message)
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
