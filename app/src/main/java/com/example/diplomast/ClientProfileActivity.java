package com.example.diplomast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Work;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;
import com.google.gson.Gson;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientProfileActivity extends AppCompatActivity {
    APIinterface api; Client client; String separatorr;
    TextView LoginView, InfoView;
    ImageView FirstBtn, SecondBtn, ThirdBtn, FourthBtn, FifthBtn;
    Button ExitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        separatorr = getIntent().getStringExtra("KEY");
        client = (Client) getIntent().getSerializableExtra("ActiveClient");
        api = APIclient.start().create(APIinterface.class);
        LoginView = findViewById(R.id.login_view);
        InfoView = findViewById(R.id.info_view);
        FirstBtn = findViewById(R.id.first_btn);
        SecondBtn = findViewById(R.id.second_btn);
        ThirdBtn = findViewById(R.id.third_btn);
        FourthBtn = findViewById(R.id.fourth_btn);
        FifthBtn = findViewById(R.id.fifth_btn);
        ExitBtn = findViewById(R.id.exit_btn);

        LoginView.setText("@" + client.login);
        new Thread(() -> {
            Call<List<Work>> call = api.getWorksByClientId(client.id);
            call.enqueue(new Callback<List<Work>>() {
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
                public void onFailure(Call<List<Work>> call, Throwable t) {Log.e("FAIL", t.getMessage());}
            });

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date birthdate;
            try {
                birthdate = sdf.parse(client.birthdate);
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
                InfoView.setText(client.username + ", " + FinalAge + AgeSuffix);
            });
        }).start();
    }

    public void PanelOnClick(View view) {
        String ButtonName = getResources().getResourceEntryName(view.getId());
        switch (ButtonName){
            case "first_btn":
                Intent intent1 = new Intent(getApplicationContext(), ClientPointsActivity.class);
                intent1.putExtra("ActiveClient", (Serializable) client);
                intent1.putExtra("KEY", separatorr);
                startActivity(intent1);
                break;
            case "second_btn":
                Intent intent2 = new Intent(getApplicationContext(), WorksListActivity.class);
                intent2.putExtra("ActiveClient", (Serializable) client);
                intent2.putExtra("KEY", separatorr);
                startActivity(intent2);
                break;
            case "third_btn":
                Intent intent3 = new Intent(getApplicationContext(), NotesListActivity.class);
                intent3.putExtra("ActiveClient", (Serializable) client);
                intent3.putExtra("KEY", separatorr);
                startActivity(intent3);
                break;
            case "fourth_btn":
                Intent intent4 = new Intent(getApplicationContext(), ClientEditActivity.class);
                intent4.putExtra("ActiveClient", (Serializable) client);
                intent4.putExtra("KEY", separatorr);
                startActivity(intent4);
                break;
            case "fifth_btn":
                Intent intent5 = new Intent(getApplicationContext(), AboutActivity.class);
                intent5.putExtra("ActiveClient", (Serializable) client);
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