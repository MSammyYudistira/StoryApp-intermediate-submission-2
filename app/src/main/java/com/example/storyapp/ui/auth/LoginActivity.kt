package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.pref.UserPreferencesRepositoryImpl
import com.example.storyapp.data.remote.dto.request.LoginRequest
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.homepage.HomePageActivity
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.viewmodel.MainViewModel
import com.example.storyapp.ui.viewmodel.MainViewModelFactory
import com.example.storyapp.ui.welcome.WelcomeActivity

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()

        val preferences = UserPreferencesRepositoryImpl.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]

        dataStoreViewModel.getLoginSession().observe(this) {
            if (it) {
                val intent = Intent(this@LoginActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        viewModel.message.observe(this) {
            responseLogin(
                it,
                dataStoreViewModel
            )
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            binding.edEmailLogin.clearFocus()
            binding.edPasswordLogin.clearFocus()

            if (isDataValid()) {
                val requestLogin = LoginRequest(
                    email = binding.edEmailLogin.text.toString().trim(),
                    password = binding.edPasswordLogin.text.toString().trim()
                )
                viewModel.login(requestLogin)
            } else {
                setMessage(this@LoginActivity, getString(R.string.error_login_input))
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.tvMessage, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlEmail, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlPassword, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }

    private fun responseLogin(
        message: String,
        dataStoreViewModel: DataStoreViewModel
    ) {
        if (message.contains("Hello")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val user = viewModel.userlogin.value
            dataStoreViewModel.saveLoginSession(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMessage(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun isDataValid(): Boolean {
        return binding.edEmailLogin.isEmailValid && binding.edPasswordLogin.isPasswordValid
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}