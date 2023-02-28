package io.usys.report.ui.ludi.chat

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import io.realm.Realm
import io.usys.report.R
import io.usys.report.realm.linearLayoutManager
import io.usys.report.realm.model.Chat
import io.usys.report.realm.model.ChatAdapter
import io.usys.report.realm.model.ChatFireBaseDatabaseListener
import io.usys.report.realm.model.getFireDBChat
import io.usys.report.realm.model.users.safeUser
import io.usys.report.realm.realm
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

    var realmInstance: Realm? = null
    var chatId: String? = null
    private var userId: String? = null
    private var userName: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogFragment = AlertDialog.Builder(requireContext())
        rootview = this.layoutInflater.inflate(R.layout.dialog_chat, null)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        realmInstance = realm()
        arguments?.let {
            chatId = it.getString("chatId")
        }

        realmInstance?.safeUser { user ->
            userId = user.id
            userName = user.name ?: "Anonymous"
        }
        if (chatId == null || userId.isNullOrEmpty()) {
            this.dismiss()
        }
        database = getFireDBChat().child(chatId!!)

        chatMessages = mutableListOf()
        chatAdapter = ChatAdapter()

        messageEditText = rootview?.bind(R.id.chat_edit_text)!!
        sendButton = rootview?.bind(R.id.chat_send_button)!!
        chatRecyclerView = rootview?.bind(R.id.chat_recycler_view)!!
        chatRecyclerView.layoutManager = linearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            sendMessage()
        }

        dialogFragment?.setView(rootview)?.setMessage("Team Chat")
        return dialogFragment!!.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_chat, container, false)
        messageListener = ChatFireBaseDatabaseListener(chatAdapter, chatRecyclerView)
        return view
    }

    private fun sendMessage() {
        chatId?.let {
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
            //Add Toast for empty message
            Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "ChatId is null", Toast.LENGTH_SHORT).show()
            this.dismiss()
        }
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
        /** How to use:
         * val chatDialogFragment = ChatDialogFragment.newInstance(team!!.id)
         * chatDialogFragment.show(childFragmentManager, "chat_dialog")
         */
        fun newChatInstance(chatId: String): ChatDialogFragment {
            val args = Bundle()
            args.putString("chatId", chatId)
            val fragment = ChatDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
