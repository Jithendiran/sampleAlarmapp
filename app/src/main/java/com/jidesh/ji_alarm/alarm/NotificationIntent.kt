package com.jidesh.ji_alarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.jidesh.ji_alarm.database.DatabaseHandler
import com.jidesh.ji_alarm.service.AlarmRingService
import java.util.*


class NotificationIntent:BroadcastReceiver()
{

    override fun onReceive(context: Context?, intent: Intent?)
    {
        val snooze_time = 1

        val alarmIntent = Intent(context, AlarmRingService::class.java)

        if (context!=null)
        {
            NotificationManagerCompat.from(context).cancelAll()
            //NotificationManagerCompat.from(context).cancel(notificationId); Toremove particular
            var id = intent!!.extras!!.getInt("Id")
            Log.d("B","Id in broadcast:$id")

            var databaseHandler:DatabaseHandler = DatabaseHandler(context)
            val dateandtime = databaseHandler.getOneValue(id)

            if (intent.action.equals("com.jidesh.ji_alarm.snooze"))
            {
                //snooze
                alarmIntent.action="not"
                val alarm:AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val intent = Intent(context,BroadCast::class.java)
                intent.action = "com.jidesh.ji_alarm"
                intent.putExtra("Id",id)
                intent.putExtra("snooze","yes")
                val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                alarm.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + snooze_time * 60000,  //...start alarm again after 5 minutes
                    pendingIntent
                )

                Toast.makeText(context, "In snooze  : ${id}", Toast.LENGTH_SHORT).show()
                context.startService(alarmIntent )
            }
            else if (intent.action.equals("com.jidesh.ji_alarm.off"))
            {
                // alarm off
                alarmIntent.action="not"
                dateandtime.status = "Finished"
                databaseHandler.updatealarm(dateandtime)


                context.startService(alarmIntent )
                Toast.makeText(context, "Alarm off", Toast.LENGTH_SHORT).show()
            }
        }

    }
}