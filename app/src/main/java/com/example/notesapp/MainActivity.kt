package com.example.notesapp

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.ui.viewmodel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: NoteViewModel by viewModels()
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        // Инициализация адаптера с обработкой долгого нажатия (удаление)
        adapter = NoteAdapter { note ->
            viewModel.deleteNote(note)
            Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Сбор состояния из StateFlow (UDF)
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.notes)

                // Показ ошибок (например, пустой заголовок)
                if (state.errorMessage != null) {
                    Toast.makeText(this@MainActivity, state.errorMessage, Toast.LENGTH_SHORT).show()
                    viewModel.clearError()
                }
            }
        }

        fabAdd.setOnClickListener {
            showAddNoteDialog()
        }
    }

    private fun showAddNoteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Новая заметка")

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val padding = (16 * resources.displayMetrics.density).toInt()
        layout.setPadding(padding, padding, padding, padding)

        val etTitle = EditText(this).apply { hint = "Заголовок" }
        val etContent = EditText(this).apply { hint = "Текст заметки" }
        layout.addView(etTitle)
        layout.addView(etContent)

        builder.setView(layout)

        builder.setPositiveButton("Сохранить") { _, _ ->
            val title = etTitle.text.toString()
            val content = etContent.text.toString()
            // Передаем данные во ViewModel
            viewModel.addNote(title, content)
        }
        builder.setNegativeButton("Отмена", null)
        builder.show()
    }
}