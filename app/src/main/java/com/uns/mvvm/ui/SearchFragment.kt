package com.uns.mvvm.ui

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.uns.mvvm.R
import com.uns.mvvm.data.AppData
import com.uns.mvvm.data.AppData.Companion.showToast
import com.uns.mvvm.data.callback.BaseResponse
import com.uns.mvvm.data.model.RepoModel
import com.uns.mvvm.data.repository.RepoRepositoryImpl
import com.uns.mvvm.data.viewModel.RepoViewModel
import com.uns.mvvm.databinding.FragmentSearchBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable

class SearchFragment: Fragment() {

    companion object { private const val TAG = "SearchFragment" }

    private val repoViewModel by lazy { RepoViewModel(repoRepositoryImpl, compositeDisposable) }

    private val repoRepositoryImpl = RepoRepositoryImpl()
    private val compositeDisposable = CompositeDisposable()

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.model = repoViewModel

        initEditText()
        initObserve()

        return binding.root
    }

    private fun initEditText() {
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    val user = v?.text.toString()
                    repoViewModel.reloadRepoList(requireContext(), user)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun initObserve() {
        repoViewModel.items.addOnPropertyChangedCallback(object: Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                repoViewModel.items.get()?.let {
//                    repoAdapter.setItems(it)
                }
            }
        })
    }

    override fun onDestroy() {

        compositeDisposable.dispose()

        super.onDestroy()
    }

}