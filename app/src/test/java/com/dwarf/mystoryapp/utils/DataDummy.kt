package com.dwarf.mystoryapp.utils

import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.data.local.entity.UserEntity
import com.dwarf.mystoryapp.data.remote.response.*

object DataDummy {


    fun generateDummyUserEntity(): UserEntity {
        return UserEntity(
            "dummyId",
            "dummyName",
            "token1234567",
        )
    }

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginResult("dummyName", "dummyId", "dummyToken1234567"),
            false,
            "Success",
        )
    }

    fun generateDummySignupResponse(): SignupResponse {
        return SignupResponse(
            false,
            "Success"
        )
    }

    fun generateDummyStoriesEntity(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                "story-oW81QQSp68HGDYYP",
                "https://story-api.dicoding.dev/images/stories/photos-1666583567480_gv80_CqN.jpg",
                "2022-10-24T03:52:47.482Z",
                "test",
                "tes4",
                112.6985805,
                -7.8670628,
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyAddStoryResponse(): AddStoryResponse {
        return AddStoryResponse(
            false,
            "Success"
        )
    }

    fun generateDummyStoryResponse(): StoriesResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1666583567480_gv80_CqN.jpg",
                "2022-10-24T03:52:47.482Z",
                "test",
                "tes4",
                112.6985805,
                "story-oW81QQSp68HGDYYP",
                -7.8670628,
            )
            items.add(story)
        }
        return StoriesResponse(
            items,
            false,
            "Success"
        )
    }
}