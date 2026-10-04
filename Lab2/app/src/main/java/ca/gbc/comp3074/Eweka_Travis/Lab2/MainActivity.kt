package ca.gbc.comp3074.Eweka_Travis.Lab2

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private companion object {
        const val DEFAULT_STEP = 1
        const val ALTERNATE_STEP = 2
        const val KEY_COUNT = "count"
        const val KEY_STEP = "step"
    }

    private var count = 0
    private var step = DEFAULT_STEP

    private lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Keep the counter and step size across rotation
        if (savedInstanceState != null) {
            count = savedInstanceState.getInt(KEY_COUNT)
            step = savedInstanceState.getInt(KEY_STEP)
        }

        output = findViewById(R.id.textViewOutput)
        updateOutput()

        findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            count += step
            updateOutput()
        }

        findViewById<Button>(R.id.buttonSubtract).setOnClickListener {
            count -= step
            updateOutput()
        }

        findViewById<Button>(R.id.buttonReset).setOnClickListener {
            count = 0
            step = DEFAULT_STEP
            updateOutput()
        }

        findViewById<Button>(R.id.buttonStep).setOnClickListener {
            step = ALTERNATE_STEP
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_COUNT, count)
        outState.putInt(KEY_STEP, step)
    }

    private fun updateOutput() {
        output.text = count.toString()
    }
}
