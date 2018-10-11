package com.hazem.utilslib.core

import com.hazem.utilslib.core.BaseView


interface LoadingView : BaseView {
    fun onLoading(msg: String)
    fun onLoadingFailed(msg: String)
    fun finishLoad()
}