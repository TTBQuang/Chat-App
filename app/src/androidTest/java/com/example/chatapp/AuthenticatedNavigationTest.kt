package com.example.chatapp

import android.content.Context
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.chatapp.data.di.SignInRepositoryModule
import com.example.chatapp.data.model.SignInResult
import com.example.chatapp.data.repository.SignInRepository
import com.example.chatapp.helper.authenticatedFakeSignInResult
import com.example.chatapp.repository.AuthenticatedFakeSignInRepository
import com.example.chatapp.ui.login.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton


@RunWith(AndroidJUnit4::class)
@UninstallModules(SignInRepositoryModule::class)
@HiltAndroidTest
class AuthenticatedNavigationTest {
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class FakeRepositoryModule {
        @Singleton
        @Binds
        abstract fun bindSignInRepository(
            authenticatedFakeSignInRepository: AuthenticatedFakeSignInRepository
        ): SignInRepository
    }

    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setupAppNavHost() {
        hiltRule.inject()
    }

    @Test
    fun home_screen_is_start_destination_if_user_have_signed_in() {
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Home>()
                ?: false
        )
    }

    @Test
    fun profile_dialog_shown_when_click_profile_image() {
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Home>()
                ?: false
        )
        composeTestRule.onNodeWithTag(context.resources.getString(R.string.tag_profile_image))
            .performClick()
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Profile>()
                ?: false
        )
    }

    @Test
    fun profile_dialog_close_when_press_back() {
        navigateToProfileDialog()
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Profile>()
                ?: false
        )
        Espresso.pressBack()
        assertFalse(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Profile>()
                ?: true
        )
    }

    @Test
    fun login_screen_show_and_user_data_null_when_click_btn_log_out() {
        navigateToProfileDialog()
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Profile>()
                ?: false
        )
        composeTestRule.onNodeWithTag(context.resources.getString(R.string.tag_btn_log_out))
            .performClick()
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Login>()
                ?: false
        )

        composeTestRule.activity.setContent {
            val loginViewModel: LoginViewModel = hiltViewModel()
            assertNull(loginViewModel.userData)
        }
    }

    @Test
    fun navigate_to_chat_screen_when_click_chat_item() {
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Home>()
                ?: false
        )

        composeTestRule.onAllNodesWithTag(context.resources.getString(R.string.tag_history_chat_item))
            .onFirst()
            .performClick()

        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Chat>()
                ?: false
        )
    }

    @Test
    fun navigate_to_home_screen_when_press_back_icon_in_chat_screen() {
        navigateToChatScreen()
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Chat>()
                ?: false
        )

        composeTestRule.onNodeWithTag(context.resources.getString(R.string.tag_back_icon_chat_screen))
            .performClick()

        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Home>()
                ?: false
        )
    }

    private fun navigateToProfileDialog() {
        composeTestRule.onNodeWithTag(context.resources.getString(R.string.tag_profile_image))
            .performClick()
    }

    private fun navigateToChatScreen() {
        composeTestRule.onAllNodesWithTag(context.resources.getString(R.string.tag_history_chat_item))
            .onFirst()
            .performClick()
    }
}