package ru.guzeevmd.activediabetesassistant

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.core.view.setPadding
import ru.guzeevmd.activediabetesassistant.activities.DrawerContent

class UserProfileActivity : ComponentActivity() {
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
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val genderSpinner = Spinner(this).apply {
            val genders = arrayOf("Male", "Female", "Other")
            adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, genders)
        }

        val diabetesTypeEditText = EditText(this).apply {
            hint = "Введите тип диабета"
        }

        val saveButton = Button(this).apply {
            text = "сохранить"
            setOnClickListener {
                // Implement save logic
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
