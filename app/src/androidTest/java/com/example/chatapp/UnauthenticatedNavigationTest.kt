package com.example.chatapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.chatapp.data.di.SignInRepositoryModule
import com.example.chatapp.data.repository.SignInRepository
import com.example.chatapp.repository.UnauthenticatedFakeSignInRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
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

    @Before
    fun setupAppNavHost() {
        hiltRule.inject()
    }

    @Test
    fun verifyStartDestination() {
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Login>()
                ?: false
        )
    }
}