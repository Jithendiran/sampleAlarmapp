package com.jidesh.ji_alarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.jidesh.ji_alarm.database.Database
import com.jidesh.ji_alarm.database.DatabaseHandler

class BootupAlarmRestart:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if("android.intent.action.BOOT_COMPLETED" == intent?.action)
        {   if(context!=null)
            {
                var database: List<Database> = DatabaseHandler(context).viewalarm()
                for (i in database) {
                    Log.d("A", "Resart .. alarm")
                    when (i.status)
                    {

                        "Not" -> i.date_and_time?.let { Alarm(context).setAlarm(i.id, it) }
                    }
                }
            }

        }
    }
}