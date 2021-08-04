package com.jidesh.ji_alarm.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import androidx.annotation.RequiresApi


class AlarmRingService:IntentService("com.jidesh.ji_alarm")
{
    companion object
    {
        lateinit  var  r: Ringtone
        lateinit var  vibrator: Vibrator

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onHandleIntent(intent: Intent?)
    {
        if(intent!!.action.equals("sound"))
        {

            notificationsound(applicationContext)


        }
        else if(intent!!.action.equals("both"))
        {
            val runnable_sound = Runnable{
                notificationsound(applicationContext)
            }
            val runnable_vibrate = Runnable{
                vibrate(applicationContext)
            }
            Thread(runnable_sound).start()
            Thread(runnable_vibrate).start()
        }
        else if(intent!!.action.equals("not"))
        {
            stopSound()
            stopVibrate()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
     fun vibrate(context: Context?)
    {
        val pattern = longArrayOf(0, 200, 0) //0 to start now, 200 to vibrate 200 ms, 0 to sleep for 0 ms.
         vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26)
        {
            //vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.EFFECT_DOUBLE_CLICK))
            vibrator.vibrate(pattern,0)// 0 to repeat endlessly.
        }
        else
        {
            vibrator.vibrate(pattern,0)
        }
    }

    fun notificationsound(context: Context?)
    {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        r = RingtoneManager.getRingtone(context,
            notification)
        r.play()

    }

    fun stopSound()
    {
        r.stop()
    }
    fun stopVibrate()
    {
        vibrator.cancel()
    }
}