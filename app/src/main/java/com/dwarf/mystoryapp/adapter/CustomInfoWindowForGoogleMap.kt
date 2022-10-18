package com.dwarf.mystoryapp.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dwarf.mystoryapp.R
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.databinding.CustomInfoMapBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowForGoogleMap(private val context: Context) : GoogleMap.InfoWindowAdapter {

    private val binding = CustomInfoMapBinding.inflate((context as Activity).layoutInflater)
    private fun customMapWindow(p0: Marker) {
        val marker = p0.tag as StoryEntity
        Glide.with(context.applicationContext)
            .asBitmap()
            .load(marker.photoUrl)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean,
                ): Boolean = false

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (p0.isInfoWindowShown) {
                            p0.showInfoWindow()
                        }
                    }, 100)
                    return false
                }
            })
            .into(binding.ivInfoStory)
        binding.tvInfoName.text = context.getString(R.string.capture_by, marker.name)
    }

    override fun getInfoContents(p0: Marker): View {
        customMapWindow(p0)
        return binding.root
    }

    override fun getInfoWindow(p0: Marker): View? = null

}