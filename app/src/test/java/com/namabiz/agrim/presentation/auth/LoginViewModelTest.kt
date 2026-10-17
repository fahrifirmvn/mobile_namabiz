package com.namabiz.agrim.presentation.auth

import com.namabiz.agrim.core.datastore.UserPreferencesRepository
import com.namabiz.agrim.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val authRepository = mockk<AuthRepository>()
    private val prefsRepository = mockk<UserPreferencesRepository>()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(authRepository, prefsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testAutofillDemoBuyerSetsCredentials() {
        viewModel.autofillDemoBuyer()
        assertEquals("buyer@agrim.com", viewModel.email.value)
        assertEquals("password", viewModel.password.value)
    }

    @Test
    fun testShortPasswordReturnsError() = runTest {
        viewModel.onEmailChange("buyer@agrim.com")
        viewModel.onPasswordChange("123") // < 6 chars

        viewModel.login()

        assertTrue(viewModel.uiState.value is LoginUiState.Error)
        val error = viewModel.uiState.value as LoginUiState.Error
        assertEquals("Password must be at least 6 characters.", error.message)
    }

    @Test
    fun testSuccessfulLoginTransitionsToSuccess() = runTest {
        coEvery { authRepository.login("buyer@agrim.com", "password") } returns true

        viewModel.onEmailChange("buyer@agrim.com")
        viewModel.onPasswordChange("password")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(LoginUiState.Success, viewModel.uiState.value)
    }

    @Test
    fun testFailedLoginTransitionsToError() = runTest {
        coEvery { authRepository.login("buyer@agrim.com", "wrongpass") } returns false

        viewModel.onEmailChange("buyer@agrim.com")
        viewModel.onPasswordChange("wrongpass")
        viewModel.login()

        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is LoginUiState.Error)
        val error = viewModel.uiState.value as LoginUiState.Error
        assertEquals("Invalid email or password. Please check your credentials.", error.message)
    }
}
