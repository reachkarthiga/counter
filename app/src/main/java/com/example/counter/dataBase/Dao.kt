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

    @Query("UPDATE counters set count = :count where name = :counterName")
    fun updateCount(count:Int , counterName: String)

    @Query("UPDATE counters set tagsCount = (tagsCount+1) where name = :counterName")
    fun increaseTagCount(counterName: String)

    @Query("SELECT * FROM counters")
    fun getCounters() :LiveData<List<Counter>>

    @Query("SELECT * FROM counters where name = :counterName")
    fun getCounterByName(counterName:String) : LiveData<Counter>

    @Query("DELETE FROM counters where name = :counterName")
    fun clearTempCounter(counterName:String)

    @Query("SELECT count FROM counters where name = :counterName")
    fun getCountValue(counterName:String) :Int

}

@Dao
interface  TagDao {

    @Insert
    suspend fun insertTag(tags: Tags)

    @Delete
    suspend fun deleteTag(tags: Tags)

    @Update
    suspend fun updateTags(tags: Tags)

    @Query("Update tags set counterName = :newCounterName where counterName = :oldCounterName")
    suspend fun updateTags(oldCounterName: String , newCounterName :String)

    @Query("SELECT count(*) from tags where counterName = :counterName")
    suspend fun getTagsCount(counterName :String) :Int

    @Query("SELECT case when (select count(*) from tags where count = :count and counterName = :counterName) > 1  then (cast (1 as bit) ) else (cast (0 as bit)) end  ")
    fun checkAvailability(count:Int , counterName: String) :Boolean

    @Query("SELECT * FROM tags where counterName = :counterName")
    fun getTagsByName(counterName :String) :LiveData<List<Tags>>

    @Query("DELETE FROM tags where counterName = :counterName")
    fun clearTempTags(counterName :String)

}