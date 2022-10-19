package com.dwarf.mystoryapp.adapter


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.databinding.ItemStoryBinding
import com.dwarf.mystoryapp.ui.storydetail.StoryDetailActivity
import com.dwarf.mystoryapp.ui.storydetail.StoryDetailActivity.Companion.EXTRA_STORY

class StoriesAdapter : PagingDataAdapter<StoryEntity, StoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(stories: StoryEntity?){
            binding.apply {
                Glide.with(itemView.context)
                    .load(stories?.photoUrl)
                    .into(ivItemStory)
                tvUserStory.text = stories?.name
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                    intent.putExtra(EXTRA_STORY, stories)
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(ivItemStory, "profile"),
                            Pair(tvUserStory, "name"),
                            Pair(vGradien, "customView"),
                        )
                    itemView.context.startActivity(intent,optionsCompat.toBundle())
                }

            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val stories = getItem(position)
        holder.bind(stories)
    }
    companion object{
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>(){
                override fun areItemsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem == newItem
                }

            }
    }



}