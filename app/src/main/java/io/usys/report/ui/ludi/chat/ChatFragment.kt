package io.usys.report.ui.ludi.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.DialogChatBinding
import io.usys.report.realm.linearLayoutManager
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.fragments.*
import io.usys.report.utils.YsrMode
import io.usys.report.utils.log

/**
 * Created by ChazzCoin : October 2022.
 */

class ChatFragment : LudiTeamFragment() {

    companion object {
        const val TAB = "Chat"
    }

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: DialogChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatMessages: MutableList<Chat>

    private lateinit var database: DatabaseReference
    private lateinit var messageListener: ChildEventListener

    private lateinit var sender: String

    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView

    var chatId: String? = null
    private var userId: String? = null
    private var userName: String = ""

    override fun setupOnClickListeners() {
        TODO("Not yet implemented")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = DialogChatBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root
        //Basic Setup
        chatId = teamId

        realmInstance?.safeUser { user ->
            userId = user.id
            userName = user.name ?: "Anonymous"
            sender = user.name ?: "Anonymous"
        }
        chatId?.let {
            database = getFireDBChat().child(chatId!!)
        } ?: run {
            Toast.makeText(context, "ChatId is null", Toast.LENGTH_SHORT).show()
        }

        chatMessages = mutableListOf()
        chatAdapter = ChatAdapter()


        messageEditText = _binding?.chatEditText!!
        sendButton = _binding?.chatSendButton!!
        chatRecyclerView =_binding?.chatRecyclerView!!
        chatRecyclerView.layoutManager = linearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter
        messageListener = ChatFireBaseDatabaseListener(chatAdapter, chatRecyclerView)
        database.addChildEventListener(messageListener)
        sendButton.setOnClickListener {
            sendMessage()
        }

        return rootView
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
//            Toast.makeText(context, "Please enter a message", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(context, "ChatId is null", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        realmInstance?.safeUser { user ->
            userId = user.id
            userName = user.name ?: "Anonymous"
            sender = user.name ?: "Anonymous"
        }
//        database.addChildEventListener(messageListener)
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
        database.removeEventListener(messageListener)
    }

}
