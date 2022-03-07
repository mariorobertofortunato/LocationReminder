package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var db: RemindersDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = db.close()

    @Test
    fun insertReminder() = runBlockingTest {
        // GIVEN
        val reminder = ReminderDTO("Ayse", "Aysegul","Gaziantep",37.05,37.34,"1")
        db.reminderDao().saveReminder(reminder)

        // WHEN
        val loadedRemind = db.reminderDao().getReminderById("1")

        // THEN
        assertThat(loadedRemind as ReminderDTO, notNullValue())
        assertThat(reminder.id, `is`(loadedRemind.id))
        assertThat(reminder.description, `is`(loadedRemind.description))
        assertThat(reminder.location, `is`(loadedRemind.location))
        assertThat(reminder.latitude, `is`(loadedRemind.latitude))
        assertThat(reminder.longitude, `is`(loadedRemind.longitude))
    }

    @Test
    fun deleteReminders() = runBlockingTest {
        // GIVEN
        val reminder = ReminderDTO("Ayse", "Aysegul","Gaziantep",37.05,37.34,"1")
        db.reminderDao().saveReminder(reminder)
        val reminder2 = ReminderDTO("Ayse", "Aysegul","Gaziantep",37.05,37.34,"1")
        db.reminderDao().saveReminder(reminder2)

        db.reminderDao().deleteAllReminders()

        // WHEN
        val loadedRemind = db.reminderDao().getReminders()

        // THEN
        assertThat(loadedRemind, `is`(emptyList()))
    }

}