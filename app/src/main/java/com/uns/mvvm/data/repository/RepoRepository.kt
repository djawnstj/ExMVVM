package com.uns.mvvm.data.repository

import com.uns.mvvm.data.callback.BaseResponse
import com.uns.mvvm.data.model.RepoModel
import io.reactivex.rxjava3.disposables.Disposable

interface RepoRepository {

    fun postRepoList(user: String, callback: BaseResponse<List<RepoModel>>): Disposable

}