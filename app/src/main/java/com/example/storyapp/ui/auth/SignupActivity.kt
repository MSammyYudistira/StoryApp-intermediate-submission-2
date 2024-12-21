package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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

        supportActionBar?.title = resources.getString(R.string.create_account)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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
                val name = binding.edSignupName.text.toString().trim()
                val email = binding.edSignupEmail.text.toString().trim()
                val password = binding.edSignupPassword.text.toString().trim()

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(
                        password
                    )
                ) {
                    setMessage(this@SignupActivity, getString(R.string.error_signup_input))
                } else {

                    AlertDialog.Builder(this@SignupActivity).apply {
                        setTitle("Yeah!")
                        setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
                        setPositiveButton("Lanjut") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                    val signupData = SignupData(name, email, password)
                    signupViewModel.register(signupData)
                }
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
        if (message == "Yeay akun berhasil dibuat") {
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
            if (message.contains("Email yang anda masukan sudah terdaftar")) {
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
