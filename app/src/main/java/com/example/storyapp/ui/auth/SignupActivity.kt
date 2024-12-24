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
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.remote.response.LoginData
import com.example.storyapp.data.remote.response.SignupData
import com.example.storyapp.databinding.ActivitySignUpBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.homepage.HomePageActivity
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.viewmodel.MainViewModel
import com.example.storyapp.ui.viewmodel.MainViewModelFactory

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

        val pref = UserPreference.getInstance(dataStore)
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
        binding.signupButton.setOnClickListener {
            binding.apply {
                edSignupName.clearFocus()
                edSignupEmail.clearFocus()
                edSignupPassword.clearFocus()
            }

            if (binding.edSignupName.isNameValid && binding.edSignupEmail.isEmailValid && binding.edSignupPassword.isPasswordValid) {
                val signupData = SignupData(
                    name = binding.edSignupName.text.toString().trim(),
                    email = binding.edSignupEmail.text.toString().trim(),
                    password = binding.edSignupPassword.text.toString().trim()
                )
                signupViewModel.signup(signupData)

            } else {
                if (!binding.edSignupName.isNameValid) binding.edSignupName.error =
                    resources.getString(R.string.nameNone)
                if (!binding.edSignupEmail.isEmailValid) binding.edSignupEmail.error =
                    resources.getString(R.string.emailNone)
                if (!binding.edSignupPassword.isPasswordValid) binding.edSignupPassword.error =
                    resources.getString(R.string.passwordNone)

                setMessage(this@SignupActivity, getString(R.string.error_login_input))
            }

        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f)
                .setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(100)
        val signup =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


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
            Toast.makeText(
                this,
                resources.getString(R.string.accountSuccessCreated),
                Toast.LENGTH_SHORT
            ).show()
            val userLogin = LoginData(
                binding.edSignupEmail.text.toString(),
                binding.edSignupPassword.text.toString()
            )
            loginViewModel.login(userLogin)
        } else {
            if (message.contains("The email you entered is already registered, please change your email.")) {
                binding.edSignupEmail.setErrorMessage(
                    resources.getString(R.string.email_taken),
                    binding.edSignupEmail.text.toString()
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
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}
