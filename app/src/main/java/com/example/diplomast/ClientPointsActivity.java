package com.example.diplomast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.diplomast.Adapters.ClientPointAdapter;
import com.example.diplomast.DTO.Client;
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

public class ClientPointsActivity extends AppCompatActivity {
    APIinterface api; Client client;
    RecyclerView PointView;
    private List<Integer> selectedIds;
    List<Specialist> favouriteSpecialists;
    private ClientPointAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_points);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        client = (Client) getIntent().getSerializableExtra("ActiveClient");
        api = APIclient.start().create(APIinterface.class);
        PointView = findViewById(R.id.point_view);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        PointView.setLayoutManager(layoutManager);

        selectedIds = new ArrayList<>();

        new Thread(() -> {
            Call<List<Specialist>> call = api.getFavoriteSpecialists(client.id);
            call.enqueue(new Callback<List<Specialist>>() {
                @Override
                public void onResponse(Call<List<Specialist>> call, Response<List<Specialist>> response) {
                    if (response.isSuccessful()) {
                        favouriteSpecialists = response.body();
                        Log.d("RSEDTFYGUHIJOKP",favouriteSpecialists.toString());
                    } else {
                        Log.d("API Error", "Failed to retrieve favorite specialists. Error: " + response.message());
                    }
                }
                @Override
                public void onFailure(Call<List<Specialist>> call, Throwable t) {
                    Log.d("API Error", "Failed to retrieve favorite specialists. Error: " + t.getMessage());
                }
            });
            Call<List<PointDTO>> call1 = api.getAllPoints();
            call1.enqueue(new Callback<List<PointDTO>>() {
                @Override
                public void onResponse(Call<List<PointDTO>> call, Response<List<PointDTO>> response) {
                    List<PointDTO> points = response.body();
                    adapter = new ClientPointAdapter(points, selectedIds);
                    PointView.setAdapter(adapter);
                }
                @Override
                public void onFailure(Call<List<PointDTO>> call, Throwable t) {
                    Log.d("FAIL", t.getMessage());
                }
            });
        }).start();
    }

    public void ClearOnClick(View view) {
        selectedIds.clear();
        adapter.notifyDataSetChanged();
    }

    public void SearchOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), SpecialistListActivity.class);
        intent.putIntegerArrayListExtra("selectedIds", new ArrayList<>(selectedIds));
        intent.putExtra("favouriteSpecialists", (Serializable) favouriteSpecialists);
        intent.putExtra("ActiveClient", (Serializable) client);
        startActivity(intent);
    }
}