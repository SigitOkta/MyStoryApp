package com.dwarf.mystoryapp.ui.storydetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dwarf.mystoryapp.R
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_STORY = "extra_story"
    }
    private lateinit var binding: ActivityStoryDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        val data = intent.getParcelableExtra<StoryEntity>(EXTRA_STORY)
        setData(data)
    }

    private fun setData(data: StoryEntity?) {
        if (data != null){
            binding.apply {
                val stringData = getString(R.string.capture_by)
                val value = "$stringData ${data.name}"
                tvUserStory.text = value
                tvDesc.text = data.description

                Glide.with(this@StoryDetailActivity)
                    .load(data.photoUrl)
                    .into(ivImageStory)
            }
        }
    }
}

