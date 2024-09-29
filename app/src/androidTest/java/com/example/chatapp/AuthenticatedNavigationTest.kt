package com.example.chatapp

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.chatapp.data.di.SignInRepositoryModule
import com.example.chatapp.data.repository.SignInRepository
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
    fun verifyStartDestination() {
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Home>()
                ?: false
        )
    }

    @Test
    fun verifyProfileDialogShowWhenClickProfileIconButton() {
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Home>()
                ?: false
        )
        composeTestRule.onNodeWithTag(context.resources.getString(R.string.cd_profile_image))
            .performClick()
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Profile>()
                ?: false
        )
    }

    @Test
    fun verifyProfileDialogCloseWhenPressBack() {
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
    fun verifyLoginScreeAndUserDataNullWhenSignOutBtnClicked() {
        navigateToProfileDialog()
        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Profile>()
                ?: false
        )
        composeTestRule.onNodeWithTag(context.resources.getString(R.string.cd_btn_log_out))
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
    fun verifyHomeScreenAndSignInResultWhenSignInSuccess() {
        val loginViewModel: LoginViewModel =
            ViewModelProvider(composeTestRule.activity)[LoginViewModel::class.java]

        loginViewModel.setLoading(true)
        loginViewModel.onSignInResult(authenticatedFakeSignInResult)

        // verify saveUserUid method was called
        assertFalse(loginViewModel.state.value.isLoading)

        assertTrue(
            composeTestRule.activity.navController.currentBackStackEntry?.destination?.hasRoute<Home>()
                ?: false
        )
    }

    private fun navigateToProfileDialog() {
        composeTestRule.onNodeWithTag(context.resources.getString(R.string.cd_profile_image))
            .performClick()
    }
}