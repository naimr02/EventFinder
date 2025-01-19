package com.naimrlet.eventfinder

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// ViewModel for managing email and password states
class LoginViewModel : ViewModel() {
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    val emailHasErrors by derivedStateOf {
        if (email.isNotEmpty()) {
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }

    val passwordHasErrors by derivedStateOf {
        password.isEmpty()
    }

    fun updateEmail(input: String) {
        email = input
    }

    fun updatePassword(input: String) {
        password = input
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val loginViewModel: LoginViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // MD3 background color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Image
            Image(
                painter = painterResource(id = R.drawable.logo), // Replace "logo" with your file name
                contentDescription = "Universiti Teknologi MARA Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email TextField with Validation
            ValidatingInputTextField(
                value = loginViewModel.email,
                updateState = { input -> loginViewModel.updateEmail(input) },
                label = "Email",
                validatorHasErrors = loginViewModel.emailHasErrors,
                errorMessage = "Incorrect email format."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField with Validation
            ValidatingInputTextField(
                value = loginViewModel.password,
                updateState = { input -> loginViewModel.updatePassword(input) },
                label = "Password",
                validatorHasErrors = loginViewModel.passwordHasErrors,
                errorMessage = "Password cannot be empty.",
                isPasswordField = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Login Button styled with Material Design 3 colors and typography
            Button(
                onClick = {
                    if (!loginViewModel.emailHasErrors && !loginViewModel.passwordHasErrors) {
                        auth.signInWithEmailAndPassword(loginViewModel.email, loginViewModel.password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onLoginSuccess()
                                } else {
                                    // Handle login failure (e.g., show a snackbar or error message)
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text("Login", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidatingInputTextField(
    value: String,
    updateState: (String) -> Unit,
    label: String,
    validatorHasErrors: Boolean,
    errorMessage: String,
    isPasswordField: Boolean = false
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = updateState,
        label = { Text(label, style = MaterialTheme.typography.bodyLarge) },
        isError = validatorHasErrors,
        visualTransformation =
        if (isPasswordField) PasswordVisualTransformation() else VisualTransformation.None,
        supportingText = {
            if (validatorHasErrors) {
                Text(errorMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
            }
        }
    )
}
