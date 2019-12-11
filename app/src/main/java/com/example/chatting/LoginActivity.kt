package com.example.chatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login.setOnClickListener{
            val emailnew= email_login.text.toString()
            val passwordnew= password_login.text.toString()
            Log.d("Login","Email is $emailnew")
            Log.d("Login","Password $passwordnew")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(emailnew,passwordnew)       //FireBase authentication to check if user has logged in with email and password
                .addOnCompleteListener(){
                    if (!it.isSuccessful)return@addOnCompleteListener
                    // Sign in success, update UI with the signed-in user's information
                    val intent = Intent(this, CurrentMessagesActivity::class.java)         //on successful log in redirect user to the current chat page
                    startActivity(intent)
                    Log.d("Login","successfully logged in ${it.result?.user?.uid}")


                }
                .addOnFailureListener {
                    Log.d("Login","Failed to login: ${it.message}")
                }






        }
            backtoregistration_text_view_register.setOnClickListener {
                finish()
            }

        }
    }

