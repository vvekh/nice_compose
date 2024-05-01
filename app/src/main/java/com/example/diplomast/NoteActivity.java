package com.example.diplomast;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomast.DTO.Note;

public class NoteActivity extends AppCompatActivity {
    TextView NoteTitle, NoteContent;
    Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NoteTitle = findViewById(R.id.note_title);
        NoteContent = findViewById(R.id.note_content);

        note = (Note) getIntent().getSerializableExtra("note");

        String cont = note.content;
        NoteTitle.setText(note.title);
        NoteContent.setText(cont);
    }
}