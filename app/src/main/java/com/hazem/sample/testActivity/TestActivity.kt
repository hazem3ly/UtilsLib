package com.hazem.sample.testActivity

import com.hazem.mvpbase.BaseActivity
import com.hazem.mvpbase.MyContextWrapper
import com.hazem.mvpbase.shortToast
import com.hazem.sample.R
import kotlinx.android.synthetic.main.activity_main.*

class TestActivity : BaseActivity<TestPresenter>(), TestView {
    override fun onNetworkChanged(isConnected: Boolean?) {
//        shortToast("onNetworkChanged")
    }

    override fun loadResourceLayout(): Int {
        return R.layout.activity_main
    }

    override fun initPresenter(): TestPresenter {
        return TestPresenter(this)
    }

    override fun initViews() {
        setTitle(R.string.app_name)


        button.setOnClickListener {
            shortToast("clicked")
            val lang = getSavedLanguage(this)
            if (lang == "ar") {
                MyContextWrapper.changeAppLanguageAndRestart(this, "en")
            } else {
                MyContextWrapper.changeAppLanguageAndRestart(this, "ar")
            }
        }
    }

    override fun onMenuItemClicked(itemId: Int) {
    }

    override fun showMessage(msg: String) {
    }

    override fun onLoading(msg: String) {
    }

    override fun onLoadingFailed(msg: String) {
    }

    override fun finishLoad() {
    }
}