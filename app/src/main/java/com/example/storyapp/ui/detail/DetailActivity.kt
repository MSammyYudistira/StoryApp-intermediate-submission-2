package com.example.storyapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.entity.ListStoryDetail
import com.example.storyapp.databinding.ActivityDetailBinding
import com.example.storyapp.helper.LocationConverter

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryDetail>(EXTRA_STORY) as ListStoryDetail
        setupAction(story)

    }

    private fun setupAction(story: ListStoryDetail) {
        binding.apply {
            tvDetailName.text = "From: \n${story.name}"
            tvDetailDesc.text = "Description: \n${story.description}"
        }
        Glide.with(this)
            .load(story.photoUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.ivStoryDetailImage)

        binding.tvLocationAddress.text = LocationConverter.getStringAddress(
            LocationConverter.toLatlng(story.lat, story.lon),
            this
        )
    }


    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}