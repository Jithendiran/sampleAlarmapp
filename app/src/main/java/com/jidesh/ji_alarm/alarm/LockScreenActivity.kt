package com.jidesh.ji_alarm.alarm

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.jidesh.ji_alarm.R

class LockscreenActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("A","Lockscereen activity")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fullscreen)

        showWhenLockedAndTurnScreenOn()
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