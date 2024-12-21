package com.example.storyapp.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.ui.ViewModelFactory
import com.example.storyapp.ui.auth.dataStore
import com.example.storyapp.ui.homepage.HomePageActivity
import com.example.storyapp.ui.viewmodel.DataStoreViewModel
import com.example.storyapp.ui.welcome.WelcomeActivity

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        loginViewModel.getLoginSession().observe(this) { isLoggedIn ->

            val intent = Intent(
                this,
                if (isLoggedIn) HomePageActivity::class.java else WelcomeActivity::class.java
            )

            binding.imageView.animate()
                .setDuration(1000)
                .alpha(1f)
                .withEndAction {
                    startActivity(intent)
                    finish()
                }
        }
    }
}