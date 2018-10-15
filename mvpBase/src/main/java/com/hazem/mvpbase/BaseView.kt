package com.hazem.mvpbase

import android.app.Activity
import android.content.Context

interface BaseView {
    fun showMessage(msg: String)
    fun getCurrentContext(): Context
    fun getCurrentActivity(): Activity
}