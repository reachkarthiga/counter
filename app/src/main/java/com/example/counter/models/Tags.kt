package com.example.counter.models

data class Tags(
    val tagName:String,
    val count:Int,
    val counterName:String,
    val id :Long = System.currentTimeMillis()
)
