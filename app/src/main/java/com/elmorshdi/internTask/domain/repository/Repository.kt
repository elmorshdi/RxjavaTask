package com.elmorshdi.internTask.domain.repository

import com.elmorshdi.internTask.datasource.model.ProductResponse
import com.elmorshdi.internTask.datasource.model.UserResponse
import com.elmorshdi.internTask.datasource.network.ApiService
import com.elmorshdi.internTask.datasource.repository.MainRepository
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.helper.Constant.BASE_URL
import com.elmorshdi.internTask.view.util.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class Repository @Inject constructor(private val apiService: ApiService)  {}
//    override fun getProducts(): Resource<ProductResponse> {
//        return try {
//            val observable = apiService.getProducts()
//            handleResponse(observable)
//        } catch (e: Exception) {
//            Resource.Error(e.message ?: "An error Occurred")
//        }
//    }
//
//    override fun addProducts(product: Product): Resource<ProductResponse> {
//        return try {
//            val observable = apiService.addProducts(product)
//            handleResponse(observable)
//        } catch (e: Exception) {
//            Resource.Error(e.message ?: "An error Occurred")
//        }
//    }
//
//    override fun deleteProduct(id: Int): Resource<ProductResponse> {
//        return try {
//            val observable = apiService.deleteProduct(id)
//            handleResponse(observable)
//        } catch (e: Exception) {
//            Resource.Error(e.message ?: "An error Occurred")
//        }
//    }
//    override fun login(email: String, password: String): Resource<UserResponse> {
//            val observable = apiService.login(email, password)
//
//        observable.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object :DisposableObserver<Response<UserResponse>>(){
//
//                override fun onNext(it: Response<UserResponse>) {
//                    when (it.code()) {
//                        200 -> {
//
//                            result = Resource.Success(it.body()!!)
//                        }
//                        422, 502, 401, 400 ,500-> {
//                            result = Resource.Error(it.errorBody()?.getError()!!)
//                        }
//                    }
//                }
//                override fun onError(e: Throwable) {
//                    result = Resource.Error(e.message!!)
//
//                }
//                override fun onComplete() {
//
//                }
//            })
//        return result
//
//
//    }
//    private fun handleResponse(observable: Observable<Response<ProductResponse>>): Resource<ProductResponse> {
//        var result: Resource<ProductResponse> = Resource.Loading()
//
//        observable.subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : DisposableObserver<Response<ProductResponse>>() {
//
//                override fun onNext(it: Response<ProductResponse>) {
//                    when (it.code()) {
//                        200 -> {
//
//                            result = Resource.Success(it.body()!!)
//                        }
//                        422, 502, 401, 400 ,500-> {
//                            result = Resource.Error(it.errorBody()?.getError()!!)
//                        }
//                    }
//                }
//                override fun onError(e: Throwable) {
//                    result = Resource.Error(e.message!!)
//
//                }
//                override fun onComplete() {
//                }
//            })
//        return result
//    }
//
//    private fun internetIsConnected(): Boolean {
//        return try {
//            val command = "ping -c 1 $BASE_URL"
//            Runtime.getRuntime().exec(command).waitFor() == 0
//        } catch (e: java.lang.Exception) {
//            false
//        }
//    }
//}


