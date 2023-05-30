package com.personal_app.counter.dataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.personal_app.counter.models.Counter
import com.personal_app.counter.models.Tags


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
    fun getCounterByName(counterName:String) : Counter

    @Query("SELECT count FROM counters where name = :counterName")
    fun getCountValue(counterName:String) :Int

    @Query("UPDATE counters set count = :count where name = :counterName")
    fun setCountValue(count:Int , counterName:String)

    @Query("DELETE from counters")
    fun deleteAllCounters()

}

@Dao
interface  TagDao {

    @Insert
    suspend fun insertTag(tags: Tags)

    @Update
    suspend fun updateTag(tags: Tags)

    @Delete
    suspend fun deleteTag(tags: Tags)

    @Query("Update tags set counterName = :newCounterName where counterName = :oldCounterName")
    suspend fun updateTags(oldCounterName: String , newCounterName :String)

    @Query("SELECT count(*) from tags where counterName = :counterName")
    suspend fun getTagsCount(counterName :String) :Int

    @Query("SELECT * FROM tags where counterName = :counterName order by count desc")
    fun getTagsByName(counterName :String) :LiveData<List<Tags>>

    @Query("DELETE FROM tags where counterName = :counterName")
    fun clearTags(counterName :String)

    @Query("DELETE from tags")
    fun deleteAllTags()

    @Query("DELETE from tags where counterName = :name")
    fun deleteTagByCounter(name: String)

    @Query("UPDATE tags  set counterName = :newName where counterName = :oldName")
    fun updateAllTagsByCounter(oldName: String, newName: String)

}