package ru.guzeevmd.activediabetesassistant.pages

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32)
        }

        val resultsTextView = TextView(this).apply {
            text = "Here are your results...\n" +
                    "Risk of Hypo/Hyperglycemia: Low"
            textSize = 18f
        }

        layout.addView(resultsTextView)
        setContentView(layout)
    }
}
