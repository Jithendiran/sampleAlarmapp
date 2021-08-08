package com.jidesh.ji_alarm.alarm

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.DigitalClock
import android.widget.TextClock
import androidx.appcompat.app.AppCompatActivity
import com.jidesh.ji_alarm.R
import com.jidesh.ji_alarm.database.Database
import java.lang.Exception
import java.lang.NullPointerException

class LockscreenActivity : AppCompatActivity() {


    lateinit var  dateandtime: Database
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        showWhenLockedAndTurnScreenOn()
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        try
        {
            dateandtime = intent.extras!!.getParcelable("com.jidesh.ji_alarm.data")!!
            Log.d("A", "Off screnn ${dateandtime}")
        }
        catch (e:Exception)
        {
            Log.d("A", "Off screnn error ${e.message}")
        }


        setContentView(R.layout.fullscreen)
//        var digitalclock:TextClock = findViewById(R.id.digital_clock)
//        var analogClock:View = findViewById(R.id.analog_clock)
        var snoozebutton:Button = findViewById(R.id.snoozebutton)
        var offebutton:Button = findViewById(R.id.offbutton)

        snoozebutton.setOnClickListener {
            val snooze_intent: Intent = Intent(applicationContext,NotificationIntent::class.java)
            snooze_intent.action = "com.jidesh.ji_alarm.snooze"
            snooze_intent.putExtra("Id",dateandtime.id)
            val snooze_pending: PendingIntent = PendingIntent.getBroadcast(applicationContext,1,snooze_intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

            sendBroadcast(snooze_intent)
            finishAndRemoveTask()

        }
        offebutton.setOnClickListener {

            val alarmoff_intent: Intent = Intent(applicationContext,NotificationIntent::class.java)
            alarmoff_intent.action = "com.jidesh.ji_alarm.off"
            alarmoff_intent.putExtra("Id",dateandtime.id)
            val alarmoff_pending: PendingIntent = PendingIntent.getBroadcast(applicationContext,2,alarmoff_intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
            sendBroadcast(alarmoff_intent)
            finishAndRemoveTask()
        }

    }

    private fun showWhenLockedAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

}