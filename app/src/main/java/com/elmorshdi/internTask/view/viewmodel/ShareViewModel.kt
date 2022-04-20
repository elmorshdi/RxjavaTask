package com.elmorshdi.internTask.view.viewmodel

import android.content.SharedPreferences
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elmorshdi.internTask.datasource.model.ProductResponse
import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.domain.model.UserData
import com.elmorshdi.internTask.domain.repository.Repository
import com.elmorshdi.internTask.helper.isEmailValid
import com.elmorshdi.internTask.helper.isValidPassword
import com.elmorshdi.internTask.view.util.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.net.HttpURLConnection.*
import javax.inject.Inject


@HiltViewModel
class ShareViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val _uiStateFlow = MutableLiveData<UiState>(UiState.Empty)
    val uiStateFlow: LiveData<UiState> = _uiStateFlow
    val productsList: LiveData<List<Product>>
        get() = _productsList
    private val _productsList: MutableLiveData<List<Product>> = MutableLiveData()
    private var compositeDisposable = CompositeDisposable()
    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed)
            compositeDisposable.dispose()
    }
    fun addProduct(product: Product) {
        val observable: Observable<Product> = Observable.just(product)
        observable.subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Product> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: Product) {
                    repository.addProductToDB(product)
                }

                override fun onError(e: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onComplete() {
                    TODO("Not yet implemented")
                }

            })
    }
    private fun Observable<Response<ProductResponse<ArrayList<Product>>>>.observe() {
        _uiStateFlow.value = UiState.Loading
        this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object :
                DisposableObserver<Response<ProductResponse<ArrayList<Product>>>>() {
                override fun onNext(response: Response<ProductResponse<ArrayList<Product>>>) {
                    println("response:" + response.code())
                    when (response.code()) {
                        200 -> {
                            _uiStateFlow.value = UiState.Success
                            if (response.body()?.data != null) {
                                val productsList = response.body()?.data!!
                                _productsList.postValue(productsList)
                                repository.deleteCashedProduct()
                                productsList.forEach {
                                   addProduct(it)
                                }
                             // get count of table rows
                            }
                        }
                        // not found
                        HTTP_NOT_FOUND -> _uiStateFlow.value = UiState.NetworkError("Not Found")
                        // access denied
                        HTTP_FORBIDDEN -> _uiStateFlow.value = UiState.NetworkError("AccessDenied")
                        // unavailable service
                        HTTP_UNAVAILABLE -> _uiStateFlow.value =
                            UiState.NetworkError("ServiceUnavailable")
                        //Server ERROR
                        HTTP_INTERNAL_ERROR -> _uiStateFlow.value =
                            UiState.NetworkError("Server Error try later ")
                        HTTP_CLIENT_TIMEOUT -> {
                            _uiStateFlow.value =
                                UiState.NetworkError("Request Timeout Check your connection")
                        }
                        // all the others will be treated as unknown error
                        else -> {
                            _uiStateFlow.value =
                                UiState.NetworkError(getError(response.errorBody()!!))
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    _uiStateFlow.value = UiState.NetworkError(e.message!!)
                }

                override fun onComplete() {}
            })
            .also { compositeDisposable.add(it) }
    }

    //to add product
    fun postProduct(name: String, price: String, quantity: String) {
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

                repository.postProduct(product).observe()


            }
        }
    }

    //to delete product
    fun deleteProduct(id: Int) {
        _uiStateFlow.value = UiState.Loading
        repository.deleteProduct(id).observe()

    }

    // to login and get(token ,user name )
    fun login(email: String, password: String) {
        when {
            !email.isEmailValid() || email.isEmpty() -> {
                _uiStateFlow.value = UiState.ValidationError(Error.EmailNotValid)
            }
            !password.isValidPassword() || password.isEmpty() -> {
                _uiStateFlow.value = UiState.ValidationError(Error.PasswordNotValid)
            }
            else -> {
                _uiStateFlow.value = UiState.Loading
                val observable = repository.login(email, password)
                observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object :
                        DisposableObserver<Response<ProductResponse<UserData>>>() {
                        override fun onNext(it: Response<ProductResponse<UserData>>) {
                            val result = it.body()
                            if (it.isSuccessful && result != null) {
                                val token = it.body()?.token!!
                                val name = it.body()?.data?.name!!
                                _uiStateFlow.value = UiState.Success
                                SharedPreferencesManager.signInShared(
                                    sharedPreferences.edit(),
                                    token,
                                    name
                                )
                                _uiStateFlow.value = UiState.Success
                            } else {
                                _uiStateFlow.value =
                                    UiState.NetworkError(getError(it.errorBody()!!))
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
    fun getProducts() {
        _uiStateFlow.value = UiState.Loading
        repository.getProducts().observe()


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
        data class ValidationError(val error: Error) : UiState()
        object Loading : UiState()
        object Empty : UiState()
    }

    //To get error message from JsonBody
    private fun getError(response: ResponseBody): String {
        val jObjError = JSONObject(response.string())
        return jObjError.getString("message")
    }


    fun getCashedData() {
        println("t.toString()")

        _uiStateFlow.value = UiState.Loading
        repository.getCashedProduct().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object :
                DisposableObserver<List<Product>>() {
                override fun onNext(t: List<Product>) {
                    _uiStateFlow.postValue(UiState.Success)
                    _productsList.postValue(t)
                    println(t.toString())

                }

                override fun onError(e: Throwable) {
                    _uiStateFlow.value = UiState.NetworkError(e.message!!)

                }

                override fun onComplete() {
                }

            })
    }

}