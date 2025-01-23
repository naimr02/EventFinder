package com.naimrlet.eventfinder.auth

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.Image
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.naimrlet.eventfinder.R

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
fun LoginScreen(onLoginSuccess: () -> Unit, onSignUpClick: () -> Unit) {
    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current

    BackHandler(enabled = true) {
        // Optionally handle app exit confirmation here
    }

    val auth = FirebaseAuth.getInstance()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painterResource(id = R.drawable.logo),
                contentDescription = "Universiti Teknologi MARA Logo",
                modifier = Modifier.size(150.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            ValidatingInputTextField(
                value = loginViewModel.email,
                updateState = { input -> loginViewModel.updateEmail(input) },
                label = "Email",
                validatorHasErrors = loginViewModel.emailHasErrors,
                errorMessage = "Incorrect email format."
            )

            Spacer(modifier = Modifier.height(16.dp))

            ValidatingInputTextField(
                value = loginViewModel.password,
                updateState = { input -> loginViewModel.updatePassword(input) },
                label = "Password",
                validatorHasErrors = loginViewModel.passwordHasErrors,
                errorMessage = "Password cannot be empty.",
                isPasswordField = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!loginViewModel.emailHasErrors && !loginViewModel.passwordHasErrors) {
                        auth.signInWithEmailAndPassword(loginViewModel.email, loginViewModel.password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onLoginSuccess()
                                } else {
                                    Toast.makeText(context, "Email or Username is incorrect", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text("Login", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onSignUpClick) {
                Text("Don't have an account? Sign Up")
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
