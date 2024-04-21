package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.diplomast.Adapters.PointAdapter;
import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.PointDTO;
import com.example.diplomast.DTO.Specialist;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialistProfileActivity extends AppCompatActivity {
    APIinterface api; Specialist specialist;
    ImageView FirstBtn, SecondBtn, ThirdBtn, FourthBtn;
    TextView LoginView, InfoView, GradView, GradView2, Ispoint;
    RecyclerView PointView;
    Button ExitBtn;

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
        specialist = (Specialist) getIntent().getSerializableExtra("ActiveSpecialist");
        api = APIclient.start().create(APIinterface.class);
        Ispoint = findViewById(R.id.ispoint);
        LoginView = findViewById(R.id.login_view);
        InfoView = findViewById(R.id.info_view);
        GradView = findViewById(R.id.grad_view);
        GradView2 = findViewById(R.id.grad2_view);
        PointView = findViewById(R.id.point_view);
        FirstBtn = findViewById(R.id.first_btn);
        SecondBtn = findViewById(R.id.second_btn);
        ThirdBtn = findViewById(R.id.third_btn);
        FourthBtn = findViewById(R.id.fourth_btn);
        ExitBtn = findViewById(R.id.exit_btn);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        PointView.setLayoutManager(layoutManager);
        LoginView.setText("@" + specialist.login);
        new Thread(() -> {
            Call<List<PointDTO>> call = api.getSpecialistPoints(specialist.id);
            call.enqueue(new Callback<List<PointDTO>>() {
                @Override
                public void onResponse(Call<List<PointDTO>> call, Response<List<PointDTO>> response) {
                    if (response.isSuccessful()) {
                        List<PointDTO> points = response.body();
                        PointAdapter adapter = new PointAdapter(points); // listOfPoints - список объектов PointDTO
                        PointView.setLayoutManager(layoutManager);
                        PointView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        PointView.setVisibility(View.GONE);
                        Ispoint.setText("Темы, с которыми работает специалист, не выбраны");
                    }
                }
                @Override
                public void onFailure(Call<List<PointDTO>> call, Throwable t) {
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
                if (specialist.graduatuon2 == "0"){
                    GradView2.setText("Дополнительное образование: нет");
                }else if (specialist.graduatuon2 == "1"){
                    GradView2.setText("Дополнительное образование: есть");
                }

                InfoView.setText(specialist.username + " " + specialist.usersurname + ", " + FinalAge + AgeSuffix);
            });
        }).start();
    }

    public void PanelOnClick(View view) {
        String ButtonName = getResources().getResourceEntryName(view.getId());
        switch (ButtonName){
            case "first_btn":
                break;
            case "second_btn":
                break;
            case "third_btn":
                Intent intent = new Intent(getApplicationContext(), SpecialistEditActivity.class);
                intent.putExtra("ActiveSpecialist", (Serializable) specialist);
                startActivity(intent);
                break;
            case "fourth_btn":
                break;
        }
    }

    public void ExitOnClick(View view) {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
    }
}