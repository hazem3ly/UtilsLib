package com.hazem.mvpbase

import com.hazem.mvpbase.BaseView


interface LoadingView : BaseView {
    fun onLoading(msg: String)
    fun onLoadingFailed(msg: String)
    fun finishLoad()
}