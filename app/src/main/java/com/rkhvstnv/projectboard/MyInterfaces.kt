package com.rkhvstnv.projectboard

import com.rkhvstnv.projectboard.fragments.BaseFragment
import com.rkhvstnv.projectboard.models.UserDataClass

interface MyCallBack {
    fun onCallbackSuccess(any: Any)
    fun onCallbackErrorMessage(message: String)
}
