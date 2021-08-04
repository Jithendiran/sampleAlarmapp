package com.jidesh.ji_alarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.jidesh.ji_alarm.database.DatabaseHandler
import com.jidesh.ji_alarm.service.AlarmRingService

class NotificationIntent:BroadcastReceiver()
{

    override fun onReceive(context: Context?, intent: Intent?)
    {
        val alarmIntent = Intent(context, AlarmRingService::class.java)
        alarmIntent.action="not"

        if (context!=null)
        {
            var id = intent!!.extras!!.getInt("Id")
            Log.d("B","Id in broadcast:$id")

            var databaseHandler:DatabaseHandler = DatabaseHandler(context)
            val dateandtime = databaseHandler.getOneValue(id)

            if (intent.action.equals("com.jidesh.ji_alarm.snooze"))
            {
                //snooze
                context?.startService(alarmIntent )
                Toast.makeText(context, "In snooze", Toast.LENGTH_SHORT).show()
            }
            else if (intent.action.equals("com.jidesh.ji_alarm.off"))
            {
                // alarm off

                dateandtime.status = "Finished"
                databaseHandler.updatealarm(dateandtime)


                context?.startService(alarmIntent )
                Toast.makeText(context, "Alarm off", Toast.LENGTH_SHORT).show()
            }
        }

    }
}