package com.example.ekart.roomdb

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey
    @NonNull
    val productId: String,
    @ColumnInfo(name = "ProductName")
    val productName: String? = "",
    @ColumnInfo(name = "ProductImage")
    val productImage: String? = "",
    @ColumnInfo(name = "ProductSp")
    val productSp: String? = ""
)
