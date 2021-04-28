package com.rkhvstnv.projectboard

import com.rkhvstnv.projectboard.fragments.BaseFragment

interface MyCallBack {
    fun onCallbackObject(userData: UserDataClass)
    fun onCallbackErrorMessage(message: String)
}

interface OnFragmentResult {
    fun onResult(fragment: BaseFragment, boolean: Boolean)
}