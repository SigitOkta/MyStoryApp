package com.dwarf.mystoryapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Binder
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.dwarf.mystoryapp.R
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.data.local.room.StoryDao
import com.dwarf.mystoryapp.data.local.room.StoryDatabase
import kotlinx.coroutines.*

internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val mWidgetItems = ArrayList<Bitmap>()
    private var listStory = listOf<StoryEntity>()
    private lateinit var dao: StoryDao

    override fun onCreate() {
        dao = StoryDatabase.getInstance(mContext).storyDao()
    }

    override fun onDataSetChanged() {
        fetchDataDB()
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, mWidgetItems[position])

        val extras = bundleOf(
            ImageBannerWidget.EXTRA_ITEM to position
        )
        val fillIntent = Intent()
        fillIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView , fillIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    private fun fetchDataDB() {
        val identityToken = Binder.clearCallingIdentity()
        runBlocking(Dispatchers.IO){
            mWidgetItems.clear()
            try {
                listStory = dao.getAllStoryAsList()
                for (i in listStory.indices){
                    val bitmap = try {
                        Glide.with(mContext)
                            .asBitmap()
                            .load(listStory[i].photoUrl)
                            .submit()
                            .get()
                    }catch (e:Exception){
                        BitmapFactory.decodeResource(mContext.resources,R.drawable.ic_broken_image)
                    }
                    mWidgetItems.add(bitmap)
                }
                Log.d("story", listStory.toString())
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        Binder.restoreCallingIdentity(identityToken)
    }

}