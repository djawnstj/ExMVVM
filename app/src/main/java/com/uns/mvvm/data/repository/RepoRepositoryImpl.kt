package com.uns.mvvm.data.repository

import com.uns.mvvm.data.api.GitClient
import com.uns.mvvm.data.callback.BaseResponse
import com.uns.mvvm.data.model.RepoModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import retrofit2.HttpException

class RepoRepositoryImpl : RepoRepository {

    override fun postRepoList(user: String, callback: BaseResponse<List<RepoModel>>): Disposable {
        return(
                GitClient.api.postRepoList(user)
                // 이 이후에 실행되는 코드는 메인스레드에서 실행
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { callback.onLoading() }           // 구독할 때 수행될 작업
                    .doOnTerminate { callback.endLoading() }        // 스트림이 종료될 때 수행될 작업 (성공과 에러)
                    .subscribe({ callback.onSuccess(it) })    // observable 구독
                    {
                        if(it is HttpException) callback.onFailure(it.message())
                        else callback.onError(it)
                    }
            )
    }

}