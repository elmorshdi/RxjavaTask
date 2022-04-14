package com.elmorshdi.internTask.view.viewmodel

import android.content.SharedPreferences
import android.view.View
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.elmorshdi.internTask.datasource.model.ProductResponse
import com.elmorshdi.internTask.datasource.model.UserResponse
import com.elmorshdi.internTask.datasource.network.ApiService
import com.elmorshdi.internTask.datasource.repository.MainRepository
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.helper.Constant.BASE_URL
import com.elmorshdi.internTask.helper.isEmailValid
import com.elmorshdi.internTask.helper.isValidPassword
import com.elmorshdi.internTask.view.ui.fragment.MainFragmentDirections
import com.elmorshdi.internTask.view.util.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val apiService: ApiService
) : MainRepository,
    ViewModel() {
    private val _uiStateFlow = MutableLiveData<UiState>(UiState.Empty)
    val uiStateFlow: LiveData<UiState> = _uiStateFlow


    val productsList: LiveData<List<Product>>
        get() = _productsList
    private val _productsList: MutableLiveData<List<Product>> = MutableLiveData()

    //to add product
    override fun postProduct(name: String, price: String, quantity: String) {
        when {
            name.isEmpty() || name.isDigitsOnly() -> _uiStateFlow.value =
                UiState.ValidationError(Error.NameNotValid)
            price.isEmpty() || price.toInt() == 0 -> _uiStateFlow.value =
                UiState.ValidationError(Error.PriceNotValid)
            quantity.isEmpty() || quantity.toInt() == 0 -> _uiStateFlow.value =
                UiState.ValidationError(Error.QuantityNotValid)
            else -> {
                val product = Product(
                    name = name,
                    price = price.toInt(),
                    quantity = quantity.toInt()
                )
                try {
                    val observable = apiService.addProducts(product)
                    observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableObserver<Response<ProductResponse>>() {
                            override fun onNext(it: Response<ProductResponse>) {
                                when (it.code()) {
                                    200 -> {
                                        _uiStateFlow.value = UiState.Success
                                    }
                                    422, 502, 401, 400, 500 -> {
                                        try {
                                            _uiStateFlow.value =
                                                UiState.NetworkError(getError(it.errorBody()!!))
                                        } catch (e: java.lang.Exception) {
                                            _uiStateFlow.value = UiState.NetworkError(e.message!!)
                                        }
                                    }

                                }
                            }

                            override fun onError(e: Throwable) {
                                UiState.NetworkError(e.message!!)
                            }

                            override fun onComplete() {
                            }
                        })
                } catch (e: Exception) {
                    if (!internetIsConnected()) {
                        _uiStateFlow.value = UiState.NetworkError("Check your Internet Connection")

                    } else {
                        _uiStateFlow.value = UiState.NetworkError("An error Occurred")

                    }
                }

            }
        }
    }

    //to delete product
    override fun deleteProduct(id: Int) {
        _uiStateFlow.value = UiState.Loading
        val observable = apiService.deleteProduct(id)
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<ProductResponse>>() {
                override fun onNext(it: Response<ProductResponse>) {
                    when (it.code()) {
                        200 -> {
                            _uiStateFlow.value = UiState.Success
                        }
                        422, 502, 401, 400, 500 -> {
                            _uiStateFlow.value =
                                UiState.NetworkError(it.body()?.message!!)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    _uiStateFlow.value = UiState.NetworkError(e.message!!)
                }

                override fun onComplete() {
                }
            })
    }

    // to login and get(token ,user name )
    override fun login(email: String, password: String) {
        when {
            !email.isEmailValid() || email.isEmpty() -> {
                _uiStateFlow.value = UiState.ValidationError(Error.EmailNotValid)
            }
            !password.isValidPassword() || password.isEmpty() -> {
                _uiStateFlow.value = UiState.ValidationError(Error.PasswordNotValid)
            }
            else -> {
                _uiStateFlow.value = UiState.Loading
                val observable = apiService.login(email, password)
                observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<Response<UserResponse>>() {
                        override fun onNext(it: Response<UserResponse>) {
                            when (it.code()) {
                                200 -> {
                                    val token = it.body()?.token!!
                                    val name = it.body()?.data?.name!!
                                    _uiStateFlow.value = UiState.Success
                                    SharedPreferencesManager.signInShared(
                                        sharedPreferences.edit(),
                                        token,
                                        name
                                    )
                                }
                                422, 502, 401, 400, 500 -> {
                                    _uiStateFlow.value =
                                        UiState.NetworkError(it.body()?.message!!)
                                }
                            }
                        }

                        override fun onError(e: Throwable) {
                            _uiStateFlow.value = UiState.NetworkError(e.message!!)
                        }

                        override fun onComplete() {
                        }
                    })
            }

        }
    }

    // to get product list
    override fun getProducts() {
        _uiStateFlow.value = UiState.Loading
        val observable = apiService.getProducts()
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Response<ProductResponse>>() {
                override fun onNext(it: Response<ProductResponse>) {
                    when (it.code()) {
                        200 -> {
                            val result = it.body()

                            val productsList = result?.data!!
                            _productsList.postValue(productsList)
                            _uiStateFlow.value = UiState.Success
                        }
                        422, 502, 401, 400, 500 -> {
                            _uiStateFlow.value =
                                UiState.NetworkError(it.body()?.message!!)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    _uiStateFlow.value = UiState.NetworkError(e.message!!)
                }

                override fun onComplete() {
                }
            })
    }


    fun addProduct(view: View) {
        val action = MainFragmentDirections.actionMainFragmentToAddItemFragment()
        view.findNavController().navigate(action)
    }


    sealed class Error {
        object PasswordNotValid : Error()
        object EmailNotValid : Error()
        object NameNotValid : Error()
        object PriceNotValid : Error()
        object QuantityNotValid : Error()
    }

    sealed class UiState {
        data class NetworkError(val errorMessage: String) : UiState()
        object Success : UiState()
        data class ValidationError(val error: ShareViewModel.Error) : UiState()
        object Loading : UiState()
        object Empty : UiState()
    }

    //To get error message from JsonBody
    private fun getError(response: ResponseBody): String {
        val jObjError = JSONObject(response.string())
        return jObjError.getString("message")
    }

    private fun internetIsConnected(): Boolean {
        return try {
            val command = "ping -c 1 $BASE_URL"
            Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: java.lang.Exception) {
            false
        }
    }
}