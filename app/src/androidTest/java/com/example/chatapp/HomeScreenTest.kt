package com.example.chatapp

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.chatapp.data.di.SignInRepositoryModule
import com.example.chatapp.data.repository.SignInRepository
import com.example.chatapp.helper.fakeSearchUserDataList
import com.example.chatapp.helper.fakeUserData
import com.example.chatapp.helper.fakeUserDataList
import com.example.chatapp.repository.AuthenticatedFakeSignInRepository
import com.example.chatapp.ui.home.HomeScreen
import com.example.chatapp.ui.home.widget.SearchBar
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Singleton

@RunWith(AndroidJUnit4::class)
@UninstallModules(SignInRepositoryModule::class)
@HiltAndroidTest
class HomeScreenTest {
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
    fun show_chat_history_list_in_home_screen() {
        composeTestRule.activity.setContent {
            HomeScreen(userData = fakeUserData)
        }
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Home>()
                ?: false
        )
        composeTestRule.onAllNodesWithTag(
            context.resources.getString(R.string.tag_username_chat_item),
            useUnmergedTree = true
        )
            .assertCountEquals(fakeUserDataList.size)

        fakeUserDataList[0].username?.let {
            composeTestRule.onAllNodesWithTag(
                context.resources.getString(R.string.tag_username_chat_item),
                useUnmergedTree = true
            )
                .onFirst()
                .assertTextContains(it)
        }
    }
}