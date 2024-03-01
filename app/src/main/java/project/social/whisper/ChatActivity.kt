package project.social.whisper

import adapters.ChatAdapter
import adapters.DatabaseAdapter
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fragments.ContactFragment
import models.ChatModel
import project.social.whisper.databinding.ActivityChatBinding
import java.math.BigInteger
import java.security.SecureRandom
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

    //Activity Result Launcher
    private lateinit var readContacts: ActivityResultLauncher<Intent>

    private lateinit var callback: OnBackPressedCallback

    private lateinit var imageCapture: ActivityResultLauncher<Intent>

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

        //Activity Results
        readContacts = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Contact selected",Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Contact cancel",Toast.LENGTH_LONG).show()
            }
        }

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (b.flChatActCont.visibility == View.VISIBLE) {
                    Log.d("ASDASD","a")
                    b.flChatActCont.visibility = View.GONE
                    supportFragmentManager.popBackStack() // Pop the back stack when fragment is visible
                    // Consume the event
                } else {
                    Log.d("ASDASD","ab")
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()// Delegate to default navigation behavior
                    isEnabled = true
                }
            }
        }

        imageCapture = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val uri: Uri? = data?.data
                uploadImage(uri)
            } else {
                Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

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

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {

                    R.id.menu_contact -> {
                        requestContactPermission()

                        if (hasContactPermission()) {
                            val fragment = ContactFragment()

                            supportFragmentManager.beginTransaction()
                                .replace(R.id.fl_chat_act_cont, fragment)
                                .commit()

                            b.flChatActCont.visibility = View.VISIBLE
                        }
                    }

                    R.id.menu_image -> {
                        sendImage()
                    }

                    R.id.menu_location -> {
                        //sendLocation()
                    }

                    R.id.menu_camera -> {
                        sendWithCamera()
                    }
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

    private fun uploadImage(uri: Uri?) {
        try {
            if (uri != null) {
                DatabaseAdapter.chatImage.child(senderRoom).child(key).putFile(uri).addOnSuccessListener {

                    DatabaseAdapter.chatImage.child(senderRoom).child(key).downloadUrl.addOnSuccessListener { img ->

                        val chatMap = HashMap<String, Any>()
                        chatMap["SENDER_KEY"] = senderKey
                        chatMap["SENDER_UID"] = DatabaseAdapter.returnUser()?.uid!!
                        chatMap["MESSAGE"] = img.toString()
                        chatMap["TIMESTAMP"] = Date().time

                        try {
                            DatabaseAdapter.chatTable.child(senderRoom).push().setValue(chatMap)
                            DatabaseAdapter.chatTable.child(receiverRoom).push().setValue(chatMap)
                            isRequesting(img.toString())
                        }catch(e:Exception)
                        {
                            Log.d("DB_ERROR",e.toString())
                        }
                    }
                }
            }
            else
            {
                Toast.makeText(
                    applicationContext,
                    "Something went wrong, try again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }catch(e:Exception)
        {
            Log.d("DB_ERROR",e.toString())
        }
    }
    private fun sendWithCamera() {
        requestCameraPermission()

        if (hasCameraPermission()) {
            openCamera()
        } else {
            Toast.makeText(
                applicationContext,
                "Please give permission of camera",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openCamera() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop() //Crop image(Optional), Check Customization for more option
            .compress(1024) //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            ) //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                imageCapture.launch(intent)
            }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            applicationContext,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        val permission = ContextCompat.checkSelfPermission(
            applicationContext, android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(android.Manifest.permission.CAMERA)
        } else {
            Toast.makeText(applicationContext, "CAMERA granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun sendImage() {

    }

    override fun onDestroy() {
        super.onDestroy()

        // Remove the callback when the activity is destroyed
        callback.remove()
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

    private fun main() {
        // Step 1: Choose prime numbers p and g
        val p = BigInteger("23")
        val g = BigInteger("5")

        // Step 2: Generate private keys
        val aPrivate = generatePrivateKey()
        val bPrivate = generatePrivateKey()

        // Step 3: Calculate public keys
        val aPublic = calculatePublicKey(g, aPrivate, p)
        val bPublic = calculatePublicKey(g, bPrivate, p)

        // Step 4: Exchange public keys (simulate network exchange)
        // In a real scenario, Alice would send her public key to Bob, and vice versa.

        // Step 5: Calculate shared secret
        val sA = calculateSharedSecret(bPublic, aPrivate, p)
        val sB = calculateSharedSecret(aPublic, bPrivate, p)

        // Check if both parties derive the same shared secret
        if (sA == sB) {
            println("Shared secret: $sA")
        } else {
            println("Key exchange failed")
        }
    }

    private fun generatePrivateKey(): BigInteger {
        return BigInteger(16, SecureRandom())
    }

    private fun calculatePublicKey(g: BigInteger, privateKey: BigInteger, p: BigInteger): BigInteger {
        return g.modPow(privateKey, p)
    }

    private fun calculateSharedSecret(publicKey: BigInteger, privateKey: BigInteger, p: BigInteger): BigInteger {
        return publicKey.modPow(privateKey, p)
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