package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {


    private lateinit var db: RemindersDatabase
    private lateinit var repo: RemindersLocalRepository

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private  val reminder = ReminderDTO("Ayse", "Aysegul","Gaziantep",37.05,37.34,"1")

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), RemindersDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        repo = RemindersLocalRepository(db.reminderDao(), Dispatchers.Main)
    }


    @After
    fun closeDB() {
        db.close()
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        val reminder = ReminderDTO("Ayse", "Aysegul","Gaziantep",37.05,37.34)
        db.reminderDao().saveReminder(reminder)


        val result = repo.getReminder(reminder.id)


        result as Result.Success
        assertThat(result.data.title, `is`("Ayse"))
        assertThat(result.data.description, `is`("Aysegul"))
        assertThat(result.data.location, `is`("Gaziantep"))
        assertThat(result.data.latitude, `is`(37.05))
        assertThat(result.data.longitude, `is`(37.34))

    }

    @Test
    fun saveTask_retrievesNoTaskWrongId() = runBlocking {
        val reminder = ReminderDTO("Ayse", "Aysegul","Gaziantep",37.05,37.34)
        db.reminderDao().saveReminder(reminder)

        val result = repo.getReminder("id")

        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
    }


}