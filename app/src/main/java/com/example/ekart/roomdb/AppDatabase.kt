package com.example.ekart.roomdb

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProductModel::class], version = 1)
abstract class AppDatabase: RoomDatabase(){

    companion object{
        private var database: AppDatabase? = null
        private val DATABASE_NAME = "ekart"

        @Synchronized
        fun getInstance(context: android.content.Context): AppDatabase{
            if(database==null){
                database = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration().build()
            }
            return database!!
        }
    }

    abstract fun productDao(): ProductDao
}