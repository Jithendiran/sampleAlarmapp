package com.jidesh.ji_alarm.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Database(var id:Int, var date_and_time: String?, var status: String?):Parcelable