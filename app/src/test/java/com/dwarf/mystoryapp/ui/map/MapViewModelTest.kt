package com.dwarf.mystoryapp.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.local.entity.StoryEntity
import com.dwarf.mystoryapp.data.local.entity.UserEntity
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import com.dwarf.mystoryapp.utils.DataDummy
import com.dwarf.mystoryapp.utils.MainDispatcherRule
import com.dwarf.mystoryapp.utils.getOrAwaitValue
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MapViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var mapViewModel: MapViewModel
    private val dummyUserEntity = DataDummy.generateDummyUserEntity()
    private val dummyStoryEntity = DataDummy.generateDummyStoriesEntity()
    private val dummyToken = "token1234567"


    @Before
    fun setUp(){
        mapViewModel = MapViewModel(userPreferences, storyRepository)
    }

    @Test
    fun `when getUser return UserEntity`() = runTest {
        val expectedResponse: Flow<UserEntity> = flow {
            dummyUserEntity
        }
        `when`(userPreferences.getUser()).thenReturn(expectedResponse)
        mapViewModel.getUser().observeForever {
            assertEquals(
                dummyUserEntity,
                it
            )
        }
        Mockito.verify(userPreferences).getUser()
    }

    @Test
    fun `when get all stories with location(1) should not null and return ResultSuccess`(){
        val expectedResponse = MutableLiveData<Result<List<StoryEntity>>>()
        expectedResponse.value = Result.Success(dummyStoryEntity)
        `when`(storyRepository.getAllStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)
        val actualResponse = mapViewModel.getAllStoriesWithLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepository).getAllStoriesWithLocation(dummyToken)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
        assertEquals(dummyStoryEntity.size, (actualResponse as Result.Success).data.size)
    }

    @Test
    fun `when get all stories with location(1) Should Return Error`() {
        val expectedResponse = MutableLiveData<Result<List<StoryEntity>>>()
        expectedResponse.value = Result.Error("Error")
        `when`(storyRepository.getAllStoriesWithLocation(dummyToken)).thenReturn(expectedResponse)
        val actualResponse = mapViewModel.getAllStoriesWithLocation(dummyToken).getOrAwaitValue()
        Mockito.verify(storyRepository).getAllStoriesWithLocation(dummyToken)
        assertTrue(actualResponse is Result.Error)
    }
}