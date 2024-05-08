package com.example.diplomast;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.Adapters.FavouritesAdapter;
import com.example.diplomast.Adapters.SpecialistAdapter;
import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Note;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavouritesActivity extends AppCompatActivity {
    APIinterface api; Client client;
    List<Specialist> favouriteSpecialists = new ArrayList<>();
    RecyclerView SpecView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favourites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        client = (Client) getIntent().getSerializableExtra("ActiveClient");
        SpecView = findViewById(R.id.spec_view);
        SpecView.setLayoutManager(new LinearLayoutManager(this)); // Установка одного LayoutManager

        api = APIclient.start().create(APIinterface.class);
        Call<List<Specialist>> call = api.getFavoriteSpecialists(client.id);
        call.enqueue(new Callback<List<Specialist>>() {
            @Override
            public void onResponse(Call<List<Specialist>> call, Response<List<Specialist>> response) {
                favouriteSpecialists = response.body();
                FavouritesAdapter adapter = new FavouritesAdapter(favouriteSpecialists);
                SpecView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<List<Specialist>> call, Throwable t) {Log.d("FAIL", t.getMessage());}
        });
    }
}