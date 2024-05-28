package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.diplomast.Adapters.PointAdapter;
import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Point;
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.DTO.Timeline;
import com.example.diplomast.DTO.Work;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialistProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_FIRST_BTN = 1;
    SharedPreferences sharedPreferences;
    String Enable; List<Timeline> lines; List<Point> points;
    APIinterface api; Specialist specialist; String separatorr;
    LinearLayout PanelLayout, ShowLayout;
    ImageView FirstBtn, SecondBtn, ThirdBtn, FourthBtn, WorkBtn;
    TextView LoginView, InfoView, GradView, GradView2, Ispoint, TimelineView, PriceView;
    RecyclerView PointView; List<Work> workList;
    Button ExitBtn, AddWorkBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_specialist_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Enable = getIntent().getStringExtra("Enable");
        separatorr = getIntent().getStringExtra("KEY");
        specialist = (Specialist) getIntent().getSerializableExtra("ActiveSpecialist");
        api = APIclient.start().create(APIinterface.class);
        Ispoint = findViewById(R.id.ispoint);
        PanelLayout = findViewById(R.id.panel_layout);
        ShowLayout = findViewById(R.id.show_layout);
        TimelineView = findViewById(R.id.timeline_view);
        PriceView = findViewById(R.id.price_view);
        LoginView = findViewById(R.id.login_view);
        InfoView = findViewById(R.id.info_view);
        GradView = findViewById(R.id.grad_view);
        GradView2 = findViewById(R.id.grad2_view);
        PointView = findViewById(R.id.point_view);
        FirstBtn = findViewById(R.id.first_btn);
        AddWorkBtn = findViewById(R.id.add_work_btn);
        WorkBtn = findViewById(R.id.work_btn);
        SecondBtn = findViewById(R.id.second_btn);
        ThirdBtn = findViewById(R.id.third_btn);
        FourthBtn = findViewById(R.id.fourth_btn);
        ExitBtn = findViewById(R.id.exit_btn);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        PointView.setLayoutManager(layoutManager);
        LoginView.setText("@" + specialist.login);
        new Thread(() -> {
            if ("Специалист".equals(separatorr)){
                Call<List<Work>> call1 = api.getWorksBySpecialistId(specialist.id);
                call1.enqueue(new Callback<List<Work>>() {
                    @Override
                    public void onResponse(Call<List<Work>> call, Response<List<Work>> response) {
                        Gson gson = new Gson();
                        String worksJson = gson.toJson(response.body());
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("workList", worksJson);
                        editor.apply();
                        Log.d("Okay", response.message());
                    }
                    @Override
                    public void onFailure(Call<List<Work>> call, Throwable t) {Log.d("FAIL", t.getMessage());}
                });
            }

            Call<List<Point>> call = api.getSpecialistPoints(specialist.id);
            call.enqueue(new Callback<List<Point>>() {
                @Override
                public void onResponse(Call<List<Point>> call, Response<List<Point>> response) {
                    if (response.isSuccessful()) {
                        points = response.body();
                        PointAdapter adapter = new PointAdapter(points); // listOfPoints - список объектов Point
                        PointView.setLayoutManager(layoutManager);
                        PointView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        PointView.setVisibility(View.GONE);
                        Ispoint.setText("Темы, с которыми работает специалист, не выбраны");
                    }
                }
                @Override
                public void onFailure(Call<List<Point>> call, Throwable t) {
                    Log.d("FAIL", t.getMessage());
                }
            });

            Call<List<Timeline>> call2 = api.getAllTimelines();
            call2.enqueue(new Callback<List<Timeline>>() {
                @Override
                public void onResponse(Call<List<Timeline>> call, Response<List<Timeline>> response) {
                    lines = response.body();
                    Timeline line = lines.get(specialist.timelineid - 1);
                    TimelineView.setText("Часовой пояс: " + line.timelinename);
                }
                @Override
                public void onFailure(Call<List<Timeline>> call, Throwable t) {
                    Log.d("FAIL", t.getMessage());
                }
            });

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthdate;
            try {
                birthdate = sdf.parse(specialist.birthdate);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            Calendar CurrentDate = Calendar.getInstance();
            int CurrentYear = CurrentDate.get(Calendar.YEAR);
            int CurrentMonth = CurrentDate.get(Calendar.MONTH);
            int CurrentDay = CurrentDate.get(Calendar.DAY_OF_MONTH);
            Calendar Birth = Calendar.getInstance();
            Birth.setTime(birthdate);
            int BirthYear = Birth.get(Calendar.YEAR);
            int BirthMonth = Birth.get(Calendar.MONTH);
            int BirthDay = Birth.get(Calendar.DAY_OF_MONTH);
            int Age = CurrentYear - BirthYear;
            if (CurrentMonth < BirthMonth || (CurrentMonth == BirthMonth && CurrentDay < BirthDay)) {
                Age--;
            }
            String AgeSuffix;
            if (Age % 10 == 1 && Age % 100 != 11) {
                AgeSuffix = " год";
            } else if ((Age % 10 >= 2 && Age % 10 <= 4) && !(Age % 100 >= 12 && Age % 100 <= 14)) {
                AgeSuffix = " года";
            } else {
                AgeSuffix = " лет";
            }
            int FinalAge = Age;
            runOnUiThread(() -> {

                if (specialist.graduationid == 1){
                    GradView.setText("Образование: полное высшее");
                }else if (specialist.graduationid == 2){
                    GradView.setText("Образование: два полных высших");
                }
                if (specialist.graduatuon2 == "1"){
                    GradView2.setText("Дополнительное образование: есть");
                }else {
                    GradView2.setText("Дополнительное образование: нет");
                }

                PriceView.setText("Стоимость сеанса: " + specialist.price + "руб.");

                InfoView.setText(specialist.username + " " + specialist.usersurname + ", " + FinalAge + AgeSuffix);
            });
        }).start();
        Load();
    }
    private void Load(){
        if ("false".equals(Enable)){
            PanelLayout.setVisibility(View.GONE);
            ExitBtn.setVisibility(View.GONE);
            Type listType = new TypeToken<List<Work>>() {}.getType();
            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String workJson = sharedPreferences.getString("workList", "");
            Gson gson = new Gson();
            workList = gson.fromJson(workJson, listType);
            boolean isSpecialistIdFound = false;
            for (Work work : workList) {
                String a = String.valueOf(work.specialistid);
                String b = String.valueOf(specialist.id);
                if (a.equals(b)) {
                    isSpecialistIdFound = true;
                    break;
                }
            }
            // Скрываем кнопку, если id специалиста найден в списке
            if (isSpecialistIdFound) {
                AddWorkBtn.setVisibility(View.GONE);
            } else {
                AddWorkBtn.setVisibility(View.VISIBLE);
            }
        } else {
            ShowLayout.setVisibility(View.GONE);
        }
    }
    public void WorkOnClick(View view){
        Gson gson = new Gson();
        String clientJson = sharedPreferences.getString("tempUser", "");
        Client tempClient = gson.fromJson(clientJson, Client.class);
        Call<Void> call = api.addWork(tempClient.id, specialist.id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(getApplicationContext(), "Заявка отправлена, скоро вы сможете связаться со специалистом!", Toast.LENGTH_LONG).show();
                Work work = new Work();
                work.specialistid = specialist.id;
                work.clientid = tempClient.id;
                work.status = "0";
                if (workList == null || workList.size() < 1){
                    workList = new ArrayList<>();
                    work.id = workList.size();
                    workList.add(work);
                }else {
                    workList.add(work);
                }

                Gson gson = new Gson();
                String worksJson = gson.toJson(workList);
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("workList", worksJson);
                editor.apply();
                Log.d("Okay", response.message());

                AddWorkBtn.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {Log.e("FAIL", t.getMessage());}
        });
    }
    public void PanelOnClick(View view) {
        String ButtonName = getResources().getResourceEntryName(view.getId());
        switch (ButtonName){
            case "first_btn":
                Intent intent1 = new Intent(getApplicationContext(), SpecialistPointsActivity.class);
                intent1.putExtra("ActiveSpecialist", (Serializable) specialist);
                intent1.putExtra("KEY", separatorr);
                startActivity(intent1);
                break;
            case "second_btn":
                Intent intent2 = new Intent(getApplicationContext(), NotesListActivity.class);
                intent2.putExtra("ActiveSpecialist", (Serializable) specialist);
                intent2.putExtra("KEY", separatorr);
                startActivity(intent2);
                break;
            case "third_btn":
                Intent intent3 = new Intent(getApplicationContext(), SpecialistEditActivity.class);
                intent3.putExtra("ActiveSpecialist", (Serializable) specialist);
                intent3.putExtra("KEY", separatorr);
                startActivity(intent3);
                break;
            case "fourth_btn":
                Intent intent4 = new Intent(getApplicationContext(), AboutActivity.class);
                intent4.putExtra("ActiveSpecialist", (Serializable) specialist);
                intent4.putExtra("KEY", separatorr);
                startActivity(intent4);
                break;
            case "work_btn":
                Intent intent5 = new Intent(getApplicationContext(), WorksListActivity.class);
                intent5.putExtra("ActiveSpecialist", (Serializable) specialist);
                intent5.putExtra("KEY", separatorr);
                startActivity(intent5);
                break;
        }
    }
    public void ExitOnClick(View view) {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Очистка всех значений
        editor.apply();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Закрыть все предыдущие активити
        startActivity(intent);
    }
}