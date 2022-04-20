package com.elmorshdi.internTask.domain.repository

import com.elmorshdi.internTask.datasource.model.ProductResponse
import com.elmorshdi.internTask.datasource.network.ApiService
import com.elmorshdi.internTask.datasource.repository.MainRepository
import com.elmorshdi.internTask.datasource.roomdatabase.ProductDao
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.domain.model.UserData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import javax.inject.Inject


class Repository @Inject constructor(
    private val apiService: ApiService,
    private val dao: ProductDao
) : MainRepository {

    override fun getProducts(): Observable<Response<ProductResponse<ArrayList<Product>>>> {
        return apiService.getProducts()
    }

    override fun postProduct(product: Product): Observable<Response<ProductResponse<ArrayList<Product>>>> {
        return apiService.addProducts(product)
    }

    override fun deleteProduct(id: Int): Observable<Response<ProductResponse<ArrayList<Product>>>> {
        return apiService.deleteProduct(id)
    }

    override fun login(
        email: String,
        password: String
    ): Observable<Response<ProductResponse<UserData>>> {
        return apiService.login(email, password)
    }

    override fun getCashedProduct(): Observable<List<Product>> {
    return dao.getAll()
    }

    override fun deleteCashedProduct(): Completable {
        return dao.deleteAll()
    }

    override fun addProductToDB(products: Product): Completable {
        return dao.insert(products)
    }


}


