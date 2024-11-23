package com.example.chatapp

import android.content.Context
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.chatapp.data.di.SignInRepositoryModule
import com.example.chatapp.data.model.SignInResult
import com.example.chatapp.data.repository.SignInRepository
import com.example.chatapp.helper.authenticatedFakeSignInResult
import com.example.chatapp.helper.unauthenticatedFakeSignInResult
import com.example.chatapp.repository.UnauthenticatedFakeSignInRepository
import com.example.chatapp.ui.login.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
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
class UnauthenticatedNavigationTest {
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class FakeRepositoryModule {
        @Singleton
        @Binds
        abstract fun bindSignInRepository(
            unauthenticatedFakeSignInRepository: UnauthenticatedFakeSignInRepository
        ): SignInRepository
    }

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
    fun login_screen_is_start_destination_if_user_have_not_signed_in() {
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Login>()
                ?: false
        )
    }
}