package com.reza.fcmsample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
Log.d("REZAAA", "onCreate")
        // jika punya payload, berarti dari notif
        val hasPayload = intent.getIntExtra("hasPayload", -1)
        if (hasPayload == 1) {
            tvLogs.text = ""
            intent.getStringExtra("score")?.let {
                tvLogs.append("Score: $it\n")
            }
            intent.getStringExtra("time")?.let {
                tvLogs.append("Time: $it\n")
            }
        }

        FirebaseApp.initializeApp(this)

        showToken.setOnClickListener { showToken() }
        subscribe.setOnClickListener {
            FirebaseMessaging.getInstance().subscribeToTopic(topicName.text.toString())
                .addOnCompleteListener { task ->
                    var msg = "Subscribe Success"
                    if (!task.isSuccessful) {
                        msg = "Subscribe Failed"
                    }
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        }
        unsubscribe.setOnClickListener {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName.text.toString())
                .addOnCompleteListener { task ->
                    var msg = "Unsubscribe Success"
                    if (!task.isSuccessful) {
                        msg = "Unsubscribe Failed"
                    }
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    tvLogs.text = "getInstanceId failed"
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
                tvLogs.text = msg
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })
    }

    @Throws(IOException::class, JSONException::class)
    fun addToGroup(
        senderId: String,
        userEmail: String,
        registrationId: String,
        idToken: String
    ): String {
        val url = URL("https://fcm.googleapis.com/fcm/googlenotification")
        val con = url.openConnection() as HttpURLConnection
        con.doOutput = true

        // HTTP request header
        con.setRequestProperty("project_id", senderId)
        con.setRequestProperty("Content-Type", "application/json")
        con.setRequestProperty("Accept", "application/json")
        con.requestMethod = "POST"
        con.connect()

        // HTTP request
        val data = JSONObject()
        data.put("operation", "add")
        data.put("notification_key_name", userEmail)
        data.put("registration_ids", JSONArray(arrayListOf(registrationId)))
        data.put("id_token", idToken)

        val os = con.outputStream
        os.write(data.toString().toByteArray(charset("UTF-8")))
        os.close()

        // Read the response into a string
        val `is` = con.inputStream
        val responseString = Scanner(`is`, "UTF-8").useDelimiter("\\A").next()
        `is`.close()

        // Parse the JSON string and return the notification key
        val response = JSONObject(responseString)
        return response.getString("notification_key")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d("REZAAA", "onNewIntent")
    }
}
