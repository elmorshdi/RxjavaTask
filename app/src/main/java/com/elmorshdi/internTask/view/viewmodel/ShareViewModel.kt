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
import com.elmorshdi.internTask.view.util.UiState
import com.elmorshdi.internTask.view.util.getNetworkError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Response
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

    //to add product
    fun postProduct(name: String, price: String, quantity: String) {
        when {
            name.isEmpty() || name.isDigitsOnly() -> _uiStateFlow.value =
                UiState.ValidationError(com.elmorshdi.internTask.view.util.Error.NameNotValid)
            price.isEmpty() || price.toInt() == 0 -> _uiStateFlow.value =
                UiState.ValidationError(com.elmorshdi.internTask.view.util.Error.PriceNotValid)
            quantity.isEmpty() || quantity.toInt() == 0 -> _uiStateFlow.value =
                UiState.ValidationError(com.elmorshdi.internTask.view.util.Error.QuantityNotValid)
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

    // to get product list
    fun getProducts() {
        _uiStateFlow.value = UiState.Loading
        repository.getProducts().observe()
    }

    // to login and get(token ,user name )
    fun login(email: String, password: String) {
        when {
            !email.isEmailValid() || email.isEmpty() -> {
                _uiStateFlow.value =
                    UiState.ValidationError(com.elmorshdi.internTask.view.util.Error.EmailNotValid)
            }
            !password.isValidPassword() || password.isEmpty() -> {
                _uiStateFlow.value =
                    UiState.ValidationError(com.elmorshdi.internTask.view.util.Error.PasswordNotValid)
            }
            else -> {
                _uiStateFlow.value = UiState.Loading
                val observable = repository.login(email, password)
                observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object :
                        DisposableObserver<Response<ProductResponse<UserData>>>() {
                        override fun onNext(it: Response<ProductResponse<UserData>>) {
                            if (it.code() == 200) {
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
                                _uiStateFlow.value = getNetworkError(it.code(), it.errorBody()!!)
                            }
                        }

                        override fun onError(e: Throwable) {
                            _uiStateFlow.value = UiState.NetworkError(e.message!!)
                        }

                        override fun onComplete() {
                        }
                    }).let { composite ->
                        compositeDisposable.add(composite)
                    }
            }

        }
    }

    //------------------------------CashedData---------------------------------------------
    fun getCashedData() {
        _uiStateFlow.value = UiState.Loading
        repository.getCashedProduct().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<List<Product>>() {
                override fun onNext(t: List<Product>) {
                    if (t.isNotEmpty()) {
                        _uiStateFlow.postValue(UiState.Success)
                        _productsList.postValue(t)
                    } else _uiStateFlow.postValue(UiState.NetworkError("Connect To Internet"))
                }

                override fun onError(e: Throwable) {
                    _uiStateFlow.value = UiState.NetworkError(e.message!!)
                }

                override fun onComplete() {}
            }).let { composite ->
                compositeDisposable.add(composite)
            }
    }

    private fun deleteCashed() {
        repository.deleteCashedProduct()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {

            }).let { composite ->
                compositeDisposable.add(composite)
            }


    }

    private fun addProductToDB(productsList: ArrayList<Product>) {

        productsList.forEach { product ->
            repository.addProductToDB(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {

                }).let { composite ->
                    compositeDisposable.add(composite)
                }

        }
    }

    //------------------------------CashedData---------------------------------------------
    private fun Observable<Response<ProductResponse<ArrayList<Product>>>>.observe() {
        _uiStateFlow.value = UiState.Loading
        this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object :
                DisposableObserver<Response<ProductResponse<ArrayList<Product>>>>() {
                override fun onNext(response: Response<ProductResponse<ArrayList<Product>>>) {
                    if (response.code() == 200) {
                        _uiStateFlow.value = UiState.Success
                        if (response.body()?.data != null) {
                            val productsList = response.body()?.data!!
                            _productsList.postValue(productsList)
                            repository.deleteCashedProduct()
                            deleteCashed()
                            addProductToDB(productsList)
                        }
                    } else
                        _uiStateFlow.value =
                            getNetworkError(response.code(), response.errorBody()!!)
                }

                override fun onError(e: Throwable) {
                    _uiStateFlow.value = UiState.NetworkError(e.message!!)
                }

                override fun onComplete() {}
            })
            .also { compositeDisposable.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        if (!compositeDisposable.isDisposed)
            compositeDisposable.dispose()
    }

}