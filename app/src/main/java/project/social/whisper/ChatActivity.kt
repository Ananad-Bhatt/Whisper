package project.social.whisper

import adapters.ChatAdapter
import adapters.DatabaseAdapter
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fragments.ContactFragment
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

    private var contactNames:ArrayList<String> = ArrayList()
    private var contactNumbers:ArrayList<String> = ArrayList()

    //Activity Result Launcher
    private lateinit var readContacts: ActivityResultLauncher<Intent>

    //Permission callback
    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                Toast.makeText(this, "Granted", Toast.LENGTH_LONG).show()
            }

            false -> {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityChatBinding.inflate(layoutInflater)
        setContentView(b.root)

        readContacts = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Contact selected",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Contact cancel",Toast.LENGTH_LONG).show()
            }
        }

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

        b.ibChatActAttach.setOnClickListener {
            val popupMenu = PopupMenu(this, b.ibChatActAttach)

            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.chat_pop_up_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { _ ->

                requestContactPermission()

                if(hasContactPermission())
                {
                    readContact()
                    Log.d("CONTACT",contactNames[1])
                    val fragment = ContactFragment()
                    val args = Bundle().apply {
                        putStringArrayList("contact", contactNames)
                    }
                    fragment.arguments = args

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fl_chat_act_cont, fragment)
                        .commit()

                    b.flChatActCont.visibility = View.VISIBLE
                }
                true
            }
            // Showing the popup menu
            popupMenu.show()
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

    override fun onBackPressed() {
        if (b.flChatActCont.visibility == View.VISIBLE) {
            Log.d("ASDASD","a")
            b.flChatActCont.visibility = View.GONE
            supportFragmentManager.popBackStack() // Pop the back stack when fragment is visible
            // Consume the event
        } else {
            Log.d("ASDASD","ab")
            super.onBackPressed()// Delegate to default navigation behavior
        }
    }
    private fun readContact() {
        val contentResolver= contentResolver;
        val cursor=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null)
        if (cursor!!.moveToFirst()){
            if (cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME) >= 0) {
                do {
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val number = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                    if(name != null && number != null) {
                        contactNames.add(name)
                        contactNumbers.add(number)
                    }

                } while (cursor.moveToNext())
            }
        }
    }

    private fun hasContactPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactPermission() {
        val permission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.READ_CONTACTS
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(android.Manifest.permission.READ_CONTACTS)
        } else {
            Toast.makeText(this, "Contact granted", Toast.LENGTH_LONG).show()
        }
    }

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