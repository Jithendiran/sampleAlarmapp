@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.jidesh.ji_alarm.calander



import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*




class Calander
{


    @SuppressLint("SimpleDateFormat")
    private var  dateFormat = SimpleDateFormat("E,dd MMM yyy")


    @SuppressLint("SimpleDateFormat")
    private var sqldate = SimpleDateFormat("yyyy-MM-dd HH:mm:SS")

    fun getDateFromString(dateString: String):Date
    {
        /**
         * This function get string as input and return date as output in 2001-02-27 23:56:02 in this format
         */
        return sqldate.parse(dateString)

    }

    fun getCurrentDate():Date
    {
        /**
         * This function return current date and time
         */

        //System.currentTimeMillis()
        return Calendar.getInstance().time
    }

    fun getDateString(date:Date):String
    {
        /**
         *  This function return date in Mon,01 Jun 2001 in this format
         */
        return dateFormat.format(date)
    }

    fun sqlDateFormat(date:Date):String
    {
        /**
         * This function return date in 2001-02-27 23:56:02 in this format
         */
        return sqldate.format(date)
    }

    fun toCalendar(date: Date?): Calendar
    {
        /**
         * This function return date to calander
         */
        val cal = Calendar.getInstance()
        cal.time = date
        return cal
    }

    fun remaintime(timeinmillisec: Long): String {
        /**
         * This method return remaining time in DD HH MM SS format
         */
        val seconds: Long = timeinmillisec / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return "Days:$days Hours:${hours % 24} Minutes:${minutes % 60} Seconds:${seconds % 60}"
    }
}