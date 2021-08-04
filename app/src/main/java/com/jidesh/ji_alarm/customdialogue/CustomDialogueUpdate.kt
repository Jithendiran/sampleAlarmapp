package com.jidesh.ji_alarm.customdialogue

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
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

class CustomDialogueUpdate(var dateandtime:Database,var position:Int): DialogFragment(), DatePickerDialog.OnDateSetListener
{
    lateinit var date_pick: TextView
    var calendar = Calendar.getInstance()
    var calendarji = Calander()

    var date:Date = calendarji.getDateFromString(dateandtime.date_and_time!!)

    var cal = calendarji.toCalendar(date)

    var day = cal.get(Calendar.DAY_OF_MONTH)
    var month = cal.get(Calendar.MONTH)
    var year:Int = cal.get(Calendar.YEAR)




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view:View = inflater.inflate(R.layout.alarm,container,false)


        date_pick= view.findViewById(R.id.date_pick)
        val time_pick: TimePicker = view.findViewById(R.id.time_pick)
        val ok_btn: Button = view.findViewById(R.id.set_alarm)
        if(Build.VERSION.SDK_INT >= 23)
        {
            time_pick.hour = date.hours
            time_pick.minute = date.minutes

        }
        else
        {
            time_pick.currentHour =  date.hours
            time_pick.currentMinute =  date.minutes
        }
        date_pick.text = calendarji.getDateString(date)

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



            //date = calendarji.getDateFromString(dateandtime.date_and_time!!)
            if(calendarji.toCalendar(date).timeInMillis - calendarji.getCurrentDate().time < 0)
            {
                val c = Calendar.getInstance()
                c.time = date
                c.add(Calendar.DATE, 1)
                Log.d("Time","${c.time}")
                Log.d("Next Alarm","${c.time.time - calendarji.getCurrentDate().time}")
                val datestring = calendarji.sqlDateFormat(c.time)
                (context as MainActivity).updateAlarm( Database(dateandtime.id,datestring,"Not"),position)
                date = calendarji.getDateFromString(calendarji.sqlDateFormat(c.time))

            }
            else
            {
                // < 0 code
                (context as MainActivity).updateAlarm( Database(dateandtime.id,calendarji.sqlDateFormat(calendar.time),"Not"),position)

            }

            //(context as MainActivity).updateAlarm(Database(dateandtime.id,calendarji.sqlDateFormat(calendar.time),"Not"),position)

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