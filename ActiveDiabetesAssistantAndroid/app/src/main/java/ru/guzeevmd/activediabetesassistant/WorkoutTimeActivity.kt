package ru.guzeevmd.activediabetesassistant

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding

class WorkoutTimeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
        }

        val workoutTimeEditText = EditText(this).apply {
            hint = "Время тренировки (минуты)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val saveButton = Button(this).apply {
            text = "сохранить"
            setOnClickListener {
                // Implement save logic
                finish()
            }
        }

        layout.addView(workoutTimeEditText)
        layout.addView(saveButton)
        setContentView(layout)
    }
}
