package com.pechuro.bsuirschedule.feature.splash

import android.content.Intent
import android.os.Bundle
import com.pechuro.bsuirschedule.R
import com.pechuro.bsuirschedule.common.BaseActivity
import com.pechuro.bsuirschedule.feature.load.InfoLoadActivity

class SplashActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_splash

    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        initViewModel(SplashActivityViewModel::class)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val intent = decideNextActivityIntent()
            startActivity(intent)
            finish()
        }
    }

    private fun decideNextActivityIntent(): Intent = if (viewModel.isInfoLoaded()) {
    //    NavigationActivity.newIntent(this)
        InfoLoadActivity.newIntent(this)
    } else {
        InfoLoadActivity.newIntent(this)
    }
}
