package com.elmorshdi.internTask.datasource.repository

interface MainRepository {
      fun getProducts()
      fun postProduct(name: String, price: String, quantity: String)
      fun deleteProduct(id:Int)
      fun login(email :String,password :String)
}