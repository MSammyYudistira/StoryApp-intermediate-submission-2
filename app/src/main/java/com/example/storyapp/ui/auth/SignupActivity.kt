package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.pref.UserPreferencesRepositoryImpl
import com.example.storyapp.data.remote.dto.request.LoginRequest
import com.example.storyapp.data.remote.dto.request.SignUpRequest
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.homepage.HomePageActivity
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.viewmodel.MainViewModel
import com.example.storyapp.ui.viewmodel.MainViewModelFactory
import com.example.storyapp.ui.welcome.WelcomeActivity

class SignupActivity : AppCompatActivity() {

    private val loginViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val signupViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferencesRepositoryImpl.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this@SignupActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        signupViewModel.message.observe(this) { messageRegist ->
            signupResponse(
                messageRegist
            )
        }

        signupViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.message.observe(this) { messageLogin ->
            loginResponse(
                messageLogin,
                dataStoreViewModel
            )
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }


        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            binding.apply {
                edFullNameSignup.clearFocus()
                edEmailSignup.clearFocus()
                edPasswordSignup.clearFocus()
            }

            if (binding.edFullNameSignup.isNameValid && binding.edEmailSignup.isEmailValid && binding.edPasswordSignup.isPasswordValid) {
                val signupData = SignUpRequest(
                    name = binding.edFullNameSignup.text.toString().trim(),
                    email = binding.edEmailSignup.text.toString().trim(),
                    password = binding.edPasswordSignup.text.toString().trim()
                )
                signupViewModel.signup(signupData)

            } else {
                if (!binding.edFullNameSignup.isNameValid) binding.edFullNameSignup.error =
                    resources.getString(R.string.name_empty)
                if (!binding.edEmailSignup.isEmailValid) binding.edEmailSignup.error =
                    resources.getString(R.string.email_empty)
                if (!binding.edPasswordSignup.isPasswordValid) binding.edPasswordSignup.error =
                    resources.getString(R.string.password_empty)

                setMessage(this@SignupActivity, getString(R.string.error_login_input))
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivSignup, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title =
            ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.tvFullName, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlFullName, View.ALPHA, 1f)
                .setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlEmail, View.ALPHA, 1f)
                .setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f)
                .setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.tlPassword, View.ALPHA, 1f)
                .setDuration(100)
        val signup =
            ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()

    }

    private fun loginResponse(
        message: String,
        dataStoreViewModel: DataStoreViewModel
    ) {
        if (message.contains("Hello")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val user = loginViewModel.userlogin.value
            dataStoreViewModel.saveLoginSession(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun signupResponse(
        message: String,
    ) {
        if (message == "Account Created Successfully") {
            val userLogin = LoginRequest(
                binding.edEmailSignup.text.toString(),
                binding.edPasswordSignup.text.toString()
            )
            loginViewModel.login(userLogin)
        } else {
            if (message.contains("The email you entered is already registered, please change your email.")) {
                binding.edEmailSignup.setErrorMessage(
                    resources.getString(R.string.email_taken),
                    binding.edEmailSignup.text.toString()
                )
                Toast.makeText(this, resources.getString(R.string.email_taken), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setMessage(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbSignup.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
