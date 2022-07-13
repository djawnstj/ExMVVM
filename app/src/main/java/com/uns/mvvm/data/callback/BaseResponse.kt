package com.uns.mvvm.data.callback

interface BaseResponse<T> {

    fun onSuccess(data: T)

    fun onFailure(description: String)

    fun onError(throwable: Throwable)

    fun onLoading()

    fun endLoading()
}