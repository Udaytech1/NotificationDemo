package com.example.notification

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Storing data into SharedPreferences
        // Storing data into SharedPreferences
        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        if(sharedPreferences.getString("token","")=="") {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                myEdit.putString("token", token)

                myEdit.commit()
                myEdit.apply()

                // Log and toast
                Log.d("Token", token)
                System.out.println("Token==========="+token+".")
                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })
        }else{
            System.out.println("Token==========="+sharedPreferences.getString("token","").toString()+".")
            Log.d("Token", sharedPreferences.getString("token","").toString()+".")
        }
    }
}