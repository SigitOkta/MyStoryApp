package com.dwarf.mystoryapp.data.repositorty

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dwarf.mystoryapp.adapter.StoriesAdapter
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.data.remote.response.AddStoryResponse
import com.dwarf.mystoryapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private val dummyStoryEntity = DataDummy.generateDummyStoriesEntity()
    private val dummyAddStoryResponse = DataDummy.generateDummyAddStoryResponse()
    private val dummyToken = "token1234567"
    private val dummyDescription = "tesDescription".toRequestBody()
    private val dummyLat = 0.1.toString().toRequestBody()
    private val dummyLon = 0.1.toString().toRequestBody()
    private val requestImageFile = "image/jpeg".toRequestBody()
    private val dummyFile: MultipartBody.Part = MultipartBody.Part.createFormData(
        "photo",
        "File",
        requestImageFile
    )



    @Test
    fun `when getAllStories With Location Should Not Null and return success`() = runTest {
        val expectedResponse = MutableLiveData<Result<List<StoryEntity>>>()
        expectedResponse.value = Result.Success(dummyStoryEntity)
        Mockito.`when`(storyRepository.getAllStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)
        val actualResponse = storyRepository.getAllStoriesWithLocation(dummyToken).getOrAwaitValue()
        Assert.assertNotNull(actualResponse)
        assertEquals(
            dummyStoryEntity,
            (actualResponse as Result.Success).data
        )
    }

    @Test
    fun `when add new story not null and should return success`() = runTest {
        val expectedResponse = MutableLiveData<Result<AddStoryResponse>>()
        expectedResponse.value = Result.Success(dummyAddStoryResponse)
        Mockito.`when`(
            storyRepository.addNewStory(
                dummyToken,
                dummyDescription,
                dummyFile,
                dummyLat,
                dummyLon
            )
        ).thenReturn(expectedResponse)
        val actualResponse = storyRepository.addNewStory(dummyToken,dummyDescription,dummyFile,dummyLat,dummyLon).getOrAwaitValue()
        Assert.assertNotNull(actualResponse)
        assertEquals(
            dummyAddStoryResponse,
            (actualResponse as Result.Success).data
        )
    }

    @Test
    fun `when get All stories without location(0) should not null and return success`() = runTest{
        val data : PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStoryEntity)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getAllStories(dummyToken)).thenReturn(expectedStory)
        val actualStory = storyRepository.getAllStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStoryEntity, differ.snapshot())
        assertEquals(dummyStoryEntity.size, differ.snapshot().size)
        assertEquals(dummyStoryEntity[0].id, differ.snapshot()[0]?.id)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}