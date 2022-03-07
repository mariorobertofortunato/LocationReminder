package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValueTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.nullValue
import org.hamcrest.core.Is
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {
    private lateinit var listViewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        stopKoin()
        val remindersList = mutableListOf<ReminderDTO>(
            ReminderDTO("testTitle", "testDesc","testLocation",30.00,31.00),
            ReminderDTO("testTitle", "testDesc","testLocation",30.00,31.00),
            ReminderDTO("testTitle", "testDesc","testLocation",30.00,31.30)
        )

        dataSource = FakeDataSource(remindersList)
        listViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
    }

    @Test
    fun reminder_loading() {
        mainCoroutineRule.pauseDispatcher()

        listViewModel.loadReminders()
        assertThat(listViewModel.showLoading.getOrAwaitValueTest(), `is`(true))

        mainCoroutineRule.resumeDispatcher()
        assertThat(listViewModel.showLoading.getOrAwaitValueTest(), `is`(false))
    }

    @Test
    fun loadReminder_checkData() {
        // GIVEN
        listViewModel.loadReminders()

        // WHEN
        val viewModelList = listViewModel.remindersList.getOrAwaitValueTest()

        // THEN
        assertThat(viewModelList, not(nullValue()))
    }
    @Test
    fun loadRemindersError_showTestException() {
        dataSource.setReturnError(true)
        listViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)

        listViewModel.loadReminders()

        val value = listViewModel.showSnackBar.getOrAwaitValueTest()
        Assert.assertThat(value, Is.`is`("Test exception"))
    }

    @Test
    fun loadreminders_savereminder() = mainCoroutineRule.runBlockingTest {

        listViewModel.loadReminders()
        val viewModel = listViewModel.remindersList.getOrAwaitValueTest()
        val dataSource = (dataSource.getReminders() as Result.Success).data

        assertThat(viewModel[0].id, `is`(dataSource[0].id))
        assertThat(viewModel[0].title, `is`(dataSource[0].title))
        assertThat(viewModel[0].description, `is`(dataSource[0].description))
        assertThat(viewModel[0].location, `is`(dataSource[0].location))
        assertThat(viewModel[0].longitude, `is`(dataSource[0].longitude))
        assertThat(viewModel[0].latitude, `is`(dataSource[0].latitude))
    }

}