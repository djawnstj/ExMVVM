package com.uns.mvvm.data.viewModel

import android.content.Context
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.uns.mvvm.data.AppData
import com.uns.mvvm.data.AppData.Companion.showToast
import com.uns.mvvm.data.callback.BaseResponse
import com.uns.mvvm.data.model.RepoModel
import com.uns.mvvm.data.repository.RepoRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable

class RepoViewModel(private val repoRepository: RepoRepository,
                    private val compositeDisposable: CompositeDisposable): ViewModel() {

    companion object { private const val TAG = "RepoViewModel" }

    //로딩 필드
    val isLoading = ObservableField(8)
    val loadingMessage = ObservableField("")
    //검색 버튼 활성 필드
    val enableSearchButton = ObservableField(false)
    //에러 메시지 필드
    val errorMessage = ObservableField("")
    //검색 입력 필드
    val editSearchText = ObservableField("")
    //리포지터리 아이템 필드
    val items = ObservableField<List<RepoModel>>(emptyList())

    init {
        editSearchText.addOnPropertyChangedCallback(object: OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                //사용자가 입력한 값을 관찰하고 있어 실시간으로 데이터를 받아올 수 있습니다.
                val user = editSearchText.get()
                AppData.debug(TAG, "입력받은 값 : $user")
                if (user.isNullOrEmpty()) {
                    enableSearchButton.set(false)
                } else {
                    enableSearchButton.set(true)
                }
            }
        })
    }

    fun reloadRepoList(context: Context, user: String) = postRepoList(context, user)

    private fun postRepoList(context: Context, user: String) {
        AppData.debug(TAG, "postRepoList($user) called.")

        if (user.isEmpty()) {
            context.showToast("쿼리가 비었대")
        } else {
            repoRepository.postRepoList(user, object: BaseResponse<List<RepoModel>> {
                override fun onSuccess(data: List<RepoModel>) {
                    AppData.debug(TAG,"onSuccess!!")
                    items.set(data)
                }

                override fun onFailure(description: String) {
                    showErrorMessage(description)
                }

                override fun onError(throwable: Throwable) {
                    showErrorMessage(throwable.message ?: "error")
                }

                override fun onLoading() {
                    clearItems()
                    showLoading("$user 정보를 조회합니다.")
                    hideErrorMessage()
                }

                override fun endLoading() {
                    hideLoading()
                }

            }).also { compositeDisposable.add(it) }
        }
    }

    private fun clearItems() {
        items.set(emptyList())
    }

    private fun showLoading(msg: String) {
        isLoading.set(0)
        loadingMessage.set(msg)
    }

    private fun hideLoading() {
        isLoading.set(8)
    }

    private fun showErrorMessage(error: String) {
        errorMessage.set(error)
    }

    private fun hideErrorMessage() {
        errorMessage.set("")
    }

}