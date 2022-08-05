package com.example.ekart.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ProductDao {
    @Insert
    fun insertProduct(product: ProductModel)

    @Delete
    fun deleteProduct(product: ProductModel)

    @Query("Select * FROM products")
    fun getAllProducts(): LiveData<List<ProductModel>>

    @Query("SELECT * FROM products WHERE productId = :id")
    fun isExit(id: String): ProductModel
}