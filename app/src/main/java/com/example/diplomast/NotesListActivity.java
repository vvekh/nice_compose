package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.Adapters.NoteAdapter;
import com.example.diplomast.DTO.Note;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotesListActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {
    RecyclerView NoteView; Specialist specialist;
    Button NewNoteBtn;
    String separatorr;
    APIinterface api;
    NoteAdapter noteAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notes_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        specialist = (Specialist) getIntent().getSerializableExtra("ActiveSpecialist");
        separatorr = getIntent().getStringExtra("KEY");
        api = APIclient.start().create(APIinterface.class);
        NoteView = findViewById(R.id.note_view);
        NoteView.setLayoutManager(new LinearLayoutManager(this));
        NewNoteBtn = findViewById(R.id.new_note_btn);

        if ("Клиент".equals(separatorr)) {
            NewNoteBtn.setVisibility(View.INVISIBLE);
        } else if ("Специалист".equals(separatorr)) {
            NewNoteBtn.setVisibility(View.VISIBLE);
        }

        List<Note> notes = new ArrayList<>();
        noteAdapter = new NoteAdapter(notes, this);
        NoteView.setAdapter(noteAdapter);

        Call<List<Note>> call = api.getNotes();
        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(Call<List<Note>> call, Response<List<Note>> response) {
                if (response.isSuccessful()) {
                    notes.clear();
                    notes.addAll(response.body());
                    noteAdapter.notifyDataSetChanged();
                } else {
                    // Обработка ошибки
                }
            }

            @Override
            public void onFailure(Call<List<Note>> call, Throwable t) {
                // Обработка ошибки
            }
        });
    }

    @Override
    public void onNoteClick(Note note) {
        Intent intent = new Intent(getApplicationContext(), NoteActivity.class);
        intent.putExtra("note", (Serializable) note);
        startActivity(intent);
    }

    public void OnNewNoteClick(View view){
        Intent intent = new Intent(getApplicationContext(), AddNoteActivity.class);
        intent.putExtra("ActiveSpecialist", (Serializable) specialist);
        intent.putExtra("KEY", separatorr);
        startActivity(intent);
    }
}