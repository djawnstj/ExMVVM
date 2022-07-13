package com.uns.mvvm.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.uns.mvvm.R
import com.uns.mvvm.data.AppData.Companion.showToast
import com.uns.mvvm.data.api.GitClient
import com.uns.mvvm.data.viewModel.RepoViewModel
import com.uns.mvvm.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    companion object { private const val TAG = "MainActivity" }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()

    }

    private fun initView() {

        supportFragmentManager.beginTransaction().replace(binding.container.id, SearchFragment()).commit()

    }


}