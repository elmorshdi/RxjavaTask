package com.elmorshdi.internTask.datasource.roomdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.elmorshdi.internTask.domain.model.Product

@Database(
    entities = [Product::class], // Tell the database the entries will hold data of this type
    version = 1
)
abstract class  MyDataBase : RoomDatabase() {

        abstract fun getDao(): ProductDao
    }