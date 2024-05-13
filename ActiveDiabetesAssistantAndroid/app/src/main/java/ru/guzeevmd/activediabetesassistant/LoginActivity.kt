package ru.guzeevmd.activediabetesassistant

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
        }

        val emailEditText = EditText(this).apply {
            hint = "Ваш Email"
        }

        val passwordEditText = EditText(this).apply {
            hint = "Пароль"
        }

        val loginButton = Button(this).apply {
            text = "войти"
            setOnClickListener {
                // Implement login logic
                finish()
            }
        }

        layout.addView(emailEditText)
        layout.addView(passwordEditText)
        layout.addView(loginButton)
        setContentView(layout)
    }
}
