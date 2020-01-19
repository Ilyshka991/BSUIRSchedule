package com.pechuro.bsuirschedule.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import kotlin.reflect.KClass

abstract class BaseDialog : DialogFragment(), HasSupportFragmentInjector {

    @Inject
    protected lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    protected lateinit var viewModelFactory: ViewModelProvider.Factory

    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        performDI()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(layoutId, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun supportFragmentInjector() = fragmentDispatchingAndroidInjector

    protected fun <T : BaseViewModel> initViewModel(clazz: KClass<T>, shared: Boolean = false): T {
        val owner: ViewModelStoreOwner = if (shared) requireActivity() else this
        return initViewModel(clazz, owner)
    }

    protected fun <T : BaseViewModel> initViewModel(clazz: KClass<T>, owner: ViewModelStoreOwner): T {
        return ViewModelProvider(owner, viewModelFactory).get(clazz.java)
    }

    private fun performDI() = AndroidSupportInjection.inject(this)
}