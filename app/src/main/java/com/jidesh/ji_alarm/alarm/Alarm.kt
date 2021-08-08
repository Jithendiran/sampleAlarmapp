package com.jidesh.ji_alarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log

import android.widget.Toast
import com.jidesh.ji_alarm.calander.Calander

import java.util.*

class Alarm(private var context:Context)
{

    private val calendar = Calander()

    fun setAlarm(req:Int,dateandtime:String)
    {
        /**
         * This function trigger alaram on particular date and time
         *
         * 'req' unique identify alarm
         * 'dateandtime' contain date and time
         */
        val alarm:AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context,BroadCast::class.java)
        intent.action = "com.jidesh.ji_alarm"

        intent.putExtra("Id",req)

        val date: Date = calendar.getDateFromString(dateandtime)

        val pendingIntent:PendingIntent = PendingIntent.getBroadcast(context,req,intent,PendingIntent.FLAG_UPDATE_CURRENT)

        alarm.setExact(AlarmManager.RTC_WAKEUP,calendar.toCalendar(date).timeInMillis,pendingIntent)

        Toast.makeText(context,"Alarm in ${calendar.remaintime(calendar.toCalendar(date).timeInMillis - calendar.getCurrentDate().time)}",Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(req:Int):Int
    {
        try {
            val alarm:AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context,BroadCast::class.java)
            intent.action = "com.jidesh.ji_alarm"
            val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, req, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarm.cancel(pendingIntent)

            return 0

        }
        catch (e:Exception)
        {
            Log.d("E",e.message!!)
            return -1
        }

    }
}
