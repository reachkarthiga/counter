package com.personal_app.counter.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.personal_app.counter.models.Counter
import com.personal_app.counter.models.Tags


@Database(entities = [Counter::class, Tags::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {

    abstract val counterDao: CounterDao
    abstract val tagDao: TagDao

    companion object {

        @Volatile
        private var INSTANCE: com.personal_app.counter.dataBase.Database? = null

        fun getInstance(context: Context): com.personal_app.counter.dataBase.Database {

            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {

                    instance = Room.databaseBuilder(
                        context,
                        com.personal_app.counter.dataBase.Database::class.java,
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