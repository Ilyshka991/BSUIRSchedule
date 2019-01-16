package com.pechuro.bsuirschedule.ui.fragment.itemoptions

import com.pechuro.bsuirschedule.di.annotations.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface ItemOptionsDialogProvider {

    @FragmentScope
    @ContributesAndroidInjector()
    fun bind(): ItemOptionsDialog
}
