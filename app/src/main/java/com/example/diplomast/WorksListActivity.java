package com.example.diplomast;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplomast.Adapters.ClWorkAdapter;
import com.example.diplomast.Adapters.SpWorkAdapter;
import com.example.diplomast.Adapters.SpecialistAdapter;
import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.DTO.Work;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorksListActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    List<Work> workList;
    String separatorr;
    APIinterface api;
    RecyclerView WorkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_works_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WorkView = findViewById(R.id.work_view);
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        WorkView.setLayoutManager(new LinearLayoutManager(this));
        api = APIclient.start().create(APIinterface.class);
        String workJson = sharedPreferences.getString("workList", "");
        Type listType = new TypeToken<List<Work>>() {}.getType();
        Gson gson = new Gson();
        workList = gson.fromJson(workJson, listType);
        separatorr = sharedPreferences.getString("KEY", "");

        if (separatorr.equals("Клиент")){
            String clientJson = sharedPreferences.getString("tempUser", "");
            if (!clientJson.isEmpty()) {
                Client tempClient = gson.fromJson(clientJson, Client.class);
                Call<List<Specialist>> call = api.getAllSpecialists();
                call.enqueue(new Callback<List<Specialist>>() {
                    @Override
                    public void onResponse(Call<List<Specialist>> call, Response<List<Specialist>> response) {
                        if (response.isSuccessful()) {
                            List<Specialist> specialists = response.body();
                            SpWorkAdapter spWorkAdapter = new SpWorkAdapter(specialists, workList, tempClient);
                            WorkView.setAdapter(spWorkAdapter);
                        } else {Log.e("FAIL", response.message());}
                    }
                    @Override
                    public void onFailure(Call<List<Specialist>> call, Throwable t) {Log.e("FAIL", t.getMessage());}
                });
            }
        }else if (separatorr.equals("Специалист")){
            String specialistJson = sharedPreferences.getString("tempUser", "");
            if (!specialistJson.isEmpty()) {
                Specialist tempSpecialist = gson.fromJson(specialistJson, Specialist.class);
                Call<List<Client>> call = api.getAllClients();
                call.enqueue(new Callback<List<Client>>() {
                    @Override
                    public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                        if (response.isSuccessful()) {
                            List<Client> clients = response.body();
                            ClWorkAdapter clWorkAdapter = new ClWorkAdapter(clients, workList, tempSpecialist);
                            WorkView.setAdapter(clWorkAdapter);
                        } else {Log.e("FAIL", response.message());}
                    }
                    @Override
                    public void onFailure(Call<List<Client>> call, Throwable t) {Log.e("FAIL", t.getMessage());}
                });
            }
        }
    }
}