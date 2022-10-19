package com.dwarf.mystoryapp.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dwarf.mystoryapp.data.local.entity.StoryEntity


@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllStory(storyList: List<StoryEntity>)

    @Query("DELETE FROM story")
    fun deleteAll()

    @Query("SELECT * FROM story")
    fun getAllStoryAsList(): List<StoryEntity>
}