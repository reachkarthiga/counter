package com.personal_app.counter.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tags(
    val tagName:String,
    val count:Int,
    val counterName:String,
    @PrimaryKey
    val id :Long = System.currentTimeMillis()
)
