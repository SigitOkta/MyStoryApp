package com.dwarf.mystoryapp.ui.addstory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dwarf.mystoryapp.data.Result
import com.dwarf.mystoryapp.data.local.datastore.UserPreferences
import com.dwarf.mystoryapp.data.local.entity.UserEntity
import com.dwarf.mystoryapp.data.remote.response.AddStoryResponse
import com.dwarf.mystoryapp.data.repositorty.StoryRepository
import com.dwarf.mystoryapp.utils.DataDummy
import com.dwarf.mystoryapp.utils.MainDispatcherRule
import com.dwarf.mystoryapp.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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
class AddStoryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var addStoryViewModel: AddStoryViewModel
    private val dummyUserEntity = DataDummy.generateDummyUserEntity()
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

    @Before
    fun setUp() {
        addStoryViewModel = AddStoryViewModel(userPreferences, storyRepository)
    }

    @Test
    fun `when getUser return UserEntity`() = runTest {
        val expectedResponse: Flow<UserEntity> = flow {
            dummyUserEntity
        }
        `when`(userPreferences.getUser()).thenReturn(expectedResponse)
        addStoryViewModel.getUser().observeForever {
            assertEquals(
                dummyUserEntity,
                it
            )
        }
        Mockito.verify(userPreferences).getUser()
    }

    @Test
    fun `when do addStory should not null and return Result Success`() {
        val expectedResponse = MutableLiveData<Result<AddStoryResponse>>()
        expectedResponse.value = Result.Success(dummyAddStoryResponse)
        `when`(
            storyRepository.addNewStory(
                dummyToken,
                dummyDescription,
                dummyFile,
                dummyLat,
                dummyLon
            )
        ).thenReturn(expectedResponse)
        val actualResponse = addStoryViewModel.addNewStory(dummyToken, dummyDescription, dummyFile, dummyLat, dummyLon).getOrAwaitValue()
        Mockito.verify(storyRepository).addNewStory(dummyToken, dummyDescription, dummyFile, dummyLat, dummyLon)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is Result.Success)
    }
    @Test
    fun `when do addStory and Return Error`() {
        val expectedResponse = MutableLiveData<Result<AddStoryResponse>>()
        expectedResponse.value = Result.Error("Error")
        `when`(
            storyRepository.addNewStory(
                dummyToken,
                dummyDescription,
                dummyFile,
                dummyLat,
                dummyLon
            )
        ).thenReturn(expectedResponse)
        val actualResponse = addStoryViewModel.addNewStory(dummyToken, dummyDescription, dummyFile, dummyLat, dummyLon).getOrAwaitValue()
        Mockito.verify(storyRepository).addNewStory(dummyToken, dummyDescription, dummyFile, dummyLat, dummyLon)
        assertTrue(actualResponse is Result.Error)
    }
}