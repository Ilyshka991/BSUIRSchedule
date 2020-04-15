package com.pechuro.bsuirschedule.ext

import androidx.lifecycle.*
import com.pechuro.bsuirschedule.domain.common.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

internal class NonNullMediatorLiveData<T> : MediatorLiveData<T>()

internal fun <T> LiveData<T>.nonNull(): NonNullMediatorLiveData<T> {
    val mediator: NonNullMediatorLiveData<T> = NonNullMediatorLiveData()
    mediator.addSource(this) { it?.let { mediator.value = it } }
    return mediator
}

internal inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (t: T) -> Unit) {
    this.observe(owner, Observer { observer(it) })
}

internal inline fun <T> flowLiveData(crossinline block: suspend () -> Flow<T>): LiveData<T> = flow {
    emitAll(block().catch {
        Logger.e(it)
    })
}.asLiveData()