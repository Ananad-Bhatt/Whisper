package project.social.whisper

import adapters.ChatAdapter
import adapters.DatabaseAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import models.ChatModel
import project.social.whisper.databinding.ActivityChatBinding
import java.util.Date


class ChatActivity : AppCompatActivity() {

    private lateinit var b:ActivityChatBinding

    private lateinit var key:String
    private lateinit var uid:String

    private lateinit var senderKey:String
    private lateinit var receiverKey:String

    private lateinit var senderRoom:String
    private lateinit var receiverRoom:String

    private var chats:ArrayList<ChatModel> = ArrayList()

    private var chatAdapter:ChatAdapter = ChatAdapter(this, chats)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityChatBinding.inflate(layoutInflater)
        setContentView(b.root)

        key = intent.getStringExtra("key")!!
        uid = intent.getStringExtra("uid")!!
        val userName = intent.getStringExtra("userName")!!
        val imgUrl = intent.getStringExtra("imgUrl")!!

        senderKey = DatabaseAdapter.key
        receiverKey = key

        senderRoom = senderKey + receiverKey
        receiverRoom = receiverKey + senderKey

        val lManager = LinearLayoutManager(this)
        lManager.stackFromEnd = true
        b.rvChatAct.layoutManager = lManager
        b.rvChatAct.adapter = chatAdapter

//        val itemTouchHelper = ItemTouchHelper(simpleCallback)
//        itemTouchHelper.attachToRecyclerView(b.rvChatAct)

//        b.rvChatAct.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
//            if (bottom < oldBottom) {
//                b.rvChatAct.scrollBy(0, oldBottom - bottom);
//            }
//        }

        receiveData()
        sendData()

        b.tvChatActUserName.text = userName
        Glide.with(this).load(imgUrl).into(b.imgChatActUserImage)

        //Accept Requests buttons
        b.btnActMsgReq.setOnClickListener {
            try {
                DatabaseAdapter.chatRooms.child(receiverRoom).child("IS_ACCEPTED").setValue(true)
                    .addOnCompleteListener {
                        b.llChatActMsgReq.visibility = View.GONE
                    }
            }catch(e:Exception)
            {
                Log.d("DB_ERROR",e.toString())
            }
        }

        b.tvChatActUserName.setOnClickListener {
            val i = Intent(this, UserProfileActivity::class.java)
            i.putExtra("userName",b.tvChatActUserName.text.toString())
            startActivity(i)
        }

        //Decline Requests buttons
        b.btnDecMsgReq.setOnClickListener {
            Toast.makeText(this,"Request declined!",Toast.LENGTH_LONG).show()
            b.llChatActMsgReq.visibility = View.GONE
        }
    }

