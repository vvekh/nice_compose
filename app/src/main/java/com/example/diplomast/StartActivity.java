package com.example.diplomast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.diplomast.DTO.Client;
import com.example.diplomast.DTO.Specialist;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {
    APIinterface api; Boolean i = true; String separatorr;
    EditText LoginBox, PasswordBox, PasswordBox2;
    Button TopBtn, BottomBtn;
    LinearLayout RegLayout2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        separatorr = getIntent().getStringExtra("KEY");
        api = APIclient.start().create(APIinterface.class);
        LoginBox = findViewById(R.id.login_box);
        PasswordBox = findViewById(R.id.password_box);
        PasswordBox2 = findViewById(R.id.password_box2);
        TopBtn = findViewById(R.id.top_btn);
        BottomBtn = findViewById(R.id.bottom_btn);
        RegLayout2 = findViewById(R.id.reg_layout2);
    }

    public void TopOnClick(View view) {
        String log = String.valueOf(LoginBox.getText());
        String pas = String.valueOf(PasswordBox.getText());
        String pas2 = String.valueOf(PasswordBox2.getText());

        if (i == true){
            if (log.length() < 4){
                //Слишком короткий логин
                Toast.makeText(getApplicationContext(), "Слишком короткий логин!", Toast.LENGTH_SHORT).show();
            } else if (pas.length() < 8){
                //Слишком короткий пароль
                Toast.makeText(getApplicationContext(), "Слишком короткий пароль!", Toast.LENGTH_SHORT).show();
            } else if (!pas.equals(pas2)){
                //Пароли не совпадают
                Toast.makeText(getApplicationContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
            } else {
                Registration(log, pas);
            }
        }else if (i == false){
            Authorization(log, pas);
        }
    }

    public void BottomOnClick(View view) {
        if (i == true){
            RegLayout2.setVisibility(View.GONE);
            TopBtn.setText("Войти");
            BottomBtn.setText("Зарегистрироваться");
            i = false;
        } else if (i == false){
            RegLayout2.setVisibility(View.VISIBLE);
            TopBtn.setText("Зарегистрироваться");
            BottomBtn.setText("Войти");
            i = true;
        }
    }

    private void Registration(String login, String password){
        if ("Клиент".equals(separatorr)){
            Client newclient = new Client();
            newclient.login = login;
            newclient.password = password;
            Intent intent = new Intent(getApplicationContext(), ClientEditActivity.class);
            intent.putExtra("ActiveClient", (Serializable) newclient);
            startActivity(intent);
        } else if ("Специалист".equals(separatorr)){
            Specialist newspecialist = new Specialist();
            newspecialist.login = login;
            newspecialist.password = password;
            Intent intent = new Intent(getApplicationContext(), SpecialistEditActivity.class);
            intent.putExtra("ActiveSpecialist", (Serializable) newspecialist);
            startActivity(intent);
        }
    }

    private void Authorization(String login, String password){
        if ("Клиент".equals(separatorr)){
            Call<Client> call = api.getClientByLoginAndPassword(login, password);
            call.enqueue(new Callback<Client>() {
                @Override
                public void onResponse(Call<Client> call, Response<Client> response) {
                    if (response.isSuccessful()){
                        Client tempclient = response.body();
                        Intent intent = new Intent(getApplicationContext(), ClientProfileActivity.class);
                        intent.putExtra("ActiveClient", (Serializable) tempclient);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                        Log.d("BAD RESPONSE", String.valueOf(response.body()));
                    }
                }
                @Override
                public void onFailure(Call<Client> call, Throwable t) {
                    Log.d("FAILURE", String.valueOf(t));
                }
            });
        } else if ("Специалист".equals(separatorr)){
            Call<Specialist> call = api.getSpecialistByLoginAndPassword(login, password);
            call.enqueue(new Callback<Specialist>() {
                @Override
                public void onResponse(Call<Specialist> call, Response<Specialist> response) {
                    if (response.isSuccessful()){
                        Specialist specialist = response.body();
                        Intent intent = new Intent(getApplicationContext(), SpecialistProfileActivity.class);
                        intent.putExtra("ActiveSpecialist", (Serializable) specialist);
                        startActivity(intent);
                    }else {
                        Toast.makeText(getApplicationContext(), "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                        Log.d("BAD RESPONSE", String.valueOf(response.body()));
                    }
                }
                @Override
                public void onFailure(Call<Specialist> call, Throwable t) {
                    Log.d("FAILURE", String.valueOf(t));
                }
            });
        }
    }
}