package com.example.pulseid

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // UI references
        val emailEditText = findViewById<EditText>(R.id.etUsername)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.etConfirmPassword)
        val createAccountButton = findViewById<Button>(R.id.btnCreateAccount)

        // Handle password eye toggle
        setPasswordToggle(passwordEditText, true)
        setPasswordToggle(confirmPasswordEditText, false)

        createAccountButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user with Firebase
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()

                        // Save login flag
                        val prefs = getSharedPreferences("PulseID_Prefs", MODE_PRIVATE)
                        prefs.edit().putBoolean("isFirstLogin", false).apply()

                        // Go to HomeActivity
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        // Save login flag
        val prefs = getSharedPreferences("PulseID_Prefs", MODE_PRIVATE)
        val isFirstLogin = prefs.getBoolean("isFirstLogin", true)

        /* Save it as false now that account is created */
        prefs.edit().putBoolean("isFirstLogin", false).apply()

        if (isFirstLogin) {
            // Direct to NoDataActivity on first login
            startActivity(Intent(this, NoDataActivity::class.java))
        } else {
            // Direct to DashboardActivity on future logins
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        finish()

    }

    // Function to toggle password visibility
    @SuppressLint("ClickableViewAccessibility")
    private fun setPasswordToggle(editText: EditText, isMainPassword: Boolean) {
        editText.setOnTouchListener { _, event ->
            val drawableEnd = 2 // Right drawable
            if (event.action == MotionEvent.ACTION_UP) {
                val drawable = editText.compoundDrawables[drawableEnd]
                drawable?.let {
                    if (event.rawX >= (editText.right - it.bounds.width())) {
                        if (isMainPassword) {
                            isPasswordVisible = !isPasswordVisible
                            editText.inputType = if (isPasswordVisible)
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            else
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                0, 0,
                                if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed, 0
                            )
                        } else {
                            isConfirmPasswordVisible = !isConfirmPasswordVisible
                            editText.inputType = if (isConfirmPasswordVisible)
                                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                            else
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                0, 0,
                                if (isConfirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed, 0
                            )
                        }

                        editText.setSelection(editText.text.length)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }
    }
}
