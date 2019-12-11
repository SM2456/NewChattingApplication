package com.example.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.chatting.NewMessageActivity.Companion.USER_KEY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_current_messages.*
import kotlinx.android.synthetic.main.current_message_row.view.*

class CurrentMessagesActivity : AppCompatActivity() {

    companion object{
        var currUser:MainActivity.User?=null
        val TAG="CurrentMessagesActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_messages)
        recyclerview_latest_messages.adapter=adapter                                 //setting the adapter to the recycler view adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL)) //making the latest messages appear in vertical manner in the currentuser window

        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG,"1234")
            val intent=Intent(this,ChatLog::class.java)          //if the currentuser is selected from the user list..open the chatlog for that user
            val row=item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY,row.chatPartnerUser)     //taking user name with whom the logged in user is chatting from the newmessage activity
            startActivity(intent)

        }

        //setupDummyRows()


        listenForLatestMessages()


        fetchCurrUser()
         verifyUser()
        }
    class LatestMessageRow(val chatMessage:ChatLog.ChatMessage):Item<GroupieViewHolder>(){
        var chatPartnerUser:MainActivity.User?=null
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.message_textview_latest_message.text=chatMessage.text      //displaying the messages from a particular slected user

            val chatPartnerId:String
            if(chatMessage.fromId==FirebaseAuth.getInstance().uid){                         //assigning the values of partner ids as fromId and toId
                chatPartnerId=chatMessage.toId
            }else{
                chatPartnerId=chatMessage.fromId
            }

            val ref=FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")        //creating a reference variable with the chatPartnerId from the database
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                 chatPartnerUser=p0.getValue(MainActivity.User::class.java)
                    viewHolder.itemView.username_textview_latest_message.text=chatPartnerUser?.username           //loading partner with its username.
                    val targetImageView=viewHolder.itemView.imageview_latest_message              //loading the latest messages exchanged between the logged in user and the partner
                    Picasso.get().load(chatPartnerUser?.profileimageurl).into(targetImageView)                  //loading the profile image of the partner.
                }
                override fun onCancelled(p0: DatabaseError) {

                }
            })



        }
        override fun getLayout(): Int {
            return R.layout.current_message_row
        }
    }
   val latestMessagesMap=HashMap<String,ChatLog.ChatMessage>()   //creating a hashmap to store users and their corresponding chat messages with which the currently logged in user texted.
    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {     //iterating through the chatlog and adding the latest messages and the user profile based on chatpartnerid
            adapter.add(LatestMessageRow(it))
        }
    }
    private fun listenForLatestMessages(){
        val fromId=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")  //determining the messages coming from a user.
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage=p0.getValue(ChatLog.ChatMessage::class.java)?:return          //retireving the current message from the chatlog
                latestMessagesMap[p0.key!!]=chatMessage
                refreshRecyclerViewMessages()

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
             val chatMessage=p0.getValue(ChatLog.ChatMessage::class.java)?:return           //updating the latest message in the currentuser activity
                latestMessagesMap[p0.key!!]=chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    val adapter=GroupAdapter<GroupieViewHolder>()                                              //declaring the group adapter to access items from the database.


    private fun fetchCurrUser(){
        val uid=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")     //retrieving the user id for the current user
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currUser=p0.getValue(MainActivity.User::class.java)
                Log.d("CurrentMessagesActivity","Current user ${currUser?.profileimageurl}")   //loading the profile image of the currentuser with whom user is chatting
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyUser() {
        val uid=FirebaseAuth.getInstance().uid                                             //verying the partner uid with the details stored in the firebase
        if(uid==null){
            val intent=Intent(this,MainActivity::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
    }
}

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_new_message ->{
                val intent=Intent(this,NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this,MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }}
