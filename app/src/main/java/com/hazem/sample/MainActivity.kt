package com.hazem.sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hazem.mvpbase.BaseActivity
import com.hazem.mvpbase.shortToast
import com.hazem.sample.testActivity.TestActivity
import com.hazem.sample.testActivity.TestPresenter
import com.hazem.sample.testActivity.TestView
import kotlinx.android.synthetic.main.activity_main.*

//import com.hazem.utilslib.libs.toast

class MainActivity : BaseActivity<TestPresenter>(), TestView {
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
            startActivity(Intent(this, TestActivity::class.java))
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
