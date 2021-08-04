package com.jidesh.ji_alarm

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jidesh.ji_alarm.alarm.Alarm
import com.jidesh.ji_alarm.customdialogue.CustomDialogue
import com.jidesh.ji_alarm.database.Database
import com.jidesh.ji_alarm.database.DatabaseHandler
import com.jidesh.ji_alarm.recycler.Adapter

class MainActivity() : AppCompatActivity()
{
    lateinit var  data: ArrayList<Database>
    private lateinit var recycler:RecyclerView
    //private lateinit var data:ArrayList<Database>
    private lateinit var noalarm:TextView
    private var databaseHandler:DatabaseHandler = DatabaseHandler(this)
    private lateinit var alarm :Alarm
    private  lateinit var itemSwiper:ItemTouchHelper

    override fun onStart() {
        super.onStart()
        val sharedPref = applicationContext.getSharedPreferences("com.jidesh.ji_alarm.shared",Context.MODE_PRIVATE)
        val edit =  sharedPref.edit()
        edit.putBoolean("IsAlive",true)
        edit.apply()
        Log.d("A","On start")
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        Log.d("A","ONcreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val add_btn:View = findViewById<View>(R.id.floating_action_button)
        itemSwiper = ItemTouchHelper(simpleCallbackSwipper)
        alarm = Alarm(this)
        noalarm = findViewById(R.id.tvNoRecordsAvailable)
        recycler = findViewById(R.id.recycler)

        setrecycler()
        add_btn.setOnClickListener {
            Log.d("A","Before add alarm")
            CustomDialogue().show(supportFragmentManager,"Add Alarm")
            Log.d("A","After add alarm")
        }




    }

    private fun setrecycler()
    {

        data = databaseHandler.viewalarm() as ArrayList<Database>
        //Log.d("A","In View :${ data[2].id }")

        if(data.size > 0)
        {
            noalarm.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = Adapter(this,data)
            recycler.hasFixedSize()
            itemSwiper.attachToRecyclerView(recycler)
        }
        else
        {
            noalarm.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        }

    }

    private var simpleCallbackSwipper = object :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
        {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            var position = viewHolder.adapterPosition
            when(direction)
            {
                ItemTouchHelper.RIGHT -> deleteAlarm(data[position])
            }
        }

    }

    fun addalarm(datum:Database)
    {

        val id = databaseHandler.addalarm(datum)
        if( id > -1)
        {
            datum.id = id.toInt()
            data.add(datum)
            Log.d("Id","Id:$id")
            alarm.setAlarm(id.toInt(),datum.date_and_time!!) ///

            if(recycler.adapter == null)
            {
                setrecycler()
            }
            else
            {
                if (noalarm.visibility == View.VISIBLE)
                {
                    noalarm.visibility = View.GONE
                    recycler.visibility = View.VISIBLE
                    Log.d("A", "NOT empty recycler")
                }
            }


            Log.d("A","Last index : ${data.lastIndex}")
            recycler.adapter!!.notifyItemInserted(data.lastIndex)
            Log.d("A","Data${data}")

        }
        else
        {
            Toast.makeText(this,"Problem in adding Alarm",Toast.LENGTH_SHORT).show()
        }
    }



    private fun findIndex(datumid: Int):Int
    {
        try {
            data.forEach {
                if(it.id == datumid)
                    return data.indexOf(it)
            }
        }
        catch (e:Exception)
        {
            e.message?.let { Log.d("A", it) }
        }


    return -1
    }

    fun updateAlarm(datum: Database,position: Int)
    {
        databaseHandler.updatealarm(datum)
        alarm.setAlarm(datum.id,datum.date_and_time!!)
        data[position].apply {
            status = datum.status
            date_and_time = datum.date_and_time
        }

        try {
            recycler.post {
                recycler.adapter!!.notifyItemChanged(position)
            }
        }
        catch (e:IllegalStateException)
        {
            Log.d("A","In exception ${e.message.toString()}")
        }

    }

    fun updatealarminbroadcast(datum: Database)
    {
            Log.d("A","Before 1 ${datum.id}")
            val ind = findIndex(datum.id)
        Log.d("A","2 ${ind}")
            if(ind > -1)
            {
                data[ind].status=datum.status
                data[ind].date_and_time=datum.date_and_time
                recycler.adapter!!.notifyItemChanged(ind)
            }
            else
            {
                Log.d("A","Error")
            }


    }

    fun cancelAlarm(req:Database,position: Int):Int
    {
        var res = -1
        if(alarm.cancelAlarm(req.id) > -1)
        {
            Toast.makeText(this,"Alarm canceled",Toast.LENGTH_SHORT).show()
             res = databaseHandler.updatealarm(Database(req.id,req.date_and_time,"Canceled"))
            data[position].status="Canceled"
            Log.d("Id","ID:${req.id} :: ${data[position].id}")
           Log.d("A", "${databaseHandler.getOneValue(req.id)}")
        }

        return  res
    }

    fun deleteAlarm(req: Database)
    {
        var position = findIndex(req.id)
        if(req.status == "Not")
        {
            if (cancelAlarm(req, position) > -1)
            {
                databaseHandler.deletealarm(req)
            }
        }
        else
        {
            databaseHandler.deletealarm(req)

        }

        data.removeAt(position)
        recycler.adapter!!.notifyItemRemoved(position)

        if(data.size <= 0)
        {
            noalarm.visibility = View.VISIBLE
            recycler.visibility = View.GONE
            //itemSwiper.attachToRecyclerView(null)
        }
        recycler.adapter!!.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
       // recycler.adapter!!.notifyDataSetChanged()

        Log.d("A","ONstop")
    }



    override fun onDestroy() {
        super.onDestroy()
        val sharedPref = applicationContext.getSharedPreferences("com.jidesh.ji_alarm.shared",Context.MODE_PRIVATE)
        val edit =  sharedPref.edit()
        edit.putBoolean("IsAlive",false)
        edit.apply()
        Log.d("A","ONdestroy")
    }


}