package com.example.storyapp.unit.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.data.contract.RemoteDataRepository
import com.example.storyapp.data.local.room.entity.StoryEntity
import com.example.storyapp.ui.viewmodel.MainViewModel
import com.example.storyapp.utils.DataDummy
import com.example.storyapp.utils.MainDispatcherRule
import com.example.storyapp.utils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val mockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var remoteDataRepository: RemoteDataRepository

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        mainViewModel = MainViewModel(remoteDataRepository)
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `when successfully load story should return data`() = runTest {
        val dummyToken = "dummy-token"
        val dummyPagingData = PagingData.from(DataDummy.generateDummyNewsEntity())

        // Mocking repository methods
        `when`(remoteDataRepository.getPagingStories(dummyToken)).thenReturn(MutableLiveData(dummyPagingData))


        // Act
        val actualStories = mainViewModel.getPagingStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        // Assert
        assertNotNull(differ.snapshot())
        assertEquals(DataDummy.generateDummyNewsEntity().size, differ.snapshot().size)
        assertEquals(DataDummy.generateDummyNewsEntity()[0], differ.snapshot()[0])
    }

    @OptIn(ExperimentalPagingApi::class)
    @Test
    fun `when failed load story should return empty list`() = runTest {
        val dummyToken = "dummy-token"
        val dummyPagingData = PagingData.from(emptyList<StoryEntity>())

        // Mocking repository methods to return empty data
        `when`(remoteDataRepository.getPagingStories(dummyToken)).thenReturn(MutableLiveData(dummyPagingData))

        // Act
        val actualStories = mainViewModel.getPagingStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = NoopListCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        // Assert that the snapshot contains no items
        Assert.assertEquals(0, differ.snapshot().size)
    }

    // DiffCallback for comparing ListStoryItem (or DetailStoryList)
    class StoryDiffCallback : DiffUtil.ItemCallback<StoryEntity>() {
        override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem.name == newItem.name // Adjust based on actual fields
        }

        override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
            return oldItem == newItem
        }
    }

    // No-op ListUpdateCallback for AsyncPagingDataDiffer
    class NoopListCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}
