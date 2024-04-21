package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.diplomast.DTO.Specialist;
import com.example.diplomast.DTO.Timeline;

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

public class SpecialistEditActivity extends AppCompatActivity {
    APIinterface api; Specialist specialist;
    TextView LoginView, InfoView;
    LinearLayout CalendarLayout, DocLayout;
    EditText FioBox, LoginBox, PriceBox;
    DatePicker BirthdateBox; Spinner TimelineBox, SexBox;
    Button SavedocBtn, SavedocBtn2, SaveBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_specialist_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api = APIclient.start().create(APIinterface.class);
        specialist = (Specialist) getIntent().getSerializableExtra("ActiveSpecialist");
        LoginView = findViewById(R.id.login_view);
        InfoView = findViewById(R.id.info_view);
        CalendarLayout = findViewById(R.id.calendar_layout);
        DocLayout = findViewById(R.id.doc_layout);
        FioBox = findViewById(R.id.fio_box);
        LoginBox = findViewById(R.id.login_box);
        PriceBox = findViewById(R.id.price_box);
        BirthdateBox = findViewById(R.id.birthdate_box);
        TimelineBox = findViewById(R.id.timeline_box);
        SavedocBtn = findViewById(R.id.savedoc_btn);
        SavedocBtn2 = findViewById(R.id.savedoc_btn2);
        SaveBtn = findViewById(R.id.save_btn);
        SexBox = findViewById(R.id.sex_box);

        new Thread(() -> {
            long CurrentTime = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(CurrentTime);
            calendar.add(Calendar.YEAR, -22);
            long MaxDate = calendar.getTimeInMillis();
            List<String> pols = new ArrayList<>();
            Call<List<Timeline>> call = api.getAllTimelines();
            call.enqueue(new Callback<List<Timeline>>() {
                @Override
                public void onResponse(Call<List<Timeline>> call, Response<List<Timeline>> response) {
                    List<Timeline> timelines = response.body();
                    List<String> timelineNames = new ArrayList<>();
                    timelineNames.add("Выберите часовой пояс");
                    for (Timeline timeline : timelines) {
                        String timelineName = timeline.timelinename;
                        timelineNames.add(timelineName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SpecialistEditActivity.this, android.R.layout.simple_spinner_item, timelineNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    TimelineBox.setAdapter(adapter);
                    TimelineBox.setSelection(specialist.timelineid);
                    pols.add("Выберите пол");
                    pols.add("Женский");
                    pols.add("Мужской");
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<>(SpecialistEditActivity.this, android.R.layout.simple_spinner_item, pols);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    SexBox.setAdapter(adapter1);
                    SexBox.setSelection(specialist.sexid);
                }
                @Override
                public void onFailure(Call<List<Timeline>> call, Throwable t) {
                    Log.d("FAILURE", String.valueOf(t));
                }
            });
            runOnUiThread(() -> {
                BirthdateBox.setMaxDate(MaxDate);
                LoginView.setText("@" + specialist.login);
                LoginBox.setText(specialist.login);
            });
        }).start();

        if (specialist.birthdate != null){
            new Thread(() -> {
                Calendar CurrentDate = Calendar.getInstance();
                int CurrentYear = CurrentDate.get(Calendar.YEAR);
                int CurrentMonth = CurrentDate.get(Calendar.MONTH);
                int CurrentDay = CurrentDate.get(Calendar.DAY_OF_MONTH);

                if (specialist.birthdate != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date birthdate;
                    try {
                        birthdate = sdf.parse(specialist.birthdate);
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
                            CalendarLayout.setVisibility(View.GONE);
                            DocLayout.setVisibility(View.GONE);
                            PriceBox.setText(specialist.price);
                            InfoView.setText(specialist.username + " " + specialist.usersurname + ", " + FinalAge + AgeSuffix);
                            FioBox.setText(specialist.usersurname + " " + specialist.username);
                        });
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else {
            TimelineBox.setSelection(0);
            SexBox.setSelection(0);
        }
    }

    public void SavedocOnClick(View view) {
    }

    public void Savedoc2OnClick(View view) {
    }

    public void SaveOnClick(View view) {
        if (LoginBox.length() < 4){
            Toast.makeText(getApplicationContext(), "Слишком короткий логин!", Toast.LENGTH_SHORT).show();
        } else if (!FioBox.getText().toString().contains(" ")) {
            Toast.makeText(getApplicationContext(), "Введите полное ФИО!", Toast.LENGTH_SHORT).show();
        } else if (SexBox.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Выберите пол!", Toast.LENGTH_SHORT).show();
        } else if (TimelineBox.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Выберите часовой пояс!", Toast.LENGTH_SHORT).show();
        } else if (PriceBox.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Введите сумму консультации!", Toast.LENGTH_SHORT).show();
        } else if (PriceBox.getText().toString().equals("0")) {
            Toast.makeText(getApplicationContext(), "Введите корректную сумму консультации!", Toast.LENGTH_SHORT).show();
        } else {
            Saving();
        }
    }

    private void Saving(){
        if (specialist.status.equals("1")){ //существующий аккаунт
            String fio = String.valueOf(FioBox.getText());
            String[] parts = fio.split(" ");
            specialist.usersurname = parts[0];
            specialist.username = parts[1];
            specialist.login = LoginBox.getText().toString();
            specialist.timelineid = TimelineBox.getSelectedItemPosition();
            specialist.price = String.valueOf(PriceBox.getText());

            Call<Void> call = api.updateSpecialist(specialist.id, specialist);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("SUCCESS", "Data updated successfully");
                        Intent intent = new Intent(getApplicationContext(), SpecialistProfileActivity.class);
                        intent.putExtra("ActiveSpecialist", (Serializable) specialist);
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
        }else { //новый аккаунт
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

            String fio = String.valueOf(FioBox.getText());
            String[] parts = fio.split(" ");
            specialist.usersurname = parts[0];
            specialist.username = parts[1];
            specialist.login = LoginBox.getText().toString();
            specialist.timelineid = TimelineBox.getSelectedItemPosition();
            specialist.sexid = SexBox.getSelectedItemPosition();
            specialist.birthdate = selectedDate;
            specialist.price = String.valueOf(PriceBox.getText());
            specialist.graduatuon2 = "0";
            specialist.status = "3";

            Call<Void> call = api.postNewSpecialist(specialist);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("SUCCESS", response.message());
                    //заглушка о том, что аккаунт на рассмотрении

                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("FAIL", t.getMessage());
                }
            });
        }
    }
}