package ru.guzeevmd.activediabetesassistant

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
        }

        val nameEditText = EditText(this).apply {
            hint = "Введите имя"
        }

        val ageEditText = EditText(this).apply {
            hint = "Введите возраст"
        }

        val genderSpinner = Spinner(this)

        val diabetesTypeEditText = EditText(this).apply {
            hint = "Введите тип диабета"
        }

        val saveButton = Button(this).apply {
            text = "сохранить"
            setOnClickListener {
                // Implement registration logic
                finish()
            }
        }

        layout.addView(nameEditText)
        layout.addView(ageEditText)
        layout.addView(genderSpinner)
        layout.addView(diabetesTypeEditText)
        layout.addView(saveButton)
        setContentView(layout)
    }
}
