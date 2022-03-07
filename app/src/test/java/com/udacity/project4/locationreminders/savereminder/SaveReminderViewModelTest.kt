package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.getOrAwaitValueTest
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.wrapEspressoIdlingResource
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()


    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setup() {
        stopKoin()
        val remindersList = mutableListOf<ReminderDTO>(
            ReminderDTO("testTitle", "testDesc","testLocation",30.00,31.00),
            ReminderDTO("qqqqq", "wwwww","eeeeee",31.00,32.00),
            ReminderDTO("aaaaa", "sssss","dddd",32.00,33.00)
        )
        fakeDataSource = FakeDataSource(remindersList)
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @Test
    fun onClear() {
        viewModel.onClear()

        assertThat(viewModel.reminderTitle.getOrAwaitValueTest(), nullValue())
        assertThat(viewModel.reminderDescription.getOrAwaitValueTest(), nullValue())
        assertThat(viewModel.reminderSelectedLocationStr.getOrAwaitValueTest(), nullValue())
        assertThat(viewModel.selectedPOI.getOrAwaitValueTest(), nullValue())
        assertThat(viewModel.latitude.getOrAwaitValueTest(), nullValue())
        assertThat(viewModel.longitude.getOrAwaitValueTest(), nullValue())

    }

    @Test
    fun saveReminder_dataSource() = mainCoroutineRule.runBlockingTest {
        wrapEspressoIdlingResource {
        fakeDataSource.deleteAllReminders()
        val reminderData = ReminderDataItem("Ayse", "Aysegul","Gaziantep",37.05,37.34)
        viewModel.saveReminder(reminderData)
        val reminderFromDataSource = (fakeDataSource.getReminders() as Result.Success).data
        assertThat(reminderData.title, equalTo(reminderFromDataSource[0].title))
    }
    }
}