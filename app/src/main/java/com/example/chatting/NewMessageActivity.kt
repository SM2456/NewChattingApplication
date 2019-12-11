package com.example.chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import androidx.recyclerview.widget.RecyclerView.ViewHolder as ViewHolder

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title="Contacts"
        val adapter=GroupAdapter<GroupieViewHolder>()
        recyclerView_NewMessage.adapter=adapter
       fetchUsers()


    }
    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(){
        val ref=FirebaseDatabase.getInstance().getReference("/users") //creating a database reference variable to access the users
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter=GroupAdapter<GroupieViewHolder>()                            //declaring the group adapter
                p0.children.forEach {
                    Log.d("NewMessageActivity",it.toString())
                    val user=it.getValue(MainActivity.User::class.java)
                    if(user!=null) {
                        adapter.add(UserItem(user))                                  //adding the users in the NewMessageActivity recyclerview using the recyclerview adapter
                    }

                }
                adapter.setOnItemClickListener { item, view ->

                    val userItem=item as UserItem
                    val intent=Intent(this@NewMessageActivity,ChatLog::class.java)     //clicking on a particular user from the users list and redirecting it to the chat activity
                    //intent.putExtra(USER_KEY,userItem.user.username)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                }
                recyclerView_NewMessage.adapter=adapter                         //setting the groupadapter to recyclerview_newMessage adapter

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
class UserItem(val user:MainActivity.User):Item<GroupieViewHolder>(){       //UserItem class getting the username and the profile images of the users in the currently logged user's list.
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text=user.username      //putting the username
    Picasso.get().load(user.profileimageurl).into(viewHolder.itemView.imageview_new_message)   //putting the profile picture

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}


