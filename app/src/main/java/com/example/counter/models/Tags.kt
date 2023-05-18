package com.example.counter.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "tags")
data class Tags(
    val tagName:String,
    val count:Int,
    val counterName:String,
    @PrimaryKey
    val id :Long = System.currentTimeMillis()
)
