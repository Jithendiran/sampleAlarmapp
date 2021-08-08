@file:Suppress("DEPRECATION")

package com.jidesh.ji_alarm.alarm


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.jidesh.ji_alarm.R
import com.jidesh.ji_alarm.database.DatabaseHandler
import com.jidesh.ji_alarm.service.AlarmRingService
import java.util.*

import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import com.jidesh.ji_alarm.database.Database


class BroadCast: BroadcastReceiver()
{

    private lateinit var databaseHandler:DatabaseHandler



    @RequiresApi(Build.VERSION_CODES.Q)


        override fun onReceive(context: Context, intent: Intent)
        {
            if (intent.action.equals("com.jidesh.ji_alarm"))
            {

                databaseHandler = DatabaseHandler(context)
                val id = intent.extras!!.getInt("Id")
                val snooze_flag = intent.extras!!.getString("snooze")

                val dateandtime = databaseHandler.getOneValue(id)

                // sound -> only sound , both -> vibrate and sound
                val alarmIntent = Intent(context,AlarmRingService::class.java)
                alarmIntent.action="both"
                context.startService(alarmIntent )

                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
                val isScreenOn = pm!!.isScreenOn
                if (isScreenOn)
                {
                    Log.d("A","Screen on")
                    createNotification(context,dateandtime,snooze_flag)
                }
                else
                {
                    Log.d("A","Screen off")
                    context.startActivity(Intent(context,LockscreenActivity::class.java))
                    createNotification(context,dateandtime,snooze_flag)
                }



                val sharedPreferences = context.getSharedPreferences("com.jidesh.ji_alarm.shared", Context.MODE_PRIVATE)
                if (sharedPreferences.getBoolean("IsAlive", true))
                {
                    Toast.makeText(context, "It is active", Toast.LENGTH_SHORT).show()
                    //MainActivity().updatealarminbroadcast(dateandtime)
                    //MainActivity().check()
                }
                else
                {
                    Toast.makeText(context, "It is Not-active", Toast.LENGTH_SHORT).show()
                }

            }
        }

    private fun createNotification(context: Context,dateandtime:Database,snooze_flag:String?)
    {
        //notification
        //request code 1 -> snooze,2->off
        val snooze_intent:Intent = Intent(context,NotificationIntent::class.java)
        snooze_intent.action = "com.jidesh.ji_alarm.snooze"
        snooze_intent.putExtra("Id",dateandtime.id)

        val alarmoff_intent:Intent = Intent(context,NotificationIntent::class.java)
        alarmoff_intent.action = "com.jidesh.ji_alarm.off"
        alarmoff_intent.putExtra("Id",dateandtime.id)

        Log.d("A","Id in broadcast:${dateandtime.id}")
        Log.d("A","Id in alarmintent:${alarmoff_intent.extras!!.getInt("Id")}")
        Log.d("A","Id in snoozeintent:${snooze_intent.extras!!.getInt("Id")}")

        val snooze_pending:PendingIntent = PendingIntent.getBroadcast(context,1,snooze_intent,PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmoff_pending:PendingIntent = PendingIntent.getBroadcast(context,2,alarmoff_intent,PendingIntent.FLAG_UPDATE_CURRENT)

        val notification_layout = RemoteViews(context.packageName,R.layout.notification)

        notification_layout.setTextViewText(R.id.Desc,"${dateandtime.date_and_time}")
        notification_layout.setImageViewResource(R.id.Imgview,R.drawable.ic_baseline_alarm_on_24)
        notification_layout.setTextViewText(R.id.snooze,"Snooze")
        notification_layout.setTextViewText(R.id.off,"OFF")


        if (snooze_flag != null)
        {
            notification_layout.setTextViewText(R.id.Heading,"Snoozing")
        }
        else
        {
            notification_layout.setTextViewText(R.id.Heading,"Alarm ")
        }

        notification_layout.setOnClickPendingIntent(R.id.snooze,snooze_pending)
        notification_layout.setOnClickPendingIntent(R.id.off,alarmoff_pending)

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification:NotificationCompat.Builder = NotificationCompat.Builder(context,"JI_Alarm")
            .setSmallIcon(R.drawable.ic_baseline_access_alarm_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContent(notification_layout)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channelId = "com.jidesh.ji_alarm"
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager.createNotificationChannel(channel)
            notification.setChannelId(channelId)
        }

        mNotificationManager.notify(0, notification.build());
    }


}


