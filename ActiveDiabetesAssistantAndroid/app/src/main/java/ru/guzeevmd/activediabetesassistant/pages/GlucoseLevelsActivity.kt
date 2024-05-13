package ru.guzeevmd.activediabetesassistant.pages

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding

class GlucoseLevelsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
        }

        val glucoseEditText = EditText(this).apply {
            hint = "Значение глюкозы"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val saveButton = Button(this).apply {
            text = "сохранить"
            setOnClickListener {
                // Implement save logic
                finish()
            }
        }

        layout.addView(glucoseEditText)
        layout.addView(saveButton)
        setContentView(layout)
    }
}
