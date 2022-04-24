package com.elmorshdi.internTask.datasource.network

import com.elmorshdi.internTask.datasource.model.ProductResponse
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.domain.model.UserData
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @GET("products")
    fun getProducts(): Observable<Response<ProductResponse<ArrayList<Product>>>>

    @POST("products")
    fun addProducts(@Body product: Product): Observable<Response<ProductResponse<ArrayList<Product>>>>

    @POST("login")
    fun login(
        @Query("email") email: String,
        @Query("password") password: String
    ): Observable<Response<ProductResponse<UserData>>>

    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") id: Int?): Observable<Response<ProductResponse<ArrayList<Product>>>>

}