package com.dwarf.mystoryapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dwarf.mystoryapp.data.local.entity.StoryEntity


@Dao
interface StoryDao {
    @Query("SELECT * FROM story")
    fun getAllStory(): LiveData<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllStory(storyList: List<StoryEntity>)

    @Query("DELETE FROM story")
    fun deleteAll()

    @Query("SELECT * FROM story")
    fun getAllStoryAsList(): List<StoryEntity>
}