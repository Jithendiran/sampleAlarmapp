package com.jidesh.ji_alarm.customdialogue

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.jidesh.ji_alarm.MainActivity
import com.jidesh.ji_alarm.R
import com.jidesh.ji_alarm.calander.Calander
import com.jidesh.ji_alarm.database.Database
import java.util.*

@Suppress("DEPRECATION")
class CustomDialogue():DialogFragment(),DatePickerDialog.OnDateSetListener
{


    lateinit var date_pick:TextView

    var calendar = Calendar.getInstance()
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var month:Int = calendar.get(Calendar.MONTH)
    var year:Int = calendar.get(Calendar.YEAR)

    var calendarji:Calander = Calander()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view:View = inflater.inflate(R.layout.alarm,container,false)

        date_pick= view.findViewById(R.id.date_pick)
        val time_pick:TimePicker = view.findViewById(R.id.time_pick)
        val ok_btn:Button = view.findViewById(R.id.set_alarm)




        val date: String = calendarji.getDateString(calendar.time)

        date_pick.text = date

        date_pick.setOnClickListener {

            DatePickerDialog(view.context,this,year,month,day).show()

        }

        ok_btn.setOnClickListener {

            val hour: Int
            val minute: Int
            if(Build.VERSION.SDK_INT >= 23)
            {
                 hour = time_pick.hour
                 minute = time_pick.minute

            }
            else
            {
               hour = time_pick.currentHour
                minute = time_pick.currentMinute
            }

            calendar.set(Calendar.HOUR_OF_DAY,hour)
            calendar.set(Calendar.MINUTE,minute)
            calendar.set(Calendar.SECOND,0)

            (context as MainActivity).addalarm(Database(0,calendarji.sqlDateFormat(calendar.time),"Not"))

            dismiss()
        }

        return view
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int)
    {


        day = dayOfMonth
        this.month = month
        this.year = year

        this.calendar.set(Calendar.YEAR,year)
        this.calendar.set(Calendar.MONTH,month)
        this.calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)

        date_pick.text = calendarji.getDateString(calendar.time)

    }


}