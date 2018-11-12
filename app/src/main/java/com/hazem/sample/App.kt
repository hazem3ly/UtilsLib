package com.hazem.sample

import android.app.Application
import android.content.Context
import com.hazem.mvpbase.MyContextWrapper

class App : Application() {

    companion object {
        var lang = "en"
    }

    override fun attachBaseContext(base: Context?) {
        if (base == null) super.attachBaseContext(this)
        else {
            val lang = MyContextWrapper.getCurrentLanguage(base)
            super.attachBaseContext(MyContextWrapper.wrap(base,lang))
        }
    }

}