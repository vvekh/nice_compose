package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomast.DTO.Specialist;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialistEditActivity extends AppCompatActivity {
    APIinterface api; Specialist specialist; String blank_id;
    TextView LoginView, InfoView, EmailView, PhoneView;
    LinearLayout CalendarLayout, DocLayout;
    EditText FioBox, LoginBox, PriceBox;
    DatePicker BirthdateBox; Spinner TimelineBox, SexBox;
    Button SavedocBtn, SavedocBtn2, SaveBtn;
    private static final int REQUEST_CODE_DOCUMENT1 = 1;
    private static final int REQUEST_CODE_DOCUMENT2 = 2;

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
        EmailView = findViewById(R.id.email_box);
        PhoneView = findViewById(R.id.phone_box);
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
                            PriceBox.setText(specialist.price);
                            InfoView.setText(specialist.username + " " + specialist.usersurname + ", " + FinalAge + AgeSuffix);
                            FioBox.setText(specialist.usersurname + " " + specialist.username);
                            EmailView.setText(specialist.email);
                            PhoneView.setText(specialist.phone);
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

        loadFields(specialist);
    }

    private void loadFields(Specialist spec){
        if ("1".equals(spec.status)){
            DocLayout.setVisibility(View.GONE);
        }
    }

    public void SavedocOnClick(View view) { // Сохранение документа об образовании
        // Создаем Intent для выбора файла
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf"); // Указываем тип файлов PDF
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Запускаем Intent для выбора файла
        startActivityForResult(Intent.createChooser(intent, "Select a PDF file"), 1);
    }

    public void Savedoc2OnClick(View view) { // Сохранение документа о дополнительном образовании

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Получаем URI выбранного файла
            Uri selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                String filePath = getPathFromUri(selectedFileUri);
                if (filePath != null) {
                    File file = new File(filePath);

                    // Создаем RequestBody для отправки файла
                    RequestBody requestFile = RequestBody.create(MediaType.parse("application/pdf"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("pdfFile", file.getName(), requestFile);

                    // Вызываем метод API для загрузки PDF-файла
                    uploadPdf1(body, specialist.id);
                } else {
                    // Обработка ошибки получения пути к файлу
                }
            } else {
                // Обработка ошибки получения URI файла
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String filePath = null;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }

        return filePath;
    }

    private void uploadPdf1(MultipartBody.Part pdfFile, int specialistId) {
        Call<String> call = api.uploadPdf1(pdfFile, specialistId);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String pdfPath = response.body();
                    // Обработка успешной загрузки
                } else {
                    // Обработка ошибки загрузки
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Обработка ошибки сети
            }
        });
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
        } else if (EmailView.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Введите адрес почты!", Toast.LENGTH_SHORT).show();
        } else if (PhoneView.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Введите номер телефона!", Toast.LENGTH_SHORT).show();
        } else if (!isValidEmail(EmailView.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Введите корректный адрес почты!", Toast.LENGTH_SHORT).show();
        } else if (!isValidPhone(PhoneView.getText().toString())){
            Toast.makeText(getApplicationContext(), "Введите корректный номер телефона!", Toast.LENGTH_SHORT).show();
        }
        else {
            Saving();
        }
    } //Кнопка сохранения пользователя в соответствии с условиями

    private void Saving(){
        if (specialist.status.equals("1")){ //существующий аккаунт
            String fio = String.valueOf(FioBox.getText());
            String[] parts = fio.split(" ");
            specialist.usersurname = parts[0];
            specialist.username = parts[1];
            specialist.email = EmailView.getText().toString();
            specialist.phone = PhoneView.getText().toString();
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
        } else if (specialist.status.equals("0")) {
            String fio = String.valueOf(FioBox.getText());
            String[] parts = fio.split(" ");
            specialist.usersurname = parts[0];
            specialist.username = parts[1];
            specialist.email = EmailView.getText().toString();
            specialist.phone = PhoneView.getText().toString();
            specialist.login = LoginBox.getText().toString();
            specialist.timelineid = TimelineBox.getSelectedItemPosition();
            specialist.price = String.valueOf(PriceBox.getText());

            Call<Void> call = api.update2Specialist(specialist.id, specialist);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("SUCCESS", "Data updated successfully");

                        blank_id = "3";
                        Intent intent = new Intent(getApplicationContext(), BlankActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("blank_id", blank_id);
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
        } else { //новый аккаунт
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
            specialist.email = EmailView.getText().toString();
            specialist.phone = PhoneView.getText().toString();
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

                    blank_id = "3";
                    Intent intent = new Intent(getApplicationContext(), BlankActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("blank_id", blank_id);
                    startActivity(intent);

                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("FAIL", t.getMessage());
                }
            });
        }
    } //Функция сохранения пользователя

    public static boolean isValidEmail(String email) {
        String pattern = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(email);
        return m.matches();
    }
    public static boolean isValidPhone(String phoneNumber) {
        String pattern = "^8\\d{10}$";
        return phoneNumber.matches(pattern);
    }

}