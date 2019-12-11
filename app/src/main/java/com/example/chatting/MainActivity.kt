package com.example.chatting

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register_button2_register.setOnClickListener {
            val email = email_editText13_register.text.toString()            //taking email id in variable email
            val password = password_editText14_register.text.toString()      //taking password in variable password
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter text in emailid/password", Toast.LENGTH_SHORT)//checking to see if email/password empty
                    .show()
                return@setOnClickListener

            }

            Log.d("MainActivity", "Email is " + email)
            Log.d("MainActivity", "Password $password")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)    //doing the firebase authentication
                .addOnCompleteListener() {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("MainActivity", "createUserWithEmail:success ${it.result?.user?.uid}")    //if authentication is successful,create user with an user id
                    uploadImageToFireBaseStorage()
                }

                .addOnFailureListener {
                    Log.d("MainActivity", "Failed to create user: ${it.message}")
                    Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT)  //on failure give a toast message
                        .show()
                }


            //else if successful


        }



        alraedy_have_account_text_view.setOnClickListener {               //if user already have an account
            Log.d("MainActivity", "Try to show log in activity")
            //lauch activity somehow

            val intent = Intent(this, LoginActivity::class.java)    //redirect the user to the user log in section
            startActivity(intent)


        }
        profile_photo_button.setOnClickListener {
            Log.d("MainActivity", "Show photo selector")             //adding profile photo to the user account
            val intent = Intent(Intent.ACTION_PICK)                           //picking up the photo from image gallery of the phone
            intent.type = "image/*"
            startActivityForResult(intent, 0)

        }


    }

    private fun uploadImageToFireBaseStorage() {                              //uploading the profile photo to the firebase storage.
        if (selectedPhotoUri == null) return                                    //check to see if the photo uri is not null
        val filename = UUID.randomUUID()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")  //store the image in the corresponding images node of the firebase storage
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("MainActivity", "Successfully uploaded image: ${it.metadata?.path}")   //on successful upload show the image path in the log activity
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("MainActivity", "File Location: $it")                               //give the file location of the image in the firebase storage

                    saveUserToFirebaseDatabase(it.toString())                                         //save the details of the user in the firebasestorage
                }

            }
            .addOnFailureListener {
                Log.d("MainActivity", "User cannot be saved to database: ${it.message}")
            }


    }

    private fun saveUserToFirebaseDatabase(profileimageurl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""                                 //declare the uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")    //get the reference of the uid from the database
        val user = User(uid, username_editText_registration.text.toString(), profileimageurl)         //create an instance variable of the User class
        ref.setValue(user)                                                                           //user the reference variable to access the user
            .addOnSuccessListener {
                Log.d("MainActivity", "User finally saved to database")
                val intent=Intent(this,CurrentMessagesActivity::class.java)            //on successful save, open the currentmessage log of the user
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("MainActivity", "User values not set: ${it.message}")
            }
    }

    @Parcelize
    class User(val uid: String, val username: String, val profileimageurl: String):Parcelable{      //creating the user class
        constructor() : this("", "", "")}



    var selectedPhotoUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //check what image was selected..
            Log.d("MainActivity", "Photo selected")
            selectedPhotoUri = data.data
           val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri) //converting the photo uri to bitmap image
            photo_imageview_register.setImageBitmap(bitmap)                                     //setting the profilephot in the photo_image_viewer
            profile_photo_button.alpha=0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //profile_photo_button.setBackgroundDrawable(bitmapDrawable)
        }
    }


}

