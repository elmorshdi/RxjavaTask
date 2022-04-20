package com.elmorshdi.internTask.datasource.repository

import com.elmorshdi.internTask.datasource.model.ProductResponse
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.domain.model.UserData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response

interface MainRepository {
    fun getProducts(): Observable<Response<ProductResponse<ArrayList<Product>>>>
    fun postProduct(product: Product): Observable<Response<ProductResponse<ArrayList<Product>>>>
    fun deleteProduct(id: Int): Observable<Response<ProductResponse<ArrayList<Product>>>>
    fun login(email: String, password: String): Observable<Response<ProductResponse<UserData>>>
    fun getCashedProduct():Observable<List<Product>>
    fun deleteCashedProduct():Completable
    fun addProductToDB(products:  Product):Completable
}