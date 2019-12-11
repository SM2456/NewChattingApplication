package com.example.chatting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log2.*
import kotlinx.android.synthetic.main.chat_from_message.view.*
import kotlinx.android.synthetic.main.chat_to_message.view.*

class ChatLog : AppCompatActivity() {

    companion object{
        val TAG="ChatLog"
    }

    val adapter=GroupAdapter<GroupieViewHolder>()

    var toUser:MainActivity.User?=null                                       //declaring toUser as a global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log2)

        recyclerview_chat_log.adapter=adapter
        //val username=intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<MainActivity.User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = toUser?.username                                         //setting the username of the current partner in the title bar
        //setUpDummyData()
        listenForMessages()

        send_button_chat_log.setOnClickListener {
        Log.d(TAG,"Attempt to send message...")
            performsendmessage()
        }
    }

    private fun listenForMessages(){
        val fromId=FirebaseAuth.getInstance().uid
        val toId=toUser?.uid
        val ref=FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId") //retrieving the fromId and toId from the database
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
               val chatMessage= p0.getValue(ChatMessage::class.java)     //creating a chatMessage instance of the class ChatMessage
                if(chatMessage!=null) {                                           //checking to see if the chatMessage is null
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {           //checking to see if the from id of the chatMessage corresponds to the user id of the user whose chatlog is opened
                        val currUser=CurrentMessagesActivity.currUser?:return       //if yes,then set the details of that user
                        adapter.add(ChatFromItem(chatMessage.text,currUser))
                    } else {

                        adapter.add(ChatToItem(chatMessage.text,toUser!!))                      //if no,then set it to the user who is currently logged in
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)            //seeting the position to the most recent message in the chatlog


            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }

    class ChatMessage(val id:String,val text: String,val fromId:String,val toId: String,val timestamp: Long){          //defining the ChatMessage class
        constructor():this("","","","",-1)
    }

    private fun performsendmessage(){
        val text=edittext_chat_log.text.toString()
        val fromId=FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<MainActivity.User>(NewMessageActivity.USER_KEY)
        val toId =user.uid

       // val reference=FirebaseDatabase.getInstance().getReference("/messages").push()

        if(fromId==null)return
        val reference=FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()  //setting up the reference for the fromId
        val toReference=FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()  //setting up the reference for the toId
        val chatMessage=ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"saved message successfully :${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)
            }
            .addOnFailureListener {
                Log.d(TAG,"failed due to: ${it.message}")
            }
        toReference.setValue(chatMessage)
        val latestMessagesRef=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")       //getting the reference for the latest from messages
        latestMessagesRef.setValue(chatMessage)
        val latestMessagesToRef=FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")     //getting the reference for the lates to messages
        latestMessagesToRef.setValue(chatMessage)
    }


}
class ChatFromItem(val text:String,val user:MainActivity.User):Item<GroupieViewHolder>(){        //creating the chatFromItem class
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_from_textView.text=text                                        //retrieving the text for the current logged in user from the partner user
        val uri=user.profileimageurl                                                     //loading the profile image
        val targetImageView=viewHolder.itemView.imageView_chatFrommessage         //loading the profile image into the target
        Picasso.get().load(uri).into(targetImageView)                                           //using the Picasso library to load the designated profile image

    }
    override fun getLayout(): Int {
       return R.layout.chat_from_message
    }
}
class ChatToItem(val text: String,val user:MainActivity.User):Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_to_textView.text=text
        val uri=user.profileimageurl
        val targetImageView=viewHolder.itemView.imageView_chatTorow
        Picasso.get().load(uri).into(targetImageView)

    }
    override fun getLayout(): Int {
        return R.layout.chat_to_message
    }
}
