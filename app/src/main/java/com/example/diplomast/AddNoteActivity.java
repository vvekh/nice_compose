package com.example.diplomast;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomast.DTO.Note;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNoteActivity extends AppCompatActivity {
    EditText NoteTitle, NoteContent; APIinterface api;
    Button AddNoteBtn; Specialist specialist;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        specialist = (Specialist) getIntent().getSerializableExtra("ActiveSpecialist");
        api = APIclient.start().create(APIinterface.class);

        NoteTitle = findViewById(R.id.note_title);
        NoteContent = findViewById(R.id.note_content);
        AddNoteBtn = findViewById(R.id.add_note_btn);
        AddNoteBtn.setOnClickListener(this::SaveOnClick);
    }

    private void SaveOnClick(View view){
        String title = NoteTitle.getText().toString();
        String content = NoteContent.getText().toString();
        int specialistId = specialist.id; // Получите идентификатор специалиста из существующего объекта

        if(title.equals(null) || title.equals("")){
            Toast.makeText(getApplicationContext(), "Все поля должны быть заполнены!", Toast.LENGTH_LONG).show();
        }else if(content.equals(null) || content.equals("")){
            Toast.makeText(getApplicationContext(), "Все поля должны быть заполнены!", Toast.LENGTH_LONG).show();
        }else {
            Note note = new Note();
            note.id = 0;
            note.title = title;
            note.content = content;
            note.specialistid = specialistId;
            note.status = "0";

            Call<String> call = api.insertNote(note);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Log.d("SUCCESS", "ЗАМЕТКА ДОБАВЛЕНА");
                        Toast.makeText(getApplicationContext(), "Карточка добавлена и будет доступна после одобрения администратора!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Log.d("FAIL", "ЗАМЕТКА НЕ ДОБАВЛЕНА");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("FAIL", "ЗАМЕТКА НЕ ДОБАВЛЕНА ПО ОШИБКЕ");
                }
            });
        }
    }
}