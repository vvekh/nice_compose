package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.diplomast.Adapters.SpecialistPointAdapter;
import com.example.diplomast.DTO.PointDTO;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialistPointsActivity extends AppCompatActivity {
    APIinterface api;
    private SpecialistPointAdapter adapter;
    List<PointDTO> points = new ArrayList<>();
    List<PointDTO> specialistPoints = new ArrayList<>();
    Specialist specialist;
    RecyclerView PointView;
    Button SaveBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_specialist_points);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        PointView = findViewById(R.id.point_view);
        SaveBtn = findViewById(R.id.save_btn);

        api = APIclient.start().create(APIinterface.class);
        specialistPoints = (List<PointDTO>) getIntent().getParcelableExtra("specialistPoints");
        specialist = (Specialist) getIntent().getSerializableExtra("ActiveSpecialist");
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        PointView.setLayoutManager(layoutManager);

        fetchData();
    }

    private void fetchData() {
        Call<List<PointDTO>> call = api.getAllPoints();
        call.enqueue(new Callback<List<PointDTO>>() {
            @Override
            public void onResponse(Call<List<PointDTO>> call, Response<List<PointDTO>> response) {
                points = response.body();

                Call<List<PointDTO>> call1 = api.getSpecialistPoints(specialist.id);
                call1.enqueue(new Callback<List<PointDTO>>() {
                    @Override
                    public void onResponse(Call<List<PointDTO>> call, Response<List<PointDTO>> response) {
                        specialistPoints = response.body();
                        adapter = new SpecialistPointAdapter(points, specialistPoints, specialist.id);
                        PointView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<List<PointDTO>> call, Throwable t) {
                        Log.d("FAIL", t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<PointDTO>> call, Throwable t) {
                Log.d("FAIL", t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        String separatorr = "Специалист";

        Intent intent = new Intent(this, SpecialistProfileActivity.class);
        intent.putExtra("ActiveSpecialist", (Serializable) specialist);
        intent.putExtra("KEY", separatorr);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }
}