package com.example.counter.dataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.counter.models.Counter
import com.example.counter.models.Tags


@Dao
interface  CounterDao {

    @Insert
    suspend fun insertCounter(counter: Counter)

    @Delete
    suspend fun deleteCounter(counter: Counter)

    @Update
    suspend fun updateCounter(counter: Counter)

    @Query("SELECT * FROM counters")
    fun getCounters() :LiveData<List<Counter>>

    @Query("SELECT * FROM counters where name = :counterName")
    fun getCounterByName(counterName:String) : LiveData<Counter>

}

@Dao
interface  TagDao {

    @Insert
    suspend fun insertTag(tags: Tags)

    @Delete
    suspend fun deleteTag(tags: Tags)

    @Update
    suspend fun updateTags(tags: Tags)

    @Query("SELECT * FROM tags where counterName = :counterName")
    fun getTagsByName(counterName :String) :LiveData<List<Tags>>

}