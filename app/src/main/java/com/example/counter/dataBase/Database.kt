package com.example.counter.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.counter.models.Counter
import com.example.counter.models.Tags


@Database(entities = [Counter::class, Tags::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract val counterDao: CounterDao
    abstract val tagDao: TagDao

    companion object {

        @Volatile
        private var INSTANCE: com.example.counter.dataBase.Database? = null

        fun getInstance(context: Context): com.example.counter.dataBase.Database {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {

                    instance = Room.databaseBuilder(
                        context,
                        com.example.counter.dataBase.Database::class.java,
                        "counters"
                    ).allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance

                }
                return instance
            }

        }

    }

}