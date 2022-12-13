package com.dwarf.mystoryapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.dwarf.mystoryapp.adapter.StoriesAdapter
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.data.local.entity.UserEntity
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import com.dwarf.mystoryapp.utils.DataDummy
import com.dwarf.mystoryapp.utils.MainDispatcherRule
import com.dwarf.mystoryapp.utils.StoryPagingSource
import com.dwarf.mystoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var mainViewModel: MainViewModel
    private val dummyUserEntity = DataDummy.generateDummyUserEntity()
    private  val dummyStories = DataDummy.generateDummyStoriesEntity()
    private val dummyToken = "token1234567"

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(userPreferences, storyRepository)
    }

    @Test
    fun `when getUser return UserEntity`() = runTest {
        val expectedResponse: Flow<UserEntity> = flow {
            dummyUserEntity
        }
        `when`(userPreferences.getUser()).thenReturn(expectedResponse)
        mainViewModel.getUser().observeForever {
            assertEquals(
                dummyUserEntity,
                it
            )
        }
        Mockito.verify(userPreferences).getUser()
    }

    @Test
    fun `when want logout call logout`() = runTest {
        mainViewModel.logout()
        Mockito.verify(userPreferences).logout()
    }

    @Test
    fun `when get all stories should not null and return success`() = runTest {
        val data : PagingData<StoryEntity> = StoryPagingSource.snapshot(dummyStories)
        val expectedStory = MutableLiveData<PagingData<StoryEntity>>()
        expectedStory.value = data
        `when`(storyRepository.getAllStories(dummyToken)).thenReturn(expectedStory)
        val actualStory: PagingData<StoryEntity> = mainViewModel.getAllStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStories, differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0].id, differ.snapshot()[0]?.id)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