//    private var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
//        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
//        0
//    ) {
//        override fun onMove(
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder,
//            target: RecyclerView.ViewHolder
//        ): Boolean {
//            return false
//        }
//
//        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
//
//        override fun onChildDraw(
//            c: Canvas,
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder,
//            dX: Float,
//            dY: Float,
//            actionState: Int,
//            isCurrentlyActive: Boolean
//        ) {
//            // Get the ConstraintLayout within the item view
//            val constraintLayout = viewHolder.itemView.findViewById<ConstraintLayout>(R.id.cl_sender_layout)
//
//            // Get the RelativeLayout bounds
//            val relativeLayout = viewHolder.itemView.findViewById<RelativeLayout>(R.id.ll_sender_chat)
//            val parentWidth = relativeLayout.width
//            val parentHeight = relativeLayout.height
//
//            // Calculate the bounds of the ConstraintLayout within the RelativeLayout
//            val layoutParams = constraintLayout.layoutParams as RelativeLayout.LayoutParams
//            val childLeft = layoutParams.leftMargin
//            val childTop = layoutParams.topMargin
//            val childRight = parentWidth - layoutParams.rightMargin - constraintLayout.width
//            val childBottom = parentHeight - layoutParams.bottomMargin - constraintLayout.height
//
//            // Restrict horizontal dragging within the bounds of the RelativeLayout
//            val clampedDx = dX.coerceIn(childLeft.toFloat(), childRight.toFloat())
//
//            // Ensure vertical dragging is disabled
//            super.onChildDraw(c, recyclerView, viewHolder, clampedDx, 0f, actionState, isCurrentlyActive)
//        }
//    }

    private fun receiveData() {
        receivingData()
        isVisible()
    }

    private fun isVisible(){

        var isAccepted:Boolean

        try{
            DatabaseAdapter.chatRooms.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists())
                    {
                        for(s in snapshot.children)
                        {
                            val key = s.key!!

                            if(key == receiverRoom)
                            {
                                isAccepted = s.child("IS_ACCEPTED").getValue(Boolean::class.java)!!

                                if(!isAccepted)
                                {
                                    b.llChatActMsgReq.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("DB_ERROR",error.toString())
                }
            })
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
    }

    private fun receivingData() {
        try {
            DatabaseAdapter.chatTable.child(receiverRoom).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    chats.clear()

                    if(snapshot.exists()) {
                        for (s in snapshot.children) {
                            val data: ChatModel = s.getValue(ChatModel::class.java)!!
                            chats.add(data)
                        }
                    }
                    chatAdapter.notifyItemInserted(chats.size)
                    b.rvChatAct.scrollToPosition(chatAdapter.itemCount-1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_LONG).show()
                }
            })
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
    }

    private fun sendData() {
        b.imgChatActSend.setOnClickListener {
                sendingMessage()
        }
    }

    private fun sendingMessage() {
        if(b.edtChatActMessage.text.toString().isNotEmpty())
        {
            val msg = b.edtChatActMessage.text.toString()

            val chatMap = HashMap<String, Any>()
            chatMap["SENDER_KEY"] = senderKey
            chatMap["SENDER_UID"] = DatabaseAdapter.returnUser()?.uid!!
            chatMap["MESSAGE"] = msg
            chatMap["TIMESTAMP"] = Date().time

            try {
                DatabaseAdapter.chatTable.child(senderRoom).push().setValue(chatMap)
                DatabaseAdapter.chatTable.child(receiverRoom).push().setValue(chatMap)
                isRequesting(msg)
            }catch(e:Exception)
            {
                Log.d("DB_ERROR",e.toString())
            }

            b.edtChatActMessage.text.clear()
        }
    }

    private fun isRequesting(msg:String) {

        var request = true
        var isSender = true

        try {
            DatabaseAdapter.chatRooms.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        for (s in snapshot.children) {
                            val key = s.key!!

                            if (key == senderRoom || key == receiverRoom) {
                                request = false

                                val user = s.child("USER_1").getValue(String::class.java)!!

                                isSender = user == senderKey
                            }
                        }
                    }

                        if (request) {
                            DatabaseAdapter.chatRooms.child(senderRoom).child("USER_1")
                                .setValue(senderKey)

                            DatabaseAdapter.chatRooms.child(senderRoom).child("USER_1_UID")
                                .setValue(DatabaseAdapter.returnUser()?.uid!!)

                            DatabaseAdapter.chatRooms.child(senderRoom).child("USER_2")
                                .setValue(receiverKey)

                            DatabaseAdapter.chatRooms.child(senderRoom).child("USER_2_UID")
                                .setValue(uid)

                            DatabaseAdapter.chatRooms.child(senderRoom).child("IS_ACCEPTED")
                                .setValue(false)

                            DatabaseAdapter.chatRooms.child(senderRoom).child("LAST_MESSAGE")
                                .setValue(msg)
                        }
                        else
                        {
                            if(isSender) {
                                DatabaseAdapter.chatRooms.child(senderRoom).child("LAST_MESSAGE")
                                    .setValue(msg)
                            }else
                            {
                                DatabaseAdapter.chatRooms.child(receiverRoom).child("LAST_MESSAGE")
                                    .setValue(msg)
                            }
                        }

                }

                override fun onCancelled(e: DatabaseError) {
                    Log.d("DB_ERROR", e.toString())
                }
            })
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
    }

}