package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Timeline;
import com.example.diplomast.Retrofit.APIclient;
import com.example.diplomast.Retrofit.APIinterface;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientEditActivity extends AppCompatActivity {
    APIinterface api; Client client; Boolean NewOrNot;
    TextView LoginView, InfoView, PhoneView;
    LinearLayout CalendarLayout;
    EditText NameBox, LoginBox; Spinner TimelineBox; DatePicker BirthdateBox;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api = APIclient.start().create(APIinterface.class);
        client = (Client) getIntent().getSerializableExtra("ActiveClient");
        CalendarLayout = findViewById(R.id.calendar_layout);
        PhoneView = findViewById(R.id.phone_box);
        LoginView = findViewById(R.id.login_view);
        InfoView = findViewById(R.id.info_view);
        NameBox = findViewById(R.id.name_box);
        LoginBox = findViewById(R.id.login_box);
        BirthdateBox = findViewById(R.id.birthdate_box);
        TimelineBox = findViewById(R.id.timeline_box);

        new Thread(() -> {
            long CurrentTime = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(CurrentTime);
            calendar.add(Calendar.YEAR, -14);
            long MaxDate = calendar.getTimeInMillis();
            Call<List<Timeline>> call = api.getAllTimelines();
            call.enqueue(new Callback<List<Timeline>>() {
                @Override
                public void onResponse(Call<List<Timeline>> call, Response<List<Timeline>> response) {
                    List<Timeline> timelines = response.body();
                    List<String> timelineNames = new ArrayList<>();
                    timelineNames.add("Выбрите часовой пояс");
                    for (Timeline timeline : timelines) {
                        String timelineName = timeline.timelinename;
                        timelineNames.add(timelineName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ClientEditActivity.this, android.R.layout.simple_spinner_item, timelineNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    TimelineBox.setAdapter(adapter);
                    TimelineBox.setSelection(client.timelineid);
                }
                @Override
                public void onFailure(Call<List<Timeline>> call, Throwable t) {
                    Log.d("FAILURE", String.valueOf(t));
                }
            });
            runOnUiThread(() -> {
                BirthdateBox.setMaxDate(MaxDate);
                LoginView.setText("@" + client.login);
                LoginBox.setText(client.login);
            });
        }).start();

        if (client.birthdate != null){
            CalendarLayout.setVisibility(View.GONE);
            NewOrNot = false;
            new Thread(() -> {
                Calendar CurrentDate = Calendar.getInstance();
                int CurrentYear = CurrentDate.get(Calendar.YEAR);
                int CurrentMonth = CurrentDate.get(Calendar.MONTH);
                int CurrentDay = CurrentDate.get(Calendar.DAY_OF_MONTH);

                if (client.birthdate != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date birthdate;
                    try {
                        birthdate = sdf.parse(client.birthdate);
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
                            NameBox.setText(client.username);
                            PhoneView.setText(client.phone);
                        });
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            NewOrNot = true;
            CalendarLayout.setVisibility(View.VISIBLE);
        }
    }

    public void SaveOnClick(View view) {
        if (LoginBox.length() < 4){
            Toast.makeText(getApplicationContext(), "Слишком короткий логин!", Toast.LENGTH_SHORT).show();
        } else if (NameBox.length() < 2) {
            Toast.makeText(getApplicationContext(), "Введите имя!", Toast.LENGTH_SHORT).show();
        } else if (TimelineBox.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Выберите часовой пояс!", Toast.LENGTH_SHORT).show();
        } else if (PhoneView.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Введите номер телефона!", Toast.LENGTH_SHORT).show();
        } else if (!isValidPhone(PhoneView.getText().toString())){
            Toast.makeText(getApplicationContext(), "Введите корректный номер телефона!", Toast.LENGTH_SHORT).show();
        }
        else {
            Saving();
        }
    }

    private void Saving(){
        if (NewOrNot == false){ //существующий аккаунт
            client.username = NameBox.getText().toString();
            client.login = LoginBox.getText().toString();
            client.phone = PhoneView.getText().toString();
            client.timelineid = TimelineBox.getSelectedItemPosition() + 1;
            Call<Void> call = api.updateClient(client.id, client);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("SUCCESS", "Data updated successfully");
                        Intent intent = new Intent(getApplicationContext(), ClientProfileActivity.class);
                        intent.putExtra("ActiveClient", (Serializable) client);
                        startActivity(intent);
                    } else {
                        Log.d("FAIL", response.message());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("ERROR", t.getMessage());
                }
            });
        }else if (NewOrNot == true){ //новый аккаунт
            String selectedDate;
            int year = BirthdateBox.getYear();
            int month = BirthdateBox.getMonth();
            int day = BirthdateBox.getDayOfMonth();
            if (month < 10){
                String month1 = "0" + month;
                selectedDate = year + "-" + month1 + "-" + day;
            }else {
                selectedDate = year + "-" + month + "-" + day;
            }

            client.username = String.valueOf(NameBox.getText());
            client.login = String.valueOf(LoginBox.getText());
            client.phone = PhoneView.getText().toString();
            client.timelineid = TimelineBox.getSelectedItemPosition() + 1;
            client.birthdate = selectedDate;

            Call<Void> call = api.postNewClient(client);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("SUCCESS", response.message());
                    Intent intent = new Intent(getApplicationContext(), ClientProfileActivity.class);
                    intent.putExtra("ActiveClient", (Serializable) client);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("FAIL", t.getMessage());
                }
            });
        }
    }

    public static boolean isValidPhone(String phoneNumber) {
        String pattern = "^8\\d{10}$";
        return phoneNumber.matches(pattern);
    }
}