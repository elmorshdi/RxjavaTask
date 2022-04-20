package com.elmorshdi.internTask.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
@Entity(tableName = "Product_table",)

@Parcelize
data class Product(
    @PrimaryKey
    @SerializedName("id")
    val id: Int?=null,
    @SerializedName("image")
    val image: String?=null,
    @SerializedName("name")
    val name: String?=null,
    @SerializedName("price")
    val price: Int? =null,
    @SerializedName("quantity")
    val quantity: Int? =null,
    @SerializedName("restaurant_id")
    val restaurant_id: Int?=null
): Parcelable