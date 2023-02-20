package io.usys.report.ui.ysr.chat

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import io.usys.report.R
import io.usys.report.realm.linearLayoutManager
import io.usys.report.realm.loadInRealmList
import io.usys.report.realm.model.Chat
import io.usys.report.realm.model.ChatAdapter
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.ysr.player.fairSetPositiveButton
import io.usys.report.utils.bind

class ChatDialogFragment : DialogFragment() {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatMessages: MutableList<Chat>

    private lateinit var database: DatabaseReference
    private lateinit var messageListener: ChildEventListener

    private lateinit var sender: String

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView

    private var rootview: View? = null
    private var dialogFragment: AlertDialog.Builder? = null

    private var userId: String = ""
    private var userName: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogFragment = AlertDialog.Builder(requireContext())
        rootview = this.layoutInflater.inflate(R.layout.dialog_chat, null)
//        cancelButton = rootview?.bind(R.id.reviewBtnCancel)
//        setOnClickListeners()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        safeUser { user ->
            userId = user.id
            userName = user.name ?: ""
        }
        chatMessages = mutableListOf()
        chatAdapter = ChatAdapter()

        messageEditText = rootview?.bind(R.id.chat_edit_text)!!
        sendButton = rootview?.bind(R.id.chat_send_button)!!
        chatRecyclerView = rootview?.bind(R.id.chat_recycler_view)!!
        chatRecyclerView.layoutManager = linearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter

        database = FirebaseDatabase.getInstance().reference.child("chat")

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                val chatMessage = Chat().apply {
                    this.messageText = message
                    this.senderId = userId
                    this.senderName = userName
                    this.timestamp = System.currentTimeMillis()
                }
                database.push().setValue(chatMessage)
                messageEditText.text.clear()
            }
        }

        dialogFragment?.setView(rootview)
            ?.setMessage("Team Chat")
        return dialogFragment!!.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_chat, container, false)

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
