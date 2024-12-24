package com.example.storyapp.ui.homepage

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.data.local.pref.UserPreferencesRepositoryImpl
import com.example.storyapp.data.local.room.entity.StoryEntity
import com.example.storyapp.databinding.ActivityHomepageBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.adapter.LoadingStateAdapter
import com.example.storyapp.ui.adapter.StoryListAdapter
import com.example.storyapp.ui.auth.LoginActivity
import com.example.storyapp.ui.auth.dataStore
import com.example.storyapp.ui.detail.DetailActivity
import com.example.storyapp.ui.map.MapActivity
import com.example.storyapp.ui.story.PostStoryActivity
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.viewmodel.MainViewModel
import com.example.storyapp.ui.viewmodel.MainViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePageActivity : AppCompatActivity() {
    private val preference = UserPreferencesRepositoryImpl.getInstance(dataStore)
    private lateinit var binding: ActivityHomepageBinding
    private lateinit var token: String
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setupAction()

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStories.addItemDecoration(itemDecoration)

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preference))[DataStoreViewModel::class.java]

        dataStoreViewModel.getToken().observe(this) {
            token = it
            setUserData(it)
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { view, i, keyEvent ->
                    when(i) {
                        EditorInfo.IME_ACTION_SEARCH -> {
                            searchBar.setText(searchView.text)
                            viewModel.getStories(searchView.text.toString())
                            searchView.hide()
                        }
                    }
                    false
                }
        }
    }

    private fun setupAction() {
        binding.btnAddStory.setOnClickListener {
            startActivity(Intent(this, PostStoryActivity::class.java))
        }

        binding.swipeRefresh.setOnRefreshListener {
            lifecycleScope.launch {
                viewModel.getStories(token)
                delay(1000)
                binding.swipeRefresh.isRefreshing = false
            }
        }

        binding.btnLogout.setOnClickListener {
            showAlertDialog()
        }

        binding.btnMap.setOnClickListener{
            startActivity(Intent(this, MapActivity::class.java))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setUserData(token: String) {

        val adapter = StoryListAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            })

        viewModel.getPagingStories(token).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : StoryListAdapter.OnItemClickCallback {
            override fun onItemClicked(data: StoryEntity) {
                selectedStory(data)
            }
        })
    }

    private fun selectedStory(data: StoryEntity) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_STORY, data)
        startActivity(intent)
    }

    private fun logoutStory() {
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(preference))[DataStoreViewModel::class.java]
        loginViewModel.clearDataLogin()
        Toast.makeText(this, R.string.logout_success, Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_confirmation))
            .setPositiveButton(getString(R.string.logout_cancel)) { _, _ ->
                alert.cancel()
            }
            .setNegativeButton(getString(R.string.logout)) { _, _ ->
                logoutStory()
            }
            .show()
    }

}