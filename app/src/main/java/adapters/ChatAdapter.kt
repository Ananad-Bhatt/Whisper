package adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import io.github.hyuwah.draggableviewlib.DraggableView
import io.github.hyuwah.draggableviewlib.DraggableView.*
import io.github.hyuwah.draggableviewlib.setupDraggable
import models.ChatModel
import project.social.whisper.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val context: Context, private val chats:ArrayList<ChatModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENDER_VIEW_TYPE = 1
    private val RECEIVER_VIEW_TYPE = 2

    private var prevX:Float = 0f
    private var prevY:Float = 0f
    private val thresholdX = 154f


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == SENDER_VIEW_TYPE) {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.sender_layout, parent, false)
            SenderChatHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.receiver_layout, parent, false)
            ReceiverChatHolder(view)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val m = chats[position]
        val d = Date(m.TIMESTAMP!!)

        val f = SimpleDateFormat("hh:mm a", Locale.getDefault())

        if(holder.javaClass == SenderChatHolder::class.java)
        {
            val h = holder as SenderChatHolder
            h.senderMessage.text = m.MESSAGE
            h.senderTime.text = f.format(d)


//            h.senderMainView.setupDraggable()
//            .setStickyMode(Mode.STICKY_X)
//            .setAnimated(true)
//            .build()


//            h.senderMainView.setOnTouchListener { view, motionEvent ->
//
//                var originalX = 0f
//                var originalY = 0f
//
//                when(motionEvent.action)
//                {
//                    MotionEvent.ACTION_DOWN -> {
//                        prevX = motionEvent.rawX// - view.x
//                        prevY = motionEvent.rawY// - view.y
//
//                        Log.d("VIEW_DRAG","Sender Original X ${prevX}")
//                        Log.d("VIEW_DRAG","Sender Original Y ${prevY}")
//
//                        originalX = view.x
//                        originalY = view.y
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        val newX = motionEvent.rawX// - prevX
//                        val newY = motionEvent.rawY// - prevY
//
//                        Log.d("VIEW_DRAG","Sender New X ${newX}")
//                        Log.d("VIEW_DRAG","Sender New Y ${newY}")
//
//                        updateViewPosition(view, newX, newY)
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        // Check if the view has been dragged beyond the threshold
//                        // Animate the view back to its original position
//                        view.animate()
//                            .x(originalX)
//                            .y(originalY)
//                            .setDuration(300)
//                            .start()
//                    }
//                }
//                true
//            }
        }

        else
        {
            val h = holder as ReceiverChatHolder
            h.receiverMessage.text = m.MESSAGE
            h.receiverTime.text = f.format(d)

//            var originalX = 0f
//            var originalY = 0f
//
//            h.receiverMainView.setOnTouchListener { view, motionEvent ->
//
//                when(motionEvent.action)
//                {
//                    MotionEvent.ACTION_DOWN -> {
//                        prevX = motionEvent.rawX// - view.x
//                        prevY = motionEvent.rawY// - view.y
//
//                        Log.d("VIEW_DRAG","Receiver Original X ${view.x}")
//                        Log.d("VIEW_DRAG","Receiver Original Y ${view.y}")
//
//                        originalX = view.x
//                        originalY = view.y
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        val newX = motionEvent.rawX// - prevX
//                        val newY = motionEvent.rawY// - prevY
//
//                        Log.d("VIEW_DRAG","Receiver New X ${newX}")
//                        Log.d("VIEW_DRAG","Receiver New Y ${newY}")
//
//                        updateViewPosition(view, newX, newY)
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        // Check if the view has been dragged beyond the threshold
//                        // Animate the view back to its original position
//                        view.animate()
//                            .x(originalX)
//                            .y(originalY)
//                            .setDuration(300)
//                            .start()
//                    }
//                }
//                true
//            }
        }
    }

    private fun updateViewPosition(view: View, newX: Float, newY: Float) {
        val parent = view.parent as View
        val maxX = parent.width - view.width
        val maxY = parent.height - view.height

        val clampedX = newX.coerceIn(0f, maxX.toFloat())
        val clampedY = newY.coerceIn(0f, maxY.toFloat())

        view.x = clampedX
        view.y = clampedY
    }

    override fun getItemViewType(position: Int): Int {
        return if(chats[position].USER_KEY.equals(DatabaseAdapter.returnUser()?.uid)) {
            SENDER_VIEW_TYPE
        } else {
            RECEIVER_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    class SenderChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderMessage = itemView.findViewById<TextView>(R.id.tv_sender_chat)!!
        val senderTime = itemView.findViewById<TextView>(R.id.tv_sender_chat_time)!!
        val senderMainView = itemView.findViewById<ConstraintLayout>(R.id.cl_sender_layout)!!
    }

    class ReceiverChatHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiverMessage = itemView.findViewById<TextView>(R.id.tv_receiver_chat)!!
        val receiverTime = itemView.findViewById<TextView>(R.id.tv_receiver_chat_time)!!
        val receiverMainView = itemView.findViewById<LinearLayout>(R.id.ll_receiver_chat)!!
    }
}