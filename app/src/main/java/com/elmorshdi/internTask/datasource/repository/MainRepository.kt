package com.elmorshdi.internTask.datasource.repository

import com.elmorshdi.internTask.datasource.model.ProductResponse
import com.elmorshdi.internTask.datasource.model.UserResponse
import com.elmorshdi.internTask.domain.model.Product
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response

interface MainRepository {
      fun getProducts(): Observable<Response<ProductResponse>>
      fun postProduct(product: Product):Observable<Response<ProductResponse>>
      fun deleteProduct(id:Int): Observable<Response<ProductResponse>>
      fun login(email :String,password :String):Observable<Response<UserResponse>>
}