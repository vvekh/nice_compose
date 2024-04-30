package com.example.diplomast;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.diplomast.Adapters.SpecialistAdapter;
import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialistListActivity extends AppCompatActivity {
    APIinterface api; List<Integer> selectedIds; List<Specialist> favouriteSpecialists;
    Client client;
    ImageView LikedBtn;
    RecyclerView SpecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_specialist_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        client = (Client) getIntent().getSerializableExtra("ActiveClient");
        selectedIds = (List<Integer>) getIntent().getSerializableExtra("selectedIds");
        favouriteSpecialists = (List<Specialist>) getIntent().getSerializableExtra("favouriteSpecialists");
        LikedBtn = findViewById(R.id.liked_btn);
        SpecView = findViewById(R.id.spec_view);

        SpecView.setLayoutManager(new LinearLayoutManager(this)); // Установка одного LayoutManager
        api = APIclient.start().create(APIinterface.class);
        loadSpecialists();
    }

    private void loadSpecialists() {
        if (selectedIds != null && !selectedIds.isEmpty()) {
            // Загрузка специалистов по выбранным критериям
            Call<List<Specialist>> call = api.getSpecialistsByCriteria(selectedIds);
            call.enqueue(new Callback<List<Specialist>>() {
                @Override
                public void onResponse(Call<List<Specialist>> call, Response<List<Specialist>> response) {
                    if (response.isSuccessful()) {
                        List<Specialist> specialists = response.body();
                        // Создание и установка адаптера для RecyclerView
                        SpecialistAdapter adapter = new SpecialistAdapter(specialists, favouriteSpecialists);
                        SpecView.setAdapter(adapter);
                    } else {
                        Log.e("API Error", "Failed to retrieve favorite specialists. Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Specialist>> call, Throwable t) {
                    Log.e("API Error", "Failed to retrieve favorite specialists. Error: " + t.getMessage());
                }
            });
        } else {
            // Загрузка всех специалистов
            Call<List<Specialist>> call = api.getAllSpecialists();
            call.enqueue(new Callback<List<Specialist>>() {
                @Override
                public void onResponse(Call<List<Specialist>> call, Response<List<Specialist>> response) {
                    if (response.isSuccessful()) {
                        List<Specialist> specialists = response.body();
                        // Создание и установка адаптера для RecyclerView
                        SpecialistAdapter adapter = new SpecialistAdapter(specialists, favouriteSpecialists);
                        SpecView.setAdapter(adapter);
                    } else {
                        Log.e("API Error", "Failed to retrieve favorite specialists. Error: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<List<Specialist>> call, Throwable t) {
                    Log.e("API Error", "Failed to retrieve favorite specialists. Error: " + t.getMessage());
                }
            });
        }
    }
}