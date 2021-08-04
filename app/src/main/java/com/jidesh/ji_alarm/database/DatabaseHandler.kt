package com.jidesh.ji_alarm.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.jidesh.ji_alarm.alarm.BootupAlarmRestart

class DatabaseHandler(context: Context):SQLiteOpenHelper(context,DATABASE_NAME,null, DATABASE_VERSION)
{
    companion object
    {
        var DATABASE_NAME = "Ji_Alarm"
        var  DATABASE_VERSION = 1
        var  TABLE_NAME = "alarm"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "date_and_time"
        private const val KEY_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val table =
            "CREATE TABLE $TABLE_NAME($KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,$KEY_NAME DATETIME,$KEY_STATUS TEXT)"
        db?.execSQL(table)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

     fun addalarm(date_and_time:Database):Long
    {
        val db = writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME,date_and_time.date_and_time)
        contentValues.put(KEY_STATUS,date_and_time.status)

        val success= db.insert(TABLE_NAME,null,contentValues)

        db.close()
        return success

    }

     fun updatealarm(date_and_time: Database):Int
    {
        val db = writableDatabase
        Log.d("E","In update database : $date_and_time")
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME,date_and_time.date_and_time)
        contentValues.put(KEY_STATUS,date_and_time.status)

        val success:Int= db.update(TABLE_NAME,contentValues, "$KEY_ID=${date_and_time.id}",null)
        db.close()

        return success
    }

     fun deletealarm(date_and_time: Database):Int
    {
        var db = writableDatabase


        var success= db.delete(TABLE_NAME,"$KEY_ID=${date_and_time.id}",null)
        db.close()

        return success
    }

     @SuppressLint("Recycle")
     fun viewalarm():List<Database>
    {
        val datas:ArrayList<Database> = ArrayList()

        val selectQuery = "SELECT  * FROM $TABLE_NAME"
        val db = this.readableDatabase

        val cursor: Cursor?

        try
        {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException)
        {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if(cursor.moveToFirst())
        {
            do
            {
                val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val date_and_time = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                val status = cursor.getString(cursor.getColumnIndex(KEY_STATUS))
                Log.d("A","IN database : $id")
                datas.add(Database(id,date_and_time,status))

            }while (cursor.moveToNext())
        }

        return datas
    }

    fun getOneValue(id:Int):Database
    {
        val selectQuery = "SELECT  * FROM $TABLE_NAME WHERE $KEY_ID=$id"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        if (cursor.moveToFirst())
        {
            val id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
            val date_and_time = cursor.getString(cursor.getColumnIndex(KEY_NAME))
            val status = cursor.getString(cursor.getColumnIndex(KEY_STATUS))

            return Database(id, date_and_time, status)
        }

        return Database(0,null,null)
    }
}