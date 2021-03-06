package com.elmorshdi.internTask.datasource.model

import com.elmorshdi.internTask.domain.model.Product
import com.elmorshdi.internTask.domain.model.UserData

data class ProductResponse<T>(
    var data :T? = null,
    var status: Boolean? = null,
    var count: Int? = null,
    var message: String? = null,
     var token: String?=null
  )