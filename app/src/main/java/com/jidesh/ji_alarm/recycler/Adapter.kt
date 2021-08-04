package com.jidesh.ji_alarm.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils.getActivity
import com.jidesh.ji_alarm.MainActivity
import com.jidesh.ji_alarm.R
import com.jidesh.ji_alarm.calander.Calander
import com.jidesh.ji_alarm.customdialogue.CustomDialogueUpdate
import com.jidesh.ji_alarm.database.Database
import java.util.*


class Adapter(private var context: Context,private var data:ArrayList<Database>):RecyclerView.Adapter<Adapter.Viewholder>(),ActionMode.Callback
{

    // true if the user in selection mode, false otherwise
    private var multiSelect = false
    // Keeps track of all the selected images
    private val selectedItems = arrayListOf<Database>()

    inner class Viewholder(view:View):RecyclerView.ViewHolder(view)
    {

        var hours:TextView = view.findViewById(R.id.hours)
        var minute:TextView = view.findViewById(R.id.minute)
        var date:TextView = view.findViewById(R.id.date)
        var session:TextView = view.findViewById(R.id.session)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        var toggleswitch: Switch = view.findViewById(R.id.toggleswitch)
        var layout:RelativeLayout = view.findViewById(R.id.alarm)
       // var checkBox: CheckBox = view.findViewById(R.id.checkbox)

        init {
            Log.d("A","in view holder notify")
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder
    {

        return  Viewholder(
                LayoutInflater.from(context).inflate(R.layout.adapter, parent, false)
            )




    }

    @SuppressLint("SetTextI18n", "RestrictedApi")
    override fun onBindViewHolder(holder: Viewholder, position: Int)
    {

        val calendar  = Calander()
        val dateandtime:Database = data[position]
        var date:Date = calendar.getDateFromString(dateandtime.date_and_time!!)//
        val hour = DateFormat.format("kk",date) as String
        val minute = DateFormat.format("mm",date) as String
        when(dateandtime.status)
        {
            "Not"->holder.toggleswitch.isChecked=true
            "Finished"->holder.toggleswitch.isChecked=false
            "Canceled"->holder.toggleswitch.isChecked=false
        }

        var hoursint = hour.toInt()
        if(hoursint >= 12 )
        {

            hoursint -=  12

            if(hoursint == 0)
            {
                holder.hours.text = "12"

            }
            else if(hoursint < 10)
            {
                holder.hours.text = "0${hoursint}"
            }
            else
            {
                holder.hours.text = (hoursint).toString()
            }
            holder.session.text = "Pm"
        }
        else
        {
            if (hoursint == 0)
            {
                holder.hours.text = "12"
            }
            else
            {
                holder.hours.text = hour
            }
            holder.session.text = "Am"
        }
//***************
       /* if(multiSelect)
        {

            holder.checkBox.visibility = View.VISIBLE

        }
        else
        {
            holder.checkBox.visibility = View.GONE
        }*/
//********************
        holder.minute.text = minute


        holder.date.text = calendar.getDateString(date)

        holder.toggleswitch.setOnClickListener{

            if(holder.toggleswitch.isChecked)
            {
                holder.toggleswitch.isChecked=true
                date = calendar.getDateFromString(dateandtime.date_and_time!!)
               if(calendar.toCalendar(date).timeInMillis - calendar.getCurrentDate().time < 0)
               {
                    val c = Calendar.getInstance()
                    c.time = date
                    c.add(Calendar.DATE, 1)
                    Log.d("Time","${c.time}")
                    Log.d("Next Alarm","${c.time.time - calendar.getCurrentDate().time}")
                    val datestring = calendar.sqlDateFormat(c.time)
                   (context as MainActivity).updateAlarm( Database(dateandtime.id,datestring,"Not"),position)
                    date = calendar.getDateFromString(calendar.sqlDateFormat(c.time))
                    holder.date.text = calendar.getDateString(date)
               }
               else
                {
                    // < 0 code
                    (context as MainActivity).updateAlarm( Database(dateandtime.id,dateandtime.date_and_time,"Not"),position)

               }
                Log.d("R","Remainng time${calendar.toCalendar(date).timeInMillis - calendar.getCurrentDate().time}")
                Log.d("Current time","${calendar.getCurrentDate().time} : ${System.currentTimeMillis()}")



            }
            else
            {
                // code to un set alarm
                Log.d("A","In Adapter Status${holder.toggleswitch.isChecked}")
                holder.toggleswitch.isChecked=false
               Log.d("A","In Adapter${dateandtime}")
                (context as MainActivity).cancelAlarm(dateandtime,position)

            }
        }

        holder.layout.setOnClickListener{
            if (multiSelect) {
                Log.d("I","$position")
                selectItem(holder, dateandtime)
                //holder.checkBox.visibility=View.VISIBLE
                //setCheckBox(holder,true,true)
            }
                else
                  CustomDialogueUpdate(dateandtime,position).show((context as FragmentActivity).supportFragmentManager,"Update")
        }

    ///*******************

        if (selectedItems.contains(dateandtime)) {
            // if the item is selected, let the user know by adding a dark layer above it
            holder.layout.alpha = 0.3f
//            if(multiSelect) {
//               // holder.checkBox.visibility = View.VISIBLE
//                setCheckBox(holder,true)
//            }
        } else {
            // else, keep it as it is
            holder.layout.alpha = 1.0f
//            if (multiSelect)
//                setCheckBox(holder,false)
        }

        holder.layout.setOnLongClickListener {
            // if multiSelect is false, set it to true and select the item
            if (!multiSelect) {
                // We have started multi selection, so set the flag to true
                multiSelect = true
                // As soon as the user starts multi-select process, show the contextual menu
                //getActivity().startSupportActionMode(this)
                getActivity(context)?.startActionMode(this)
                Log.d("A","Before notify")

               // notifyDataSetChanged() /// this data set changed is main cause to visible check box in all item
                Log.d("A","After notify")
                // Add it to the list containing all the selected images
                selectItem(holder,dateandtime)
                Log.d("I","$position")
                //setCheckBox(holder,true,true)


                true
            }
            else {

                false
            }
        }

    }

    override fun getItemCount(): Int
    {

        return data.size
    }


//******************
    // Called when the menu is created i.e. when the user starts multi-select mode (inflate your menu xml here)

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean
    {
        val inflater: MenuInflater? = mode?.menuInflater
        inflater?.inflate(R.menu.menu, menu)
        return true
    }
    // Called to refresh an action mode's action menu (we won't be using this here)

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean
    {
      return true
    }
    // Called when a menu item was clicked
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean
    {
        if (item?.itemId == R.id.action_delete) {
            // Delete button is clicked, handle the deletion and finish the multi select process
            for (i in selectedItems)
            {
                Log.d("A","Item to remove : $i")

                    (context as MainActivity).deleteAlarm(i)

            }
            //notifyDataSetChanged()
            mode?.finish()
            Toast.makeText(context, "Selected Alarm deleted", Toast.LENGTH_SHORT).show()

        }
        return true
    }
    // Called when the Context ActionBar disappears i.e. when the user leaves multi-select mode
    override fun onDestroyActionMode(mode: ActionMode?)
    {
        // finished multi selection
        multiSelect = false
        selectedItems.clear()
        notifyDataSetChanged()
    }

private fun selectItem(holder: Viewholder,data: Database)
{
    // If the "selectedItems" list contains the item, remove it and set it's state to normal

    if (selectedItems.contains(data)) {
        selectedItems.remove(data)
        holder.layout.alpha = 1.0f
        //setCheckBox(holder,false)
    } else {
        // Else, add it to the list and add a darker shade over the image, letting the user know that it was selected
        selectedItems.add(data)
        holder.layout.alpha = 0.3f
        //setCheckBox(holder,true)
    }
}
    /*private fun setCheckBox(holder: Viewholder,set_status:Boolean)
    {

            holder.checkBox.isChecked = set_status
            Log.d("A","IN SET_CHK:$set_status")

    }*/
}