package io.usys.report.ui.ysr.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import io.usys.report.R
import io.usys.report.realm.model.Chat
import io.usys.report.realm.model.ChatAdapter

class ChatDialogFragment : DialogFragment() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatMessages: MutableList<Chat>

    private lateinit var database: DatabaseReference
    private lateinit var messageListener: ChildEventListener

    private lateinit var sender: String

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_chat, container, false)

        chatMessages = mutableListOf()
        chatAdapter = ChatAdapter(chatMessages)

        messageEditText = view.findViewById(R.id.chat_edit_text)
        sendButton = view.findViewById(R.id.chat_send_button)
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view)
        chatRecyclerView.adapter = chatAdapter

        database = FirebaseDatabase.getInstance().reference.child("chat")

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                val chatMessage = Chat().apply {
                    this.messageText = message
                    this.senderId = sender
                    this.senderName = sender
                    this.timestamp = System.currentTimeMillis()
                }
                database.push().setValue(chatMessage)
                messageEditText.text.clear()
            }
        }

        messageListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(Chat::class.java)
                chatMessage?.let {
                    chatAdapter.addMessage(it)
                    chatRecyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
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

        return view
    }

    override fun onStart() {
        super.onStart()
        sender = arguments?.getString("sender") ?: "Anonymous"
        database.addChildEventListener(messageListener)
    }

    override fun onStop() {
        super.onStop()
        database.removeEventListener(messageListener)
    }

    companion object {
        fun newInstance(sender: String): ChatDialogFragment {
            val args = Bundle()
            args.putString("sender", sender)
            val fragment = ChatDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
