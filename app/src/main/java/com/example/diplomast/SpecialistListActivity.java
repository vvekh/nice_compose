package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.Adapters.SpecialistAdapter;
import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialistListActivity extends AppCompatActivity {
    APIinterface api; List<Integer> selectedIds; List<Specialist> favouriteSpecialists;
    Client client;
    ImageView LikedBtn;
    RecyclerView SpecView; TextView ErrorView;

    @SuppressLint("MissingInflatedId")
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
        ErrorView = findViewById(R.id.error_view);

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
                        ErrorView.setVisibility(View.GONE);
                        SpecView.setVisibility(View.VISIBLE);
                        List<Specialist> specialists = response.body();
                        // Создание и установка адаптера для RecyclerView
                        if (specialists.size() > 0){
                            SpecialistAdapter adapter = new SpecialistAdapter(specialists, favouriteSpecialists, client);
                            SpecView.setAdapter(adapter);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "cfvgbhjn", Toast.LENGTH_LONG);
                        }
                    } else {
                        ErrorView.setVisibility(View.VISIBLE);
                        SpecView.setVisibility(View.GONE);
                        Log.e("API Error", "Failed to retrieve favorite specialists. Error: " + response.message());
                    }
                }
                @Override
                public void onFailure(Call<List<Specialist>> call, Throwable t) {Log.e("API Error", "Failed to retrieve favorite specialists. Error: " + t.getMessage());}
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
                        SpecialistAdapter adapter = new SpecialistAdapter(specialists, favouriteSpecialists, client);
                        SpecView.setAdapter(adapter);
                    } else {
                        Log.e("API Error", "Failed to retrieve favorite specialists. Error: " + response.message());
                    }
                }
                @Override
                public void onFailure(Call<List<Specialist>> call, Throwable t) {Log.e("API Error", "Failed to retrieve favorite specialists. Error: " + t.getMessage());}
            });
        }
    }

    public void FavoritesOnClick(View view){
        Intent intent = new Intent(this, FavouritesActivity.class);
        intent.putExtra("ActiveClient", (Serializable) client);
        startActivity(intent);
    }
}