package com.elmorshdi.internTask.datasource.model

import com.elmorshdi.internTask.domain.model.UserData

data class UserResponse(
    var data: UserData?=null,
    var message: String?=null,
    var status: Boolean?=null,
    var token: String?=null
)