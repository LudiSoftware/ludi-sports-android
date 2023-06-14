package io.usys.report.ui.ludi.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import io.realm.RealmObject
import io.usys.report.R
import io.usys.report.databinding.LudiChatBinding
import io.usys.report.ui.views.listAdapters.realmList.linearLayoutManager
import io.usys.report.realm.model.*
import io.usys.report.realm.model.users.safeUser
import io.usys.report.ui.fragments.*
import io.usys.report.utils.ludi.toast
import org.jetbrains.anko.support.v4.toast

/**
 * Created by ChazzCoin : October 2022.
 */

class ChatFragment : LudiStringIdsFragment() {

    var onClickReturnViewRealmObject: ((View, RealmObject) -> Unit)? = null
    private var _binding: LudiChatBinding? = null
    private val binding get() = _binding!!
    private var chatAdapter: ChatAdapter? = null
    private var chatMessages: MutableList<Chat>? = null

    private var database: DatabaseReference? = null
    private var messageListener: ChildEventListener? = null

    private var sender: String? = null

    private var messageEditText: EditText? = null
    private var sendButton: Button? = null
    private var chatRecyclerView: RecyclerView? = null

    var chatId: String? = null
    private var userId: String? = null
    private var userName: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val teamContainer = requireActivity().findViewById<ViewGroup>(R.id.ludiViewPager)
        _binding = LudiChatBinding.inflate(inflater, teamContainer, false)
        rootView = binding.root
        //Basic Setup
        chatId = teamId

        realmInstance?.safeUser { user ->
            userId = user.id
            userName = user.name ?: "Anonymous"
            sender = user.name ?: "Anonymous"
        }

        bindViews()
        setupDatabaseReference()
        setupChatAdapter()
        setupMessageListener()
        setupRecyclerView()
        setupUserInputs()
        return rootView
    }

    private fun bindViews() {
        messageEditText = _binding?.chatEditText!!
        sendButton = _binding?.chatSendButton!!
        chatRecyclerView =_binding?.chatRecyclerView!!
    }
    private fun setupDatabaseReference() {
        chatId?.let { database = getFireDBChat().child(it) }
            ?: run { requireContext().toast("ChatId is null") }
    }
    private fun setupChatAdapter() {
        chatMessages = mutableListOf()
        chatAdapter = ChatAdapter(requireContext())
    }
    private fun setupMessageListener() {
        messageListener = chatAdapter?.let { ChatFireBaseDatabaseListener(it, chatRecyclerView!!) }
        messageListener?.let { database?.addChildEventListener(it) }
    }
    private fun setupRecyclerView() {
        chatRecyclerView?.layoutManager = linearLayoutManager(requireContext())
        chatRecyclerView?.adapter = chatAdapter
    }
    private fun setupUserInputs() {
        sendButton?.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        chatId?.let {
            val message = messageEditText?.text.toString().trim()
            if (message.isNotEmpty()) {
                val chatMessage = Chat().apply {
                    this.messageText = message
                    this.senderId = userId
                    this.senderImage = user?.imgUrl ?: user?.photoUrl
                    this.senderName = userName
                    this.timestamp = System.currentTimeMillis()
                }
                database?.push()?.setValue(chatMessage)
                messageEditText?.text?.clear()
            } else {
                //Add Toast for empty message
                toast("Please enter a message")
            }
        } ?: run { toast("ChatId is null") }
    }

    override fun onStart() {
        super.onStart()
        realmInstance?.safeUser { user ->
            userId = user.id
            userName = user.name ?: "Anonymous"
            sender = user.name ?: "Anonymous"
        }
    }

    override fun onStop() {
        super.onStop()
        realmInstance?.removeAllChangeListeners()
        messageListener?.let { database?.removeEventListener(it) }
    }

}


