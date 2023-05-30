package com.personal_app.counter.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "counters")
data class Counter(
    val name:String,
    val count:Int,
    val tagsCount:Int,
    @PrimaryKey
    val id: Long = System.currentTimeMillis()
)
