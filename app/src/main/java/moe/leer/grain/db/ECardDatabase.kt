package moe.leer.grain.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import moe.leer.grain.model.ECard

/**
 *
 * Created by leer on 1/22/19.
 * Copyright (c) 2017 LeeReindeer
 * https://github.com/LeeReindeer
 */
@Database(
    entities = [ECard::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class ECardDatabase : RoomDatabase() {
    val TAG = "ECardDatabase"

    abstract fun eCardDao(): ECardDao

    companion object {

        @Volatile
        private var INSTANCE: ECardDatabase? = null

        fun getInstance(context: Context): ECardDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDB(context).also { INSTANCE = it }
            }

        private fun buildDB(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ECardDatabase::class.java, "card.db"
            ).build()
    }

}