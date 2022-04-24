package com.elmorshdi.internTask.datasource.roomdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elmorshdi.internTask.domain.model.Product
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.util.*
@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: Product): Completable
    @Query("DELETE FROM product_table")
    fun deleteAll():Completable
    @Query("SELECT * FROM product_table ")
    fun getAll(): Observable<List<Product>>

}
