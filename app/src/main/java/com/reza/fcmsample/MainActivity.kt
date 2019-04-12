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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        Log.d("REZAAA", "onNewIntent")
    }
}
