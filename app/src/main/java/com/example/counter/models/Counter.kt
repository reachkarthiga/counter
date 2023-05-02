package com.example.counter.models

data class Counter(
    val name:String,
    val count:Int,
    val tagsCount:Int,
    val id: Long = System.currentTimeMillis()
)
