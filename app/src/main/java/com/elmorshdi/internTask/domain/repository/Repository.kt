package com.elmorshdi.internTask.domain.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.elmorshdi.internTask.datasource.model.ProductResponse
import com.elmorshdi.internTask.datasource.model.UserResponse
import com.elmorshdi.internTask.datasource.network.ApiService
import com.elmorshdi.internTask.datasource.repository.MainRepository
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.helper.Constant.BASE_URL
import com.elmorshdi.internTask.view.util.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class Repository @Inject constructor(private val apiService: ApiService) : MainRepository {

    override fun getProducts(): Observable<Response<ProductResponse>> {
        return apiService.getProducts()
    }

    override fun postProduct(product: Product): Observable<Response<ProductResponse>> {
        return apiService.addProducts(product)
    }

    override fun deleteProduct(id: Int): Observable<Response<ProductResponse>> {
        return apiService.deleteProduct(id)
    }

    override fun login(email: String, password: String): Observable<Response<UserResponse>> {
        return apiService.login(email,password)
    }


}


