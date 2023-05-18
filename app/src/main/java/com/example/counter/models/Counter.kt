package com.example.counter.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "counters")
data class Counter(
    val name:String,
    val count:Int,
    val tagsCount:Int,
    @PrimaryKey
    val id: Long = System.currentTimeMillis()
)
