package com.example.chatapp

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.chatapp.ui.login.LoginViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class LoginScreenTest {
    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    var composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setupAppNavHost() {
        hiltRule.inject()
    }

    @Test
    fun circular_progress_indicator_show_or_hide_base_on_setLoading_attribute() {
        composeTestRule.activity.setContent {
            val loginViewModel: LoginViewModel = hiltViewModel()
            loginViewModel.setLoading(true)
            composeTestRule.onNodeWithTag(context.resources.getString(R.string.tag_CircularProgressIndicator))
                .assertIsDisplayed()

            loginViewModel.setLoading(false)
            composeTestRule.onNodeWithTag(context.resources.getString(R.string.tag_CircularProgressIndicator))
                .assertIsNotDisplayed()
        }
    }
}